package com.example.familychoretracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ViewProgress extends AppCompatActivity {
    private static final String TAG = "ViewProgress";
    String todaysDate = new SimpleDateFormat("DDD/yyyy", Locale.getDefault()).format(new Date());
    private ArrayList<Chore> progressList;
    private RecyclerView rvDisplayProgress;
    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private Query queryRef;
    private String userID;
    private ListView mListView;
    private ProgressAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_progress);
        rvDisplayProgress = findViewById(R.id.rvDisplayProgress);
        rvDisplayProgress.setHasFixedSize(true);
        rvDisplayProgress.setLayoutManager(new LinearLayoutManager(this));
        progressList = new ArrayList<>();
        adapter = new ProgressAdapter(this, progressList);
        rvDisplayProgress.setAdapter(adapter);

        //add Firebase Database stuff
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        queryRef = mFirebaseDatabase.getReference().child("Users").child(userID).child("Chores").orderByChild("choreDueDate").equalTo(todaysDate);


        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chore chore = dataSnapshot.getValue(Chore.class);
                    //Toast.makeText(ViewProgress.this, "Made it to choreRef snapshot loop", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "chore "  + chore.getChoreID());
                    progressList.add(chore);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
