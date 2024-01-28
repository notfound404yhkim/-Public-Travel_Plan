package com.example.travelapp;
import static android.content.Context.MODE_PRIVATE;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.travelapp.api.HistoryApi;
import com.example.travelapp.api.NetworkClient;
import com.example.travelapp.api.PlaceApi;
import com.example.travelapp.config.Config;
import com.example.travelapp.model.History;
import com.example.travelapp.model.Place;
import com.example.travelapp.model.PlaceList;
import com.example.travelapp.model.Res;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import androidx.fragment.app.FragmentTransaction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import java.util.Random;

public class PlaceInfoFragment extends Fragment {
    int id;
    TextView txtTile,txtRegion,txtContent,txtDate;
    ImageView imgPhoto;
    ArrayList<Place> placeArrayList = new ArrayList<>();
    LinearLayout linearLayout; // 레이아웃

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_placeinfo, container, false);
        txtTile=view.findViewById(R.id.txtTile);
        txtRegion=view.findViewById(R.id.txtRegion);
        txtContent=view.findViewById(R.id.txtContent);
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
