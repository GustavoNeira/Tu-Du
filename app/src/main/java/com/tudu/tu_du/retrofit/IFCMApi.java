package com.tudu.tu_du.retrofit;

import com.tudu.tu_du.models.FCMBody;
import com.tudu.tu_du.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {
    @Headers({
       "Content-Type:application/json",
        "Authorization:key=AAAAX4mW9Mc:APA91bHITlIqDGnVvpeQWRDCAp-Vv0XlCDn1MsCBDsNkFAqZ4ZgWSo5KwelUnnL-ZUtEDp4Hjqdo_cvmUHCX8yFx6OAVLG6Ptul10NpaZkfFr5fMmeiPegMGs3HvtPwd5A4B6C-I1FIk"
    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);
}
