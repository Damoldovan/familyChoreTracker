package com.example.familychoretracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
    ArrayList<User> userList;
    Context context;

    public MyAdapter(Context context, ArrayList<User> userList){
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override//ViewHolder places the context into item.xml (item.xml is the layout for the items in the list)
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvUserName.setText(user.getFullName());
        holder.tvUserEmail.setText(user.getEmail());
        holder.tvUserID.setText(user.getMyUserId());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvUserName, tvUserEmail, tvUserID;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvUserID    =   itemView.findViewById(R.id.tvUserID);
        }
    }
}
