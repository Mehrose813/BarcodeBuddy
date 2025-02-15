package com.example.barcodebuddy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class ScanBarcodeActivity extends AppCompatActivity {
    private ActivityResultLauncher<ScanOptions> barLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scan_barcode);

        
        barLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() != null) {
                String scannedBarcode = result.getContents();
                searchProductByBarcode(scannedBarcode);
            }
            else{
                finish();
            }
        });

        scanCode();
    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scanning...");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(ScanBarcode.class);
        barLauncher.launch(options);
    }

    // Search product in Firebase by barcode
    private void searchProductByBarcode(String barcode) {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("Products");
        productsRef.orderByChild("barcode").equalTo(barcode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                        String name = productSnapshot.child("name").getValue(String.class);
                        String category = productSnapshot.child("cat").getValue(String.class);
                        String desc = productSnapshot.child("desc").getValue(String.class);
                        String productKey = productSnapshot.getKey();
                        String healthy = productSnapshot.child("healthy").getValue(String.class);

                        // Pass data to ProductDisplayActivity
                        Intent intent = new Intent(ScanBarcodeActivity.this, ProductDisplayActivity.class);
                        intent.putExtra("name", name);
                        intent.putExtra("cat", category);
                        intent.putExtra("desc", desc);
                        intent.putExtra("healthy", healthy);
                        intent.putExtra("productKey", productKey);
                        startActivity(intent);
                    }
                    finish();
                } else {
                    // If product not found, save missing barcode in Firebase
                    DatabaseReference missingRef = FirebaseDatabase.getInstance().getReference("MissingProducts");
                    String missingId = missingRef.push().getKey();
                    if (missingId != null) {
                        String userEmail = "";
                        String userId="";
                        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                            userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        }
                        MissingProduct missingProduct = new MissingProduct(barcode, userEmail , userId);
                        missingRef.child(missingId).setValue(missingProduct);
                    }
                    showAlert("No product found for this barcode.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showAlert("Error: " + error.getMessage());
            }
        });
    }

    // Show alert dialog
    private void showAlert(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Scan Result")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }


        })
                .create()
                .show();
    }
}