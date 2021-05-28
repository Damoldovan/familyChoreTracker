package com.example.familychoretracker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ChoreAdapter extends RecyclerView.Adapter<ChoreAdapter.MyViewHolder> {
    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private String userID;
    ArrayList<Chore> choreList;
    Context context;



   public ChoreAdapter(Context context, ArrayList<Chore> choreList){
       this.choreList = choreList;
       this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.chore, parent, false);
        return new ChoreAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChoreAdapter.MyViewHolder holder, int position) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();


        Chore chore = choreList.get(position);
        String dueDate = null;
        holder.tvAssignedTo.setText(chore.getChoreAssignedTo());
        dueDate = formatDate(chore.getChoreDueDate());
        holder.tvChoreDescription.setText(chore.getChoreDescription());
        holder.tvDueDate.setText(dueDate);
        //These lines of code are designed to make it so the user can only select in progress or complete but did not seem to work. I did not have time to investigate why.
        if(holder.swInProgress.isChecked()){
            holder.swChoreComplete.setEnabled(false);
        }

        if(holder.swChoreComplete.isChecked()){
            holder.swInProgress.setEnabled(false);
        }

        holder.btnUpdateChore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(holder.swInProgress.isChecked() && holder.swChoreComplete.isChecked()){
                    chore.setChoreComplete("true");
                    chore.setChoreNotStarted("false");
                    chore.setChoreInProgress("false");
                }

                else if(holder.swInProgress.isChecked()){
                    chore.setChoreComplete("false");
                    chore.setChoreInProgress("true");
                    chore.setChoreNotStarted("false");
                }

                else if(holder.swChoreComplete.isChecked()){
                    chore.setChoreComplete("true");
                    chore.setChoreInProgress("false");
                    chore.setChoreNotStarted("false");
                }

                String question = holder.etQuestion.getText().toString();
                chore.setChoreQuestion(question);
                Log.d(" *** CHORE UPDATING IN CHORE ADAPTER ***" + "\n", "chore complete: " + chore.getChoreComplete() + "\n" +
                        "chore in progress: " + chore.getChoreInProgress() + "\n" +
                        "chore question: " + chore.getChoreQuestion());
                updateChore(chore);
            }
            public void updateChore(Chore chore){
                String choreID = chore.getChoreID();
                Log.d(" *** CHORE UPDATING IN CHORE ADAPTER ***" + "\n", "chore complete: " + chore.getChoreComplete() + "\n" +
                        "chore in progress: " + chore.getChoreInProgress() + "\n" +
                        "chore question: " + chore.getChoreQuestion() + "\n" +
                        "chore ID" + chore.getChoreID());
                HashMap hashMap = new HashMap();
                hashMap.put("choreNotStarted", chore.getChoreNotStarted());
                hashMap.put("choreInProgress", chore.getChoreInProgress());
                hashMap.put("choreComplete", chore.getChoreComplete());
                hashMap.put("choreQuestion", chore.getChoreQuestion());


                myRef.child("Users").child(chore.getUserUid()).child("Chores").child(chore.getChoreID()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context.getApplicationContext(), "Chore has been updated!", Toast.LENGTH_LONG).show();
                        holder.btnUpdateChore.setBackgroundColor(Color.GREEN);

                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context.getApplicationContext(), "Failed to update chore.", Toast.LENGTH_LONG).show();
                            }
                        });

            }
        });
   }

    @Override
    public int getItemCount() {
        return choreList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvAssignedTo, tvChoreDescription, tvDueDate;
        EditText etQuestion;
        Switch swInProgress, swChoreComplete;
        Button btnUpdateChore;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAssignedTo = itemView.findViewById(R.id.tvAssignedTo);
            tvChoreDescription = itemView.findViewById(R.id.tvChoreDescription);
            tvDueDate   =   itemView.findViewById(R.id.tvDueDate);
            etQuestion = itemView.findViewById(R.id.etQuestion);
            swChoreComplete = itemView.findViewById(R.id.swChoreComplete);
            swInProgress = itemView.findViewById(R.id.swInProgress);
            btnUpdateChore = itemView.findViewById(R.id.btnUpdateChore);

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

