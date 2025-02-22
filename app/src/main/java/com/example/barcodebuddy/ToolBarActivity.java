package com.example.barcodebuddy;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ToolBarActivity extends AppCompatActivity {
    Toolbar toolbarr;
    TextView toolbarTitle;
    ImageView toolbarBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_bar);

        toolbarr = findViewById(R.id.toolbarr);
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarBack = findViewById(R.id.toolbar_back);

        setSupportActionBar(toolbarr);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Handle back button click
        toolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    // Function to set toolbar title dynamically
    public void setToolbarTitle(String title) {
        if (toolbarTitle != null) {
            toolbarTitle.setText(title);
        }
    }

    // Function to show/hide the back button
    public void showBackButton(boolean show) {
        if (toolbarBack != null) {
            toolbarBack.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed(); // This will go back to the previous activity
        finish(); // Finish current activity
    }
}
