package com.rishabh.enqueueme;

/**
 * Created by rohanagarwal94 on 29/12/16.
 */
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.Call;
import retrofit.http.Url;

public interface QueueApi {
    @GET("user/getQueue/{userid}")
    Call<Queues> getQueues(@Path("userid") String userId);
}