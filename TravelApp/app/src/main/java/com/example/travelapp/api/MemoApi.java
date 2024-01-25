package com.example.travelapp.api;

import com.example.travelapp.model.Memo;
import com.example.travelapp.model.MemoList;
import com.example.travelapp.model.Res;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MemoApi {

    // 메모생성 API
    @POST("/memo")
    Call<Res> addMemo(@Header("Authorization") String token, @Body Memo memo);

    // 내 메모리스트 가져오는 API
    @GET("/memo")
    Call<MemoList> getMemoList(@Header("Authorization") String token,
                               @Query("offset") int offset,
                               @Query("limit")int limit);

    //수정 API
    @PUT("/memo/{memoId}")
    Call<Res> updateMemo(@Path("memoId") int memoId, @Header("Authorization") String token, @Body Memo memo);

    //삭제 API
    @DELETE("/memo/{memoId}")
    Call<Res> deleteMemo(@Path("memoId") int memoId, @Header("Authorization") String token);
}
