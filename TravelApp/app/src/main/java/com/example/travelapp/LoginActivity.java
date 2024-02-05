package com.example.travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travelapp.api.NetworkClient;
import com.example.travelapp.api.UserApi;
import com.example.travelapp.config.Config;
import com.example.travelapp.model.User;
import com.example.travelapp.model.UserRes;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    ProgressBar progressBar;
    TextView txtView;
    Button btnLogin;
    EditText login_email;
    EditText login_password;
    ImageView imgView;

    // 구글 로그인
    SignInButton googleLoginBtn;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    Button btnLog; // 이미지 원래 하이트 300

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar = findViewById(R.id.login_progressBar);
        txtView = findViewById(R.id.txt_register);
        btnLogin = findViewById(R.id.login_button);
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);

        imgView = findViewById(R.id.imgView);
        googleLoginBtn = findViewById(R.id.googleLoginBtn);

        btnLog = findViewById(R.id.btnLog);

        progressBar.setVisibility(View.GONE);

        // GoogleSignInOptions 설정
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        // GoogleSignInClient 설정
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleLogin();
            }
        });

        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGoogleSignInClient == null) return;
                mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        task.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.i("AAAAAAAAAAAAA", "로그아웃 성공");
                            }
                        });
                        task.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("AAAAAAAAAAAAA", "로그아웃 실패");

                            }
                        });
                    }
                });
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = login_email.getText().toString().trim();
                String password = login_password.getText().toString().trim();
                if (email.isEmpty() || password.isEmpty()){
                    Toast.makeText(LoginActivity.this,"항목을 모두 입력하세요",Toast.LENGTH_SHORT).show();
                    return;
                }

                // 이메일 형식이 맞는지 체크
                Pattern pattern = Patterns.EMAIL_ADDRESS;
                if (pattern.matcher(email).matches() == false){
                    Toast.makeText(LoginActivity.this,"이메일 형식을 확인하세요",Toast.LENGTH_SHORT).show();
                    return;
                }

                showProgress();

                Retrofit retrofit = NetworkClient.getRetrofitClient(LoginActivity.this);
                UserApi api = retrofit.create(UserApi.class);

                User user = new User(email,password);

                // 4. api 호출
                Call<UserRes> call = api.login(user);

                // 5.  서버로부터 받은 응답을 처리하는 코드 작성.
                call.enqueue(new Callback<UserRes>() {
                    @Override
                    public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                        dismissProgress();
                        //서버에서 보낸 응답이 200 ok 일때 처리하는 코드.
                        Log.i("AAA","응답 code : " + response.code());
                        if (response.isSuccessful()){
                            UserRes userRes = response.body();

                            SharedPreferences sp =
                                    getSharedPreferences(Config.PREFERENCE_NAME,MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("token",userRes.accessToken);
                            editor.apply();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();


                        }else if(response.code() == 400){
                            Toast.makeText(LoginActivity.this,"회원가입이 되지 않은 이메일이거나, 비번이 틀립니다.",Toast.LENGTH_SHORT).show();
                            return;
                        }else if(response.code() == 500){
                            Toast.makeText(LoginActivity.this,"DB 처리중에 문제가 있습니다.",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else{
                            Toast.makeText(LoginActivity.this,"잠시 후 이용하세요.",Toast.LENGTH_SHORT).show();
                            return;
                        }

                    }

                    @Override
                    public void onFailure(Call<UserRes> call, Throwable t) {
                        //유저한테 네트워크 통신 실패했다고 알려준다.

                    }
                });

            }
        });

        txtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void googleLogin() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            getGoogleInfo(task);
        }
    }

    private void getGoogleInfo(Task<GoogleSignInAccount> completedTask) {
        try {
            String TAG = "구글 로그인 결과";
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d(TAG, account.getId());
            Log.d(TAG, account.getFamilyName());
            Log.d(TAG, account.getGivenName()); // 이름
            Log.d(TAG, account.getEmail()); // 이메일
        } catch (ApiException e) {
            String TAG = "구글 로그인 결과";
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private long time= 0;
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - time >= 2000) {
            time = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    // 네트워크 데이터 처리할때 사용할 다이얼로그
    Dialog dialog;
    private void showProgress(){
        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(new ProgressBar(this));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
    private void dismissProgress(){
        dialog.dismiss();
    }
}
