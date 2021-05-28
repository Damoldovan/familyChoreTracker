package com.example.familychoretracker;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;

//Created based on a video by CodingWithMitch on Youtube "Reading Data using a ValueEventListener - Android Firebase"
public class ViewDatabase extends AppCompatActivity {
    private static final String TAG = "ViewDatabase";
    private RecyclerView rvDisplayUsers;

    //add Firebase Database stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String userID;
    private MyAdapter adapter;
    private ArrayList<User> list;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_database_layout);
        rvDisplayUsers = findViewById(R.id.rvDisplayUsers);
        rvDisplayUsers.setHasFixedSize(true);
        rvDisplayUsers.setLayoutManager(new LinearLayoutManager(this));
        //declare the database reference object. This is what we use to access the database.
        //NOTE:  Unless you are signed in, this will not be usable.
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        myRef = mDatabase.getReference().child("Users").child(userID).child("subUsers");//THIS NEEDS EDITED TO PULL primaryUserId from the currently logged in admin user.
        list = new ArrayList<>();
        adapter = new MyAdapter(this, list);
        rvDisplayUsers.setAdapter(adapter);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {//MODIFIED CODE SO IT ONLY READS WHEN TOLD TO. MAY NOT WORK!!!!!!! myRef.addValueEventListener***
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    list.add(user);
                }
                adapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

        /*
        customizable toast
        @param message
        */

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}


