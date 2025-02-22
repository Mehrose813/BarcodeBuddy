package com.example.barcodebuddy;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminMainActivity extends AppCompatActivity {
BottomNavigationView navAdmin;
Toolbar toolbar;
LinearLayout container;
DatabaseReference missingProductsRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_main);

        toolbar = findViewById(R.id.toolbar_admin);
        setSupportActionBar(toolbar);
        missingProductsRef = FirebaseDatabase.getInstance().getReference("MissingProducts");

        // Listen for new missing products
        missingProductsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
//                String barcode = snapshot.getValue(String.class);
//                if (barcode != null) {
//                    // Send a notification to admin device
//                    MyUtilClass.showNotification(AdminMainActivity.this,
//                            "New Missing Product",
//                            "Product with barcode " + barcode + " is missing.");
//                }

                // Retrieve the MissingProduct object
                MissingProduct missing = snapshot.getValue(MissingProduct.class);
                if (missing != null) {
                    String title = "New Message from " + missing.getUserEmail();
                    String body = "Product with barcode " + missing.getBarcode() + " is missing.";
                    MyUtilClass.showNotification(AdminMainActivity.this, title, body);

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) { }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        navAdmin = findViewById(R.id.nav_admin);
        container = findViewById(R.id.container);


        loadFragment(new AdminHomeFragment() , "Barcode Buddy");

        navAdmin.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()== R.id.home_admin){
                    loadFragment(new AdminHomeFragment() , "Barcode Buddy");
                }
                else if(item.getItemId() == R.id.status_admin){
                    loadFragment(new AdminStatusFragment() ,"Add");
                }
                else if(item.getItemId() == R.id.notification_admin){
                    loadFragment(new AdminNotificationFragment(),"Notifications");
                }
                else{
                    loadFragment(new AdminProfileFragment(),"Profile");
                }

                return true;
            }
        });


    }
    public void loadFragment(Fragment fragment , String title){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container , fragment).commit();

        if (toolbar != null) {
            toolbar.setTitle(title);
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
            toolbar.setBackgroundColor(getResources().getColor(R.color.light_green));
        }
    }
}