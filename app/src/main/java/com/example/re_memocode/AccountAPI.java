package com.example.re_memocode;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AccountAPI {
    @FormUrlEncoded
    @POST("signup.php")
    Call<JsonObject> register(
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("login.php")
    Call<JsonObject> login(
            @Field("username") String username,
            @Field("password") String password

    );

    @FormUrlEncoded
    @POST("saveprogress.php")
    Call<JsonObject> saveProgress(
            @Field("user_id") int user_id,
            @Field("level_id") int level_id,
            @Field("status") String status
    );

    @FormUrlEncoded
    @POST("getprogress.php")
    Call<JsonObject> getProgress(
            @Field("user_id") int user_id
    );

    @FormUrlEncoded
    @POST("savelevelprogress.php")
    Call<JsonObject> saveLevelProgress(
            @Field("user_id") int user_id,
            @Field("level_id") int level_id,
            @Field("score") double score,
            @Field("stars") int stars,
            @Field("completion_time") double completedTime
    );

    // New method to get all level progress, ranked by score and completion time
    @FormUrlEncoded
    @POST("getlevelprogress.php")
    Call<JsonObject> getLevelProgress(
            @Field("level_id") int level_id
    );
}
