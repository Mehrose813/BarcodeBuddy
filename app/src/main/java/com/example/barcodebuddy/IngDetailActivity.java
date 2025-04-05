package com.example.barcodebuddy;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView; // Import Lottie

public class IngDetailActivity extends AppCompatActivity {

    TextView title;
    TextView detail;
    LottieAnimationView lottieAnimation; // Lottie animation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ing_detail);

        title = findViewById(R.id.title);
        detail = findViewById(R.id.details);
        lottieAnimation = findViewById(R.id.lottie_animation); // Initialize Lottie

        int color = getIntent().getIntExtra("color", R.color.black);
        String name = getIntent().getStringExtra("name");

        if (color != 0) {
            title.setTextColor(getResources().getColor(color, null));
        }

        if (name != null) {
            title.setText(name);

            // Show Lottie animation and hide details
            lottieAnimation.setVisibility(View.VISIBLE);
            detail.setVisibility(View.GONE);
            title.setVisibility(View.GONE);

            // Delay fetching data for 3 seconds (to show Lottie animation)
            new Handler().postDelayed(() -> {
                APIUtill.fetchIngInfo(this, name, new APIUtill.OnFetchCompleteListener() {
                    @Override
                    public void onSuccess(String response) {
                        runOnUiThread(() -> {
                            // Hide Lottie animation and show the data
                            lottieAnimation.setVisibility(View.GONE);
                            detail.setVisibility(View.VISIBLE);
                            title.setVisibility(View.VISIBLE);
                            detail.setText(formatResponse(response));
                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        runOnUiThread(() -> {
                            lottieAnimation.setVisibility(View.GONE);
                            detail.setVisibility(View.VISIBLE);
                            title.setVisibility(View.VISIBLE);
                            detail.setText("Sorry...We are unable to load data");
                        });
                    }
                });
            }, 4000); // 4 seconds delay
        }
    }

    private CharSequence formatResponse(String response) {
        return response.replace("**", ""); // Simply removes '**' without modifying anything else
    }
}
