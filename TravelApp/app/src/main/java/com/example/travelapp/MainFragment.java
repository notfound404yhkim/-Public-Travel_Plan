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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainFragment extends Fragment {

    Button btn;
    TextView textView,txtRes;
    TextView txtRegion,txtDate;
    RelativeLayout RegionLayout; //지역 선택부분 레이아웃
    RelativeLayout DateLayout;
    LinearLayout BottomLayout; //하단 레이아웃
    ListView listRegion; //지역 리스트 뷰
    Calendar calendar;
    //선택한날짜 멤버변수
    //선택한날짜 멤버변수화
    String dateString1 = null;
    String dateString2 = null;
    String token;

    ArrayList<Place> placeArrayList = new ArrayList<>();




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);  //프레그먼트 레이아웃지정.

        btn = view.findViewById(R.id.btn_select);
        textView = view.findViewById(R.id.txt_more);
        RegionLayout = view.findViewById(R.id.RegionLayout);
        BottomLayout = view.findViewById(R.id.BottomLayout);
        DateLayout = view.findViewById(R.id.DateLayout);
        listRegion = view.findViewById(R.id.listRegion);
        txtRegion = view.findViewById(R.id.txtRegion);
        txtDate = view.findViewById(R.id.txtDate);
        txtRes = view.findViewById(R.id.txt_response);

        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        //오늘 날짜
        Long today = MaterialDatePicker.todayInUtcMilliseconds();


        // 앱 첫 실행시 서울 축제 4개
        previewfestival();

        //지역 선택 부분    프레그먼트이므로 getActivity()
        final String[] region = {"서울","인천","대전","대구","광주","부산","제주"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, region);

        listRegion.setAdapter(adapter);
        RegionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomLayout.setVisibility(View.GONE);
                listRegion.setVisibility(View.VISIBLE);
            }
        });

        listRegion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0,View arg1, int arg2, long arg3) {
                //Toast.makeText(getActivity(),region[arg2],Toast.LENGTH_SHORT).show();
                txtRegion.setText(region[arg2]); //선택한지역값은 arg2에있음.
                BottomLayout.setVisibility(View.VISIBLE);
                listRegion.setVisibility(View.GONE);
            }
        });


        //날짜 선택부분
        DateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();

                builder.setTitleText("Date Picker");
                builder.setSelection(Pair.create(MaterialDatePicker.thisMonthInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds()));

                MaterialDatePicker materialDatePicker = builder.build();

                materialDatePicker.show(requireFragmentManager(), "DATE_PICKER");
                //확인버튼
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date date1 = new Date();
                        Date date2 = new Date();

                        date1.setTime(selection.first);
                        date2.setTime(selection.second);

                        dateString1 = simpleDateFormat.format(date1);
                        dateString2 = simpleDateFormat.format(date2);

                        txtDate.setText(dateString1 + " ~ " + dateString2);
                    }
                });
            }
        });



        // 선택 완료 눌렀을때.
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dateString1 == null || dateString2 == null){
                    Toast.makeText(getActivity(), "날짜를 선택하세요", Toast.LENGTH_SHORT).show();
                    return;}
            showProgress();
            Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
            HistoryApi api = retrofit.create(HistoryApi.class);
            // 토큰 가져온다.
            SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
            token = sp.getString("token", "");
            token = "Bearer " + token;

            String region = txtRegion.getText().toString().trim();
            History history = new History(region,dateString1,dateString2);


            Call<Res> call =  api.addHistory(token,history);


                call.enqueue(new Callback<Res>() {
                    @Override
                    public void onResponse(Call<Res> call, Response<Res> response) {

                        if(response.isSuccessful()){
                            dismissProgress();
                            Res res = response.body();
                            ArrayList<String> items = res.items;

                            StringBuilder stringBuilder = new StringBuilder();

                            for (String item : items) {
                                stringBuilder.append(item).append("\n"); // 각 항목을 새 줄에 추가
                            }
                            txtRes.setText(stringBuilder.toString().trim());


                            Toast.makeText(getActivity(), "여행지 추천 완료", Toast.LENGTH_SHORT).show();

                            return;
                        }else{
                            //유저에게 알리고
                            return;
                        }
                }

                @Override
                public void onFailure(Call<Res> call, Throwable t) {

                }
            });

//                Intent intent = new Intent(getActivity(), ListActivity.class);
//                startActivity(intent);

            }
        });



        // 축제 및 행사 리스트 더보기
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PlaceActivity.class);
                startActivity(intent);

            }
        });


        return view;
    }


    // 앱 처음 실행시 서울 축제 이미지 4개 가져오기

    public void previewfestival(){

        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
        PlaceApi api = retrofit.create(PlaceApi.class);
        // 토큰 가져온다.
        SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);


        Call<PlaceList> call = api.getImg("인천", 1, 0,25);
        Log.i("AAA", "테스트2 ");
        call.enqueue(new Callback<PlaceList>() {
            @Override
            public void onResponse(Call<PlaceList> call, Response<PlaceList> response) {

                Log.i("AAA",response.toString());
                if(response.isSuccessful()){

                    PlaceList placeList = response.body();


                    placeArrayList.addAll(placeList.items);


                    for (Place item : placeArrayList) {
                        System.out.println("이미지 주소: " + item.id);
                        System.out.println("이미지 링크: " + item.imgUrl);
                        Log.i("AAA", String.valueOf(item.id));


                    }



//                    // 어댑터 만들어서, 리사이클러뷰에 적용
//                    adapter = new PostingAdapter(getActivity(), postingArrayList);
//                    recyclerView.setAdapter(adapter);
//                    adapter.notifyDataSetChanged();
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

