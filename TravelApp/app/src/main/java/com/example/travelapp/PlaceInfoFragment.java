package com.example.travelapp;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.travelapp.api.NetworkClient;
import com.example.travelapp.api.PlaceApi;
import com.example.travelapp.model.Place;
import com.example.travelapp.model.PlaceList;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PlaceInfoFragment extends Fragment {
    int id;
    TextView txtTile,txtRegion,txtContent,txtDate;
    ImageView imgPhoto;
    ArrayList<Place> placeArrayList = new ArrayList<>();
    LinearLayout linearLayout; // 레이아웃

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.activity_placeinfo, container, false);
        txtTile=view.findViewById(R.id.txtTile);
        txtRegion=view.findViewById(R.id.txtRegion);
        txtContent=view.findViewById(R.id.txtTitle);
        imgPhoto=view.findViewById(R.id.imgPhoto);
        txtDate=view.findViewById(R.id.txtDate);
        linearLayout=view.findViewById(R.id.LinearLayout);

        // getArguments() 메소드를 사용하여 데이터 추출
        Bundle bundle = getArguments();
        if (bundle != null) {
            id = bundle.getInt("id");

        }
        Log.i("AAA","전달받은값"+id);
        getInfo(id);

        return view;
    }


    //행사,축제 정보 출력.
    public void getInfo(int id){
        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
        PlaceApi api = retrofit.create(PlaceApi.class);
        Call<PlaceList> call = api.getPlaceInfo(id,1);
        call.enqueue(new Callback<PlaceList>() {
            @Override
            public void onResponse(Call<PlaceList> call, Response<PlaceList> response) {

                if(response.isSuccessful()){
                    PlaceList placeList = response.body();
                    placeArrayList.addAll(placeList.items);

                    for (Place item : placeArrayList) {
                        txtTile.setText(item.placeName);
                        txtRegion.setText(item.region);
                        txtContent.setText(item.content);
                        if (item.option==1){
                        txtDate.setText(item.strDate +" ~ " + item.endDate);}
                        Picasso.get().load(item.imgUrl).into(imgPhoto);

                        linearLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onFailure(Call<PlaceList> call, Throwable t) {
                Log.i("AAA", "에러");
                dismissProgress();
            }
        });




    }



    // 네트워크 데이터 처리할때 사용할 다이얼로그
    Dialog dialog;
    private void showProgress(){
        dialog = new Dialog(getActivity());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(new ProgressBar(getActivity()));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
    private void dismissProgress(){
        dialog.dismiss();
    }
}
