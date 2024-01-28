package com.example.travelapp.api;

import com.example.travelapp.model.PostingList;
import com.example.travelapp.model.Res;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface PostingApi {

    // 포스팅 생성 API
    //이미지 는 MultpartBody로 처리
    //form-data는 part 처리
    @Multipart

    @POST("/posting")
    Call<Res> addPosting(@Header("Authorization") String token,
                         @Part MultipartBody.Part image,
                         @Part("content") RequestBody content);

    //친구 포스팅 조회 API
    @GET("/posting")
    Call<PostingList> getFriendPosting(@Header("Authorization") String token,
                                       @Query("offset") int offset,
                                       @Query("limit") int limit);


    // 내 포스팅 조회 API

    @GET("/posting/me")
    Call<PostingList> getMyPosting(@Header("Authorization") String token,
                                   @Query("offset") int offset,
                                   @Query("limit") int limit);
}
