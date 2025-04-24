package com.example.barcodebuddy;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import androidx.core.app.RemoteInput;


import androidx.core.app.NotificationCompat;

import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;

public class MyUtilClass {

    // Convert image URI to Base64 String
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

    // Convert Base64 String to Bitmap
    public static Bitmap base64ToBitmap(String base64String) {
        try {
            byte[] imageBytes = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void showNotification(Context context , String title,String body){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String Notification_Channel_Id ="com.example.barcodebuddy";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(Notification_Channel_Id,
                    "Notification",NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription(body);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context,Notification_Channel_Id);
        notificationBuilder.setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setWhen(new Date().getTime())
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(body);

        Intent notificationIntent = new Intent(context, AdminMainActivity.class);
        //notificationIntent.putExtra("open",AdminNotificationFragment.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
        | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        int id = 565;

        Intent[] intentArray= new Intent[]{notificationIntent};
        PendingIntent pendingIntent = PendingIntent.getActivities(context,id,intentArray, PendingIntent.FLAG_IMMUTABLE);
        notificationBuilder.setContentIntent(pendingIntent);

        NotificationCompat.BigTextStyle byStyle = new NotificationCompat.BigTextStyle();
        byStyle.bigText(body);
        notificationBuilder.setStyle(byStyle);
        notificationManager.notify(id, notificationBuilder.build());
    }

//    public static void sendNotificationToUser(Context context, String userId, String title, String message) {
//        NotificationManager notificationManager = (NotificationManager)
//                context.getSystemService(Context.NOTIFICATION_SERVICE);
//        String channelId = "user_notification_channel";
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(channelId, "User Notifications", NotificationManager.IMPORTANCE_HIGH);
//            channel.setDescription("Notifications for user replies");
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
//                .setAutoCancel(true)
//                .setSmallIcon(R.drawable.logo) // make sure this icon exists in your drawable folder
//                .setContentTitle(title)
//                .setContentText(message)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setWhen(new Date().getTime());
//
//        // When tapped, open the user main screen (adjust as needed)
//        Intent intent = new Intent(context, HomeActivity.class);
//        intent.putExtra("openFragment", "UserNotificationFragment");
//        PendingIntent pendingIntent = PendingIntent.getActivity(
//                context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
//        builder.setContentIntent(pendingIntent);
//
//        notificationManager.notify(1001, builder.build());
//    }
public static void sendNotificationToUser(Context context, String userId, String title, String message) {
    NotificationManager notificationManager = (NotificationManager)
            context.getSystemService(Context.NOTIFICATION_SERVICE);
    String channelId = "user_notification_channel";

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationChannel channel = new NotificationChannel(channelId, "User Notifications", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Notifications for user replies");
        notificationManager.createNotificationChannel(channel);
    }

    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.logo) // make sure this icon exists in your drawable folder
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setWhen(new Date().getTime());

    // When tapped, open the user main screen (adjust as needed)
    Intent intent = new Intent(context, HomeActivity.class);
    intent.putExtra("openFragment", "UserNotificationFragment");
    PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
    builder.setContentIntent(pendingIntent);

    notificationManager.notify(1001, builder.build());
}
}
