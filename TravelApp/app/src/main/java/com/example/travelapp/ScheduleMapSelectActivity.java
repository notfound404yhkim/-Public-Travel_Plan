package com.example.travelapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelapp.adapter.PlaceAdapter;
import com.example.travelapp.adapter.SchedulePlaceSelectAdapter;
import com.example.travelapp.api.NetworkClient;
import com.example.travelapp.api.PlaceApi;
import com.example.travelapp.config.Config;
import com.example.travelapp.model.Place;
import com.example.travelapp.model.PlaceList;

import java.util.ArrayList;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ScheduleMapSelectActivity extends AppCompatActivity {



    // 페이징 처리를 위한 변수들
    int offset = 0;
    int limit = 25;
    int count = 0;

    String token;
    String region;




    String[] regionlist = {"서울","인천","대전","대구","광주","부산","제주"};

    // 리싸이클뷰는, 함께 선언(3종)

    RecyclerView recyclerView;
    SchedulePlaceSelectAdapter adapter;
    ArrayList<Place> placeArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_add_place);

        region = getIntent().getStringExtra("region");



        //리사이클러뷰 설정
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ScheduleMapSelectActivity.this));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int totalCount = recyclerView.getAdapter().getItemCount();

                if(lastPosition + 1 == totalCount){
                    // 네트워크 통해서 데이터를 더 불러온다.
                    if( limit == count){
                        // DB에 데이터가 더 존재할수 있으니, 데이터를 불러온다.
                        addNetworkData(region);
                    }
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        getNetworkData(region);
    }


    private void getNetworkData(String region) {

        //변수 초기화
        offset = 0;
        count = 0;

        Retrofit retrofit = NetworkClient.getRetrofitClient(ScheduleMapSelectActivity.this);

        PlaceApi api = retrofit.create(PlaceApi.class);
        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        token = sp.getString("token", region);
        token = "Bearer " + token;


        Call<PlaceList> call = api.getPlacelist(token,region, 0,offset, limit);

        call.enqueue(new Callback<PlaceList>() {
            @Override
            public void onResponse(Call<PlaceList> call, Response<PlaceList> response) {


                if(response.isSuccessful()){

                    Log.i("AAA",response.toString());
                    PlaceList placeList = response.body();
                    count = placeList.count;

                    placeArrayList.clear();
                    placeArrayList.addAll( placeList.items );

                    // 어댑터 만들어서, 리사이클러뷰에 적용 //새로고침
                    adapter = new SchedulePlaceSelectAdapter(ScheduleMapSelectActivity.this, placeArrayList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }else{

                }

            }

            @Override
            public void onFailure(Call<PlaceList> call, Throwable t) {
            }
        });
    }


    private void addNetworkData(String region) {


        Retrofit retrofit = NetworkClient.getRetrofitClient(ScheduleMapSelectActivity.this);

        PlaceApi api = retrofit.create(PlaceApi.class);
        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        token = sp.getString("token", "");
        token = "Bearer " + token;

        offset = offset + count;

        Call<PlaceList> call = api.getPlacelist(token,region, 0,offset, limit);

        call.enqueue(new Callback<PlaceList>() {
            @Override
            public void onResponse(Call<PlaceList> call, Response<PlaceList> response) {


                if(response.isSuccessful()){

                    PlaceList placeList = response.body();

                    placeArrayList.clear();
                    placeArrayList.addAll( placeList.items );

                    count = placeList.count;

                    adapter.notifyDataSetChanged();

                }else{

                }
            }

            @Override
            public void onFailure(Call<PlaceList> call, Throwable t) {

            }
        });

    }


}

