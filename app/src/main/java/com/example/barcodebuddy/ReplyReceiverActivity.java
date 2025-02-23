package com.example.barcodebuddy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ReplyReceiverActivity extends ToolBarActivity {

    private TextView tvUserEmail, tvMessage;
    private EditText etReply;
    private Button btnSendReply;
    private DatabaseReference databaseReference;
    private String userEmail, barcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
     //   setContentView(R.layout.activity_reply_receiver);

        getLayoutInflater().inflate(R.layout.activity_reply_receiver, findViewById(R.id.container));

        setToolbarTitle("Reply User");
        showBackButton(true);
        // Initialize UI elements
        tvUserEmail = findViewById(R.id.tv_user_email);
        tvMessage = findViewById(R.id.tvMessage);
        etReply = findViewById(R.id.etReply);
        btnSendReply = findViewById(R.id.btnSendReply);

       String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("UserNotifications");

        // Get intent data
        if (getIntent() != null) {
            userEmail = getIntent().getStringExtra("userEmail");
            barcode = getIntent().getStringExtra("barcode");

            tvUserEmail.setText("From: " + userEmail);
            tvMessage.setText("Product with barcode " + barcode + " is missing.");
        }

        // Send reply to Firebase
        btnSendReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String replyMessage = etReply.getText().toString().trim();
                if (!replyMessage.isEmpty()) {
                    sendReply(replyMessage);
                    MyUtilClass.sendNotificationToUser(ReplyReceiverActivity.this,id,"Reply from admin",replyMessage);
                } else {
                    Toast.makeText(ReplyReceiverActivity.this, "Please enter a reply", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendReply(String replyMessage) {
        String key = databaseReference.push().getKey();

        // Create Reply object
        Reply reply = new Reply(userEmail, barcode, replyMessage);

        // Save object directly to Firebase
        databaseReference.child(key).setValue(reply)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ReplyReceiverActivity.this, "Reply Sent!", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity after sending reply
                })
                .addOnFailureListener(e -> Toast.makeText(ReplyReceiverActivity.this, "Failed to send reply", Toast.LENGTH_SHORT).show());
    }
}
