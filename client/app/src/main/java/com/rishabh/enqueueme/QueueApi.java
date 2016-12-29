package com.rishabh.enqueueme;

/**
 * Created by rohanagarwal94 on 29/12/16.
 */
import retrofit.Callback;
import retrofit.Response;
import retrofit.http.Field;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.Call;
import retrofit.http.Url;

public interface QueueApi {
    @GET("user/getQueue/{userid}")
    Call<Queues> getQueues(@Path("userid") String userId);

    @POST("user/addQueue")
    public void insertUser(
            @Field("devId") String becId,
            @Field("beconId") String beconId,
            Callback<Response> callback);


}