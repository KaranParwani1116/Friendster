package com.example.friendster;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.friendster.activity.MainActivity;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Map;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FirebaseMessagingServic";
    private static final String ChannelId="Friend_Request";
    private static final int notification_id=1134;
    private static final int Gotoreq = 3417;
    String Notificationtitle="";
    String NotificationBody="";
    String ImageUrl="";

    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            sendNotification(bitmap);
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };
    
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.i(TAG, "onMessageReceived:");


        /*if(remoteMessage.getNotification()!=null){
           Notificationtitle = remoteMessage.getNotification().getTitle();
           NotificationBody = remoteMessage.getNotification().getBody();
        }*/


        if(remoteMessage.getData()!=null)
        {
            Log.i(TAG, "Data not null");
            
            getImage(remoteMessage);
        }

    }

    private void getImage(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        Notificationtitle = data.get("title");
        NotificationBody = data.get("content");
        ImageUrl = data.get("ImageUrl");

        if(remoteMessage.getData()!=null)
        {
            Handler uihandler = new Handler(Looper.getMainLooper());
            uihandler.post(new Runnable() {
                @Override
                public void run() {
                    Picasso.get().load(ImageUrl).into(target);
                }
            });
        }
    }

    private void sendNotification(Bitmap bitmap){

        NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel mchannel=new NotificationChannel(ChannelId,"Primary",NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mchannel);
        }

        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,ChannelId)
                .setColor(ContextCompat.getColor(this,R.color.colorPrimary))
                .setContentTitle(Notificationtitle)
                .setContentText(NotificationBody)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(bitmap)
                .setContentIntent(reqfragment(this));

        notificationManager.notify(notification_id, builder.build());

    }

    private PendingIntent reqfragment(Context context)
    {
        Intent startintent=new Intent(this, MainActivity.class);
        startintent.putExtra("isFromNotification","true");
        startintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(context,Gotoreq,startintent,PendingIntent.FLAG_UPDATE_CURRENT);

    }
}
