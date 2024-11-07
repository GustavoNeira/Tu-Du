package com.tudu.tu_du.providers;

import com.tudu.tu_du.models.FCMBody;
import com.tudu.tu_du.models.FCMResponse;
import com.tudu.tu_du.retrofit.IFCMApi;
import com.tudu.tu_du.retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Retrofit;

public class NotificationProvider {
    private String url = "https://fcm.googleapis.com";

    public NotificationProvider() {

    }
    public Call<FCMResponse> sendNotification(FCMBody body) {
        return RetrofitClient.getClient(url).create(IFCMApi.class).send(body);
    }
}
