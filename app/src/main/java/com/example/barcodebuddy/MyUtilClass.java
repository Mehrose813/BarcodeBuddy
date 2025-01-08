package com.example.barcodebuddy;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class MyUtilClass {
    public static String imageUriToBase64(Uri imageuri, ContentResolver contentResolver) {

        try {
            InputStream inputStream = contentResolver.openInputStream(imageuri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static Bitmap base64ToBitmap(String base64String)
    {
        try {
            byte[] imagBytes=Base64.decode(base64String,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(imagBytes,0,imagBytes.length);
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

}

