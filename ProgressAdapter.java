package com.example.familychoretracker;

import android.content.Context;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.MyViewHolder> {
    ArrayList <Chore> progressList;
    Context context;


    public ProgressAdapter(Context context, ArrayList<Chore> progressList){
        this.progressList = progressList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position){
        View v = LayoutInflater.from(context).inflate(R.layout.progress, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Chore chore = progressList.get(position);
        String dueDate = null;
        holder.tvProgressAssignedTo.setText(chore.getChoreAssignedTo());
        holder.tvProgressChoreDescription.setText(chore.getChoreDescription());
        dueDate = formatDate(chore.getChoreDueDate());
        holder.tvProgressDueDate.setText(dueDate);
        holder.tvQuestion.setText(chore.getChoreQuestion());
        if(chore.getChoreNotStarted().equals("true")){
            holder.tvStatus.setText("NOT STARTED");
            holder.tvStatus.setBackgroundColor(Color.RED);

        }else if(chore.getChoreInProgress().equals("true")){
            holder.tvStatus.setText("IN PROGRESS");
            holder.tvStatus.setBackgroundColor(Color.BLUE);


        }else if(chore.getChoreComplete().equals("true")){
            holder.tvStatus.setText("COMPLETED");
            holder.tvStatus.setBackgroundColor(Color.GREEN);

        }

    }

    @Override
    public int getItemCount(){return progressList.size();}


    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvProgressAssignedTo, tvProgressChoreDescription, tvProgressDueDate, tvQuestion, tvStatus;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);

            tvProgressAssignedTo = itemView.findViewById(R.id.tvProgressAssignedTo);
            tvProgressChoreDescription = itemView.findViewById(R.id.tvProgressChoreDescription);
            tvProgressDueDate = itemView.findViewById(R.id.tvProgressDueDate);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvStatus = itemView.findViewById(R.id.tvStatus);

        }
    }

    public String formatDate(String choreDueDate){
        SimpleDateFormat newSdf = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat sdf = new SimpleDateFormat("DDD/yyyy");
        String str = null;
        Date date = null;
        try{
            date = sdf.parse(choreDueDate);
            str = newSdf.format(date);
        }
        catch(ParseException ex){
        }
        return str;
    }
}
