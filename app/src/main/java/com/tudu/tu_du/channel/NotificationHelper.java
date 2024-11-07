package com.tudu.tu_du.channel;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;

import com.tudu.tu_du.R;
import com.tudu.tu_du.models.Mensajes;

import java.util.Date;

public class NotificationHelper extends ContextWrapper {
    private static final  String CHANNEL_ID = "com.tudu.tu_du";
    private static final  String CHANNEL_NAME = "Tu_du";

    private NotificationManager manager;

    public NotificationHelper(Context context) {
        super(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CreateChannels();
        }
    }


    @TargetApi(Build.VERSION_CODES.O)
    private void CreateChannels(){
        NotificationChannel notificationChannel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
        );
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLightColor(Color.GRAY);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(notificationChannel);



    }
    public NotificationManager getManager(){
        if (manager == null){
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    public NotificationCompat.Builder getNotification(String title,String body ){
        return new NotificationCompat.Builder(
                getApplicationContext(),
                CHANNEL_ID
        ).setContentTitle(title)
         .setContentText(body)
         .setAutoCancel(true)
         .setColor(Color.GRAY)
         .setSmallIcon(R.mipmap.ic_tu_du_round)
         .setStyle(new NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title));
    }
    public NotificationCompat.Builder getNotificacionMensajes(
            Mensajes[] mensajes, String usernameEmisor,
            String usernameReceptor,
            String ultimoMensaje,
            Bitmap bitmapEmisor,
            Bitmap bitmapReceptor ) {
        Person person1 = new Person.Builder()
                .setName("Tú")
                .setIcon(IconCompat.createWithBitmap(bitmapReceptor))
                .build();

        Person person2 = new Person.Builder()
                .setName(usernameEmisor)
                .setIcon(IconCompat.createWithBitmap(bitmapEmisor))
                .build();

        NotificationCompat.MessagingStyle messageStyle = new  NotificationCompat.MessagingStyle(person1);
        NotificationCompat.MessagingStyle.Message message1 = new
                NotificationCompat.MessagingStyle.Message(
                        ultimoMensaje,
                        new Date().getTime(),
                        person1

                );
        messageStyle.addMessage(message1);

        for (Mensajes m:mensajes) {

            NotificationCompat.MessagingStyle.Message message2 = new
                    NotificationCompat.MessagingStyle.Message(
                    m.getMensaje(),
                    m.getTimestamp(),
                    person2);
            messageStyle.addMessage(message2);
        }
        return new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_tu_du)
                .setStyle(messageStyle);



    }


}
