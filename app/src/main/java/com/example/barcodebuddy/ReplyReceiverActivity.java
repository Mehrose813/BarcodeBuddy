package com.example.barcodebuddy;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.RemoteInput;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ReplyReceiverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reply_receiver);

        // Get the remote input bundle from the intent
        Bundle remoteInputBundle = RemoteInput.getResultsFromIntent(getIntent());
        if (remoteInputBundle == null) {
            // No reply was provided
            finish();
            return;
        }

        // Retrieve the reply text using the same key you set in your notification ("reply_key")
        CharSequence replyText = remoteInputBundle.getCharSequence("reply_key");
        if (replyText == null) {
            // No reply text provided
            finish();
            return;
        }

        // Get the target user ID from the intent extras (if not provided, use a default)
        String targetUserId = getIntent().getStringExtra("targetUserId");
        if (targetUserId == null) {
            targetUserId = "defaultUserId";
        }

        // Get a reference to the Firebase node for this user's replies
        DatabaseReference replyRef = FirebaseDatabase.getInstance()
                .getReference("UserReplies").child(targetUserId);

        // Create a unique key for the reply and store the reply text directly as a string
        String replyId = replyRef.push().getKey();
        if (replyId != null) {
            replyRef.child(replyId).setValue(replyText.toString());
        }

        // Finish the activity after processing the reply
        finish();

//        Bundle bundle = RemoteInput.getResultsFromIntent(getIntent());
//        if (bundle == null) {
//            // No reply text provided; finish the activity.
//            finish();
//            return;
//        }
//        CharSequence charSequence = bundle.getCharSequence("reply_key");
//        if (charSequence == null) {
//            // No reply text provided; finish the activity.
//            finish();
//            return;
//        }
//
//        String targetUserId = getIntent().getStringExtra("targetUserId");
//        if (targetUserId == null) {
//            targetUserId = "defaultUserId";
//        }
//
//        DatabaseReference replyRef = FirebaseDatabase.getInstance()
//                .getReference("UserReplies").child(targetUserId);
//        String replyId = replyRef.push().getKey();
//        if (replyId != null) {
//            Map<String, Object> replyData = new HashMap<>();
//            assert charSequence != null;
//            replyData.put("reply", charSequence.toString());
//            replyData.put("timestamp", System.currentTimeMillis());
//            // You can add additional information like admin ID if needed
//            replyRef.child(replyId).setValue(replyData);
//        }
//    // Close the activity after processing the reply
//    finish();
    }
}