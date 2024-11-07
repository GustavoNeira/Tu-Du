package com.tudu.tu_du.service;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tudu.tu_du.channel.NotificationHelper;
import com.tudu.tu_du.models.Mensajes;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingClient extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {

        super.onMessageReceived(message);
        Map<String, String> data = message.getData();
        String title = data.get("title");
        String body = data.get("body");


        if (title != null){
            if (title.equals("Nuevo Mensaje")){

                showNotificationmensaje(data);

            }else {
            showNotification(title, body);
        }
        }
    }

    private void showNotification(String title, String body){
        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder = notificationHelper.getNotification(title, body);
        Random random = new Random();
        int n = random.nextInt(10000);
        notificationHelper.getManager().notify(n,builder.build());
    }
    private void showNotificationmensaje(Map<String, String>data ){
        String title  = data.get("title");
        String body  = data.get("body");
        String usernameEmisor  = data.get("usernameEmisor");
        String usernameReceptor  = data.get("usernameReceptor");
        String ultimoMensaje  = data.get("ultimoMensaje");
        String mensajeJSON  = data.get("messages");
        String imagenEmisor  = data.get("imagenEmisor");
        String imagenReceptor  = data.get("imagenReceptor");
        int idNotificacion = Integer.parseInt(data.get("idNotificacion"));
        Gson gson = new Gson();
        Mensajes[] mensajes = gson.fromJson(mensajeJSON,Mensajes[].class);
        new Handler(Looper.getMainLooper())
                .post(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.with(getApplicationContext())
                             .load(imagenEmisor)
                             .into(new Target() {
                                 @Override
                                 public void onBitmapLoaded(Bitmap bitmapEmisor, Picasso.LoadedFrom from) {
                                     Picasso.with(getApplicationContext())
                                             .load(imagenReceptor)
                                             .into(new Target() {
                                         @Override
                                         public void onBitmapLoaded(Bitmap bitmapReceptor, Picasso.LoadedFrom from) {
                                             NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
                                             NotificationCompat.Builder builder =
                                                     notificationHelper.getNotificacionMensajes(
                                                             mensajes,
                                                             usernameEmisor,
                                                             usernameReceptor,
                                                             ultimoMensaje,
                                                             bitmapEmisor,
                                                             bitmapReceptor
                                                     );

                                             notificationHelper.getManager().notify(idNotificacion,builder.build());
                                         }

                                         @Override
                                         public void onBitmapFailed(Drawable errorDrawable) {

                                         }

                                         @Override
                                         public void onPrepareLoad(Drawable placeHolderDrawable) {

                                         }
                                     });
                                 }

                                 @Override
                                 public void onBitmapFailed(Drawable errorDrawable) {

                                 }

                                 @Override
                                 public void onPrepareLoad(Drawable placeHolderDrawable) {

                                 }
                             });
                    }
                });


    }


}
