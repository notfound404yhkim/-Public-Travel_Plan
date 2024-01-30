package com.example.travelapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelapp.PlaceInfoActivity;
import com.example.travelapp.R;
import com.example.travelapp.ScheduleInfoActivity;
import com.example.travelapp.model.Place;
import com.example.travelapp.model.Schedule;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    Context context;
    ArrayList<Schedule> ScheduleArrayList = new ArrayList<>(); //스케줄 목록
    Schedule schedule;
    int index;



    public ScheduleAdapter(Context context, ArrayList<Schedule> scheduleArrayList) {
        this.context = context;
        this.ScheduleArrayList = scheduleArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.place_row,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Schedule schedule = ScheduleArrayList.get(position);
        holder.txtRegion.setText(schedule.region);
        holder.txtName.setText(schedule.region + " 코스 ");
        holder.txtDate.setText(schedule.strDate +" ~ " + schedule.endDate);
        Picasso.get().load(schedule.imgUrl).into( holder.imgPhoto);

    }

    @Override
    public int getItemCount() {

        return ScheduleArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtRegion;
        TextView txtName;
        TextView txtDate;

        ImageView imgPhoto;

        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRegion = itemView.findViewById(R.id.txtRegion);
            txtName = itemView.findViewById(R.id.txtName);
            txtDate = itemView.findViewById(R.id.txtDate);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);
            cardView = itemView.findViewById(R.id.cardView);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    index = getAdapterPosition();
                    schedule = ScheduleArrayList.get(index);
                    Intent intent = new Intent(context, ScheduleInfoActivity.class);
                    intent.putExtra("id",schedule.id);
                    context.startActivity(intent);
                }
            });

        }
    }
}
