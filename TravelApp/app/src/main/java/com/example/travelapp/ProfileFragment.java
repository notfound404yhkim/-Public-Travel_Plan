package com.example.travelapp;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.travelapp.api.NetworkClient;
import com.example.travelapp.api.UserApi;
import com.example.travelapp.config.Config;
import com.example.travelapp.model.User;
import com.example.travelapp.model.UserRes;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class ProfileFragment extends Fragment {

    String token,profileurl;

    TextView txtName;
    ImageView profile_image_view;

    Button btnMyposting, btnMyschedule, btnAIHistory;



    ArrayList<User> userArrayList = new ArrayList<>();




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);  //프레그먼트 레이아웃지정.

        profile_image_view = view.findViewById(R.id.profile_image_view);
        txtName = view.findViewById(R.id.txtName);

        btnMyposting = view.findViewById(R.id.btnMyposting);
        btnMyschedule = view.findViewById(R.id.btnMyschedule);

        btnMyschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScheduleActivity.class);
                intent.putExtra("name",txtName.getText());
                intent.putExtra("imgUrl",profileurl);
                startActivity(intent);
            }
        });

        btnAIHistory = view.findViewById(R.id.btnAIHistory);
        btnAIHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HistoryActivity.class);
                startActivity(intent);
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 프로필 로드
        profileLoad();
    }


    public void profileLoad(){

        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
        UserApi api = retrofit.create(UserApi.class);
        SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        token = sp.getString("token","");
        token = "Bearer " + token;
        Call<UserRes> call = api.getProfile(token);
        Log.i("AAA","프로필 가져오기");


        call.enqueue(new Callback<UserRes>() {
            @Override
            public void onResponse(Call<UserRes> call, Response<UserRes> response) {


                if(response.isSuccessful()){

                    Log.i("AAA",response.toString());

                    UserRes userList = response.body();

                    userArrayList.clear();
                    userArrayList.addAll( userList.items );

                    for (User item : userArrayList) {
                        Log.i("AAA",item.name);
                        txtName.setText(item.name);
                        profileurl = item.profileImg;
                        //프로필 이미지 있을때만 적용
                        if (profileurl != null){
                            Picasso.get().load(item.profileImg).into( profile_image_view);
                        }
                    }
                }else{

                }

            }

            @Override
            public void onFailure(Call<UserRes> call, Throwable t) {

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