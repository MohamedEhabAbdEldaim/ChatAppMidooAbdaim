package com.midooabdaim.midooabdaimchat.data.api;

import com.midooabdaim.midooabdaimchat.data.model.MyResponse;
import com.midooabdaim.midooabdaimchat.data.model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAD5wm3H8:APA91bFNEdavdrfVqMrXAqlVMOBuMCj-G9Ug5EvBbNrTT5CRwWsUH1vkQQoAyy9bJoHghnor5xF252UkPSJdOyZVkTTjOz5-uZ9hK1zZ8xA4MpM5sWYL4DYeMpZ74T7X7QdUmM5Dwk2M"

            }
    )


    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
