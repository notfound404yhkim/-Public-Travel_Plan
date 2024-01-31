package com.example.travelapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travelapp.R;
import com.example.travelapp.model.DetailPosting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder> {
    Context context;
    ArrayList<DetailPosting.Comments> commentsArrayList;
    SimpleDateFormat sf;
    SimpleDateFormat df;

    public CommentListAdapter(Context context, ArrayList<DetailPosting.Comments> commentsArrayList) {
        this.context = context;
        this.commentsArrayList = commentsArrayList;

        // 성능 개선을 위해 첫 1회만 실행하도록 생성자 안에다 넣는다.
        sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        df = new SimpleDateFormat("yyyy년MM월dd일 HH시mm분ss초");
        sf.setTimeZone(TimeZone.getTimeZone("UTC"));
        df.setTimeZone(TimeZone.getDefault());
    }

    @NonNull
    @Override
    public CommentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_list_row, parent, false);

        return new CommentListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DetailPosting.Comments comments = commentsArrayList.get(position);

        if (comments.profileImg != null){
            Glide.with(context).load(comments.profileImg).into(holder.txtProfilePhoto);
        }

        holder.txtName.setText(comments.name);

        try {
            Date date = sf.parse(comments.createdAt);
            String localTime = df.format(date);

            holder.txtDate.setText(localTime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        holder.txtContent.setText(comments.content);

    }

    @Override
    public int getItemCount() {
        return commentsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView txtProfilePhoto;
        TextView txtName;
        TextView txtDate;
        TextView txtContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtProfilePhoto = itemView.findViewById(R.id.txtProfilePhoto);
            txtName = itemView.findViewById(R.id.txtName);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtContent = itemView.findViewById(R.id.txtContent);

        }
    }
}
