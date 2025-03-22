package com.example.barcodebuddy;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import okhttp3.*;

public class APIUtill {

    private static final String API_KEY = "AIzaSyBKQL9NDU5neKfTkb1tt97O4XlQFUxtsY0";
    private static final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("ingredients_info");

    public interface OnFetchCompleteListener {
        void onSuccess(String response);
        void onFailure(String error);
    }

    public static void fetchIngInfo(Context context, String name, OnFetchCompleteListener listener) {

        dbRef.child(name.toLowerCase()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String cachedData = snapshot.getValue(String.class);
                    listener.onSuccess(cachedData);
                } else {
                    callAPI(context, name, listener);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                listener.onFailure("Firebase Error: " + error.getMessage());
            }
        });
    }

    private static void callAPI(Context context, String name, OnFetchCompleteListener listener) {
        OkHttpClient client = new OkHttpClient();

        String jsonRequest = "{\"system_instruction\": { \"parts\": { \"text\": \"You are an expert AI in nutrition and health. Your job is to provide detailed information about ingredients when a user asks. For each ingredient, generate a structured response with the following details: Introduction, Uses, Base Ingredients, Uses in Food, Health Effects (Diabetic Patients, Heart Patients, Pregnant Women), and Conclusion. Your response should be informative, fact-based, and easy to understand.\" } }, \"contents\": [ { \"parts\": [ { \"text\": \"Tell me about " + name + "\" } ] } ], \"generationConfig\": { \"temperature\": 0.1, \"topP\": 0.1, \"topK\": 10 } }";

        RequestBody body = RequestBody.create(jsonRequest, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(context, "Error fetching info", Toast.LENGTH_SHORT).show();
                });
                listener.onFailure("Error fetching info.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (!response.isSuccessful()) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(context, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                    });
                    listener.onFailure("Error: " + response.message());
                    return;
                }

                String responseData = response.body().string();
                try {
                    JSONObject jsonResponse = new JSONObject(responseData);
                    JSONArray contents = jsonResponse.getJSONArray("candidates")
                            .getJSONObject(0)
                            .getJSONObject("content")
                            .getJSONArray("parts");

                    String info = contents.getJSONObject(0).getString("text");

                    // Replace **text** with 👉 text 👈 and make it dark green
                    info = info.replaceAll("\\*\\*(.*?)\\*\\*", "<b><font color='#006400'>👉 $1 👈</font></b>");

                    // Remove ##
                    info = info.replaceAll("##", "");

                    dbRef.child(name.toLowerCase()).setValue(info);

                    listener.onSuccess(info);
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onFailure("Error parsing information.");
                }
            }
        });
    }
}
