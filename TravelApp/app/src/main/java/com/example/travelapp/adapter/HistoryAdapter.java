package com.example.travelapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelapp.HistoryInfoActivity;
import com.example.travelapp.R;
import com.example.travelapp.model.History;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    Context context;
    ArrayList<History> historyArrayList;
    int index;

    History history;


    public HistoryAdapter(Context context, ArrayList<History> historyArrayList) {
        this.context = context;
        this.historyArrayList = historyArrayList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.history_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        History history = historyArrayList.get(position);
        holder.txtRegion.setText(history.region);
        holder.txtDate.setText(history.strDate +" ~ "+ history.endDate);


    }

    @Override
    public int getItemCount() {
        return historyArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtRegion,txtDate;
        ConstraintLayout constraintLayout;
        ImageButton imgBtn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRegion = itemView.findViewById(R.id.txtRegion);
            txtDate = itemView.findViewById(R.id.txtDate);
            constraintLayout = itemView.findViewById(R.id.box);
            imgBtn = itemView.findViewById(R.id.btnDelete);

            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    index = getAdapterPosition();
                    history = historyArrayList.get(index);
                    Intent intent = new Intent(context, HistoryInfoActivity.class);
                    intent.putExtra("id",history.id);
                    context.startActivity(intent);
                }
            });
            imgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context,"삭제되었습니다",Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}
