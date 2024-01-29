package com.example.travelapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.travelapp.adapter.PlaceAdapter;
import com.example.travelapp.api.NetworkClient;
import com.example.travelapp.api.PlaceApi;
import com.example.travelapp.api.PostingApi;
import com.example.travelapp.config.Config;
import com.example.travelapp.model.Place;
import com.example.travelapp.model.PlaceList;

import java.util.ArrayList;

public class PlaceActivity extends AppCompatActivity {



    // 페이징 처리를 위한 변수들
    int offset = 0;
    int limit = 25;
    int count = 0;

    String token;




    // 리싸이클뷰는, 함께 선언(3종)

    RecyclerView recyclerView;
    PlaceAdapter adapter;
    ArrayList<Place> placeArrayList = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        //리사이클러뷰 설정
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(PlaceActivity.this));

        getNetworkData();

    }

    private void getNetworkData() {

        //변수 초기화
        offset = 0;
        count = 0;

        Retrofit retrofit = NetworkClient.getRetrofitClient(PlaceActivity.this);

        PlaceApi api = retrofit.create(PlaceApi.class);
        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        token = sp.getString("token", "");
        token = "Bearer " + token;


        Call<PlaceList> call = api.getPlacelist(token,"인천", 1,offset, limit);

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
                    adapter = new PlaceAdapter(PlaceActivity.this, placeArrayList);
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
}

