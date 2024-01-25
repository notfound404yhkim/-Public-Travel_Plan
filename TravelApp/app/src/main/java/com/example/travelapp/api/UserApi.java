package com.example.travelapp.api;

import com.example.travelapp.model.User;
import com.example.travelapp.model.UserRes;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserApi {
    // 회원가입 API
    @POST("/user/register")
    Call<UserRes> register(@Body User user); //받는 것 return , 보내는 것 : 파라미터

    //로그인 API
    @POST("/user/login")
    Call<UserRes> login (@Body User user);

}
