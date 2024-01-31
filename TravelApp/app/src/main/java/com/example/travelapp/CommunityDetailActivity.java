package com.example.travelapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.travelapp.adapter.CommentListAdapter;
import com.example.travelapp.api.NetworkClient;
import com.example.travelapp.api.PostingApi;
import com.example.travelapp.config.Config;
import com.example.travelapp.model.Comment;
import com.example.travelapp.model.DetailPosting;
import com.example.travelapp.model.Posting;
import com.example.travelapp.model.Res;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CommunityDetailActivity extends AppCompatActivity {

    TextView txtTitle;
    TextView txtName;
    ImageView imgPhoto;
    TextView txtContent;
    TextView txtTag;
    TextView txtDate;
    Button btnUpdate;
    Button btnDelete;
    ImageView imgLike;
    ImageView imgBookmark;
    TextView txtLikeCount;
    ImageView txtProfilePhoto;
    EditText editCommentAdd;
    Button btnWrite;

    RecyclerView recyclerView;
    CommentListAdapter adapter;
    ArrayList<DetailPosting.Comments> commentsArrayList = new ArrayList<>();

    // 현재 postId
    int postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_posting);

        txtTitle = findViewById(R.id.txtTitle);
        txtName = findViewById(R.id.txtName);
        imgPhoto = findViewById(R.id.imgPhoto);
        txtContent = findViewById(R.id.txtContent);
        txtTag = findViewById(R.id.txtTag);
        txtDate = findViewById(R.id.txtDate);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        imgLike = findViewById(R.id.imgLike);
        imgBookmark = findViewById(R.id.imgBookmark);
        txtLikeCount = findViewById(R.id.txtLikeCount);
        txtProfilePhoto = findViewById(R.id.txtProfilePhoto);
        editCommentAdd = findViewById(R.id.editCommentAdd);
        btnWrite = findViewById(R.id.btnWrite);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(CommunityDetailActivity.this));

        Posting posting = (Posting) getIntent().getSerializableExtra("posting");
        postId = posting.id;

        adapter = new CommentListAdapter(CommunityDetailActivity.this, commentsArrayList);
        recyclerView.setAdapter(adapter);

        userInfo();

        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = editCommentAdd.getText().toString().trim();

                if (comment.isEmpty()){
                    Snackbar.make(btnWrite, "내용을 입력해주세요.", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                Retrofit retrofit = NetworkClient.getRetrofitClient(CommunityDetailActivity.this);

                PostingApi api = retrofit.create(PostingApi.class);

                SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                String token = sp.getString("token", "");

                Comment comment1 = new Comment(comment);
                Call<Res> call = api.addComment(postId, "Bearer " + token, comment1);

                call.enqueue(new Callback<Res>() {
                    @Override
                    public void onResponse(Call<Res> call, Response<Res> response) {
                        if (response.isSuccessful()){
                            Snackbar.make(btnWrite, "댓글을 작성하였습니다.", Snackbar.LENGTH_SHORT).show();
                            editCommentAdd.setText("");
                            getNetworkData();
                            return;
                        }
                    }

                    @Override
                    public void onFailure(Call<Res> call, Throwable t) {
                        Snackbar.make(btnWrite, "통신 실패입니다.", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                });
            }
        });

    }

    private void userInfo() {
        Retrofit retrofit = NetworkClient.getRetrofitClient(CommunityDetailActivity.this);


    }

    @Override
    protected void onResume() {
        getNetworkData();

        super.onResume();
    }

    private void getNetworkData() {
        Retrofit retrofit = NetworkClient.getRetrofitClient(CommunityDetailActivity.this);

        PostingApi api = retrofit.create(PostingApi.class);

        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String token = sp.getString("token", "");

        Call<DetailPosting> call = api.detailPosting(postId, "Bearer " + token);

        call.enqueue(new Callback<DetailPosting>() {
            @Override
            public void onResponse(Call<DetailPosting> call, Response<DetailPosting> response) {
                if (response.isSuccessful()){
                    DetailPosting detailPosting = response.body();

                    txtTitle.setText(detailPosting.items.title);
                    txtName.setText(detailPosting.items.name);
                    Glide.with(CommunityDetailActivity.this).load(detailPosting.items.imgUrl).into(imgPhoto);
                    txtContent.setText(detailPosting.items.content);

                    StringBuilder tags = new StringBuilder();
                    for (int i = 0; i < detailPosting.tag.size(); i++) {
                        tags.append(detailPosting.tag.get(i));
                        if (i < detailPosting.tag.size() - 1) {
                            tags.append(", ");
                        }
                    }
                    String result = tags.toString();
                    txtTag.setText(result);

                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    SimpleDateFormat df = new SimpleDateFormat("yyyy년MM월dd일 HH시mm분ss초");
                    sf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    df.setTimeZone(TimeZone.getDefault());

                    try {
                        Date date = sf.parse(detailPosting.items.createdAt);
                        String localtime = df.format(date);
                        txtDate.setText(localtime);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                    txtLikeCount.setText(detailPosting.items.likeCnt+"개");

                    commentsArrayList.clear();

                    commentsArrayList.addAll(detailPosting.comments);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<DetailPosting> call, Throwable t) {
                Snackbar.make(btnWrite, "통신 실패입니다.", Snackbar.LENGTH_SHORT).show();
                return;
            }
        });
    }
}