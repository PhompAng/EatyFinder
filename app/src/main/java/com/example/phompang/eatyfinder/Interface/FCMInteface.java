package com.example.phompang.eatyfinder.Interface;

import com.example.phompang.eatyfinder.model.Notification;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by phompang on 12/5/2016 AD.
 */

public interface FCMInteface {
    @Headers({
            "Authorization: key=AAAA78-8P7M:APA91bFbcDJ8RLdh4xl4kDUWnNwDMAwpN5i2t5dI8nt48nF8iL_eMr9i7_rZfRCGbltziFQberCd_u_bK5kykaRd0VsRbdIwYwNHJkCUYQRKrUWUybCZPQA24VZivrIHx8wKzCRx72XoWUbHJvR7ugCkripSQlIiYA",
            "Content-Type: application/json"
    })
    @POST("fcm/send")
    Call<ResponseBody> sendNoti(@Body Notification body);
}
