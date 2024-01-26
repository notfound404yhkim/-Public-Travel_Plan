package com.example.travelapp.api;

import com.example.travelapp.model.History;
import com.example.travelapp.model.PlaceList;
import com.example.travelapp.model.Res;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface PlaceApi {


    // 지역 축제 이미지 가져오기
    // 파리미터는 쿼리로 처리
    @POST("/place")
    Call<PlaceList> getImg(
                            @Query("region") String region,
                           @Query("option") int option,
                           @Query("offset") int offset,
                           @Query("limit") int limit);
}
