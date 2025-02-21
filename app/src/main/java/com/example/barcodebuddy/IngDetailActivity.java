package com.example.barcodebuddy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class IngDetailActivity extends AppCompatActivity {

    TextView title;
    TextView detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ing_detail);

        title=findViewById(R.id.title);
        detail=findViewById(R.id.details);

        int color = getIntent().getIntExtra("color", R.color.black);//default if no color provided
        String name = getIntent().getStringExtra("name");

        if (color != 0) {
            title.setTextColor(getResources().getColor(color, null));
        }

        if(name != null){
            title.setText(name);

            APIUtill.fetchIngInfo(this, name, new APIUtill.OnFetchCompleteListener() {
                @Override
                public void onSuccess(String response) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            detail.setText(response);
                        }
                    });
                }

                @Override
                public void onFailure(String error) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            detail.setText("Sorry...We are unable to load data");
                            Toast.makeText(IngDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }


    }
}