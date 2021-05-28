package com.example.familychoretracker;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CreateChore extends AppCompatActivity {
    final String TAG = "Create Chore";
    private static int save = -1;
    private EditText etChoreDescription, etChoreDueDate;
    private ListView lvUserList;
    private Button btnMakeChore;
    ArrayList<String> userList;
    String choreAssignedBy, userUid, choreDueDate, choreAssignedTo,choreDescription;
    String choreComplete = "false";
    String choreInProgress= "false";
    String  choreNotStarted = "true";
    String selectedUser = "";
    ArrayList<String> array = new ArrayList<>();
    //SimpleDateFormat sdf = new SimpleDateFormat("DDD/yyyy");
    User currentUser = new User();
    //Calendar calendar = Calendar.getInstance();
    //String choreAssignedDate = sdf.getDateInstance().format(calendar.getTime());
    String choreAssignedDate = new SimpleDateFormat("DDD/yyyy", Locale.getDefault()).format(new Date());


    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef, subUserRef;
    private String userID;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chore);

        //Firebase stuff
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userList = new ArrayList<>();
        userID = user.getUid();
        subUserRef = myRef.child("Users").child(userID).child("subUsers");
        lvUserList = findViewById(R.id.lvUserList);
        etChoreDescription = (EditText)findViewById(R.id.etChoreDescription);
        etChoreDueDate =(EditText)findViewById(R.id.etChoreDueDate);
        btnMakeChore=(Button)findViewById(R.id.btnMakeChore);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1, userList);

        lvUserList.setAdapter(adapter);

        lvUserList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                parent.getChildAt(position).setBackgroundColor(Color.YELLOW);
                choreAssignedTo = lvUserList.getItemAtPosition(position).toString();
                if(save!= -1 && save != position){
                    parent.getChildAt(save).setBackgroundColor(Color.WHITE);
                }
                save = position;
                }
        });

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //This is called once with the initial value and again
                //whenever data at this location is updated.
                currentUser = showData(dataSnapshot);
                //Toast.makeText(CreateChore.this,"User email: " + currentUser.getEmail() + " Full name: " + currentUser.getFullName() + " Family key email " + currentUser.getFamilyKeyEmail(), Toast.LENGTH_LONG).show();
                choreAssignedBy = currentUser.getFullName();
                userUid = currentUser.getPrimaryUserId();
                Toast.makeText(CreateChore.this, "Got user ", Toast.LENGTH_LONG).show();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CreateChore.this,"Database Error", Toast.LENGTH_LONG).show();

            }

        });
        //This populates the available users into userList to be displayed
        subUserRef.addListenerForSingleValueEvent(new ValueEventListener() {//MODIFIED CODE SO IT ONLY READS WHEN TOLD TO. MAY NOT WORK!!!!!!! myRef.addValueEventListener***
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                        userList.add(user.getFullName());

                }
                adapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btnMakeChore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(CreateChore.this, ViewDatabase.class); OLD CODE TO TEST IF I COULD VIEW DATABASE DATA
                //startActivity(intent);

                choreDescription = etChoreDescription.getText().toString().trim();
                choreDueDate = etChoreDueDate.getText().toString().trim();



                if(choreDescription.isEmpty()){
                    etChoreDescription.setError("Chore Description is required.");
                    etChoreDescription.requestFocus();
                    return;
                }

                if(choreDueDate.isEmpty()){
                    etChoreDueDate.setError("When is the chore due?");
                    etChoreDueDate.requestFocus();
                    return;
                }
                Boolean isDateValid = validateDate(choreDueDate);
                if(!isDateValid){
                    etChoreDueDate.setError("Please enter valid date format MM-dd-yyyy");
                    etChoreDueDate.requestFocus();
                }

                choreDueDate = formatDate(choreDueDate);


                createChore(choreAssignedTo, choreAssignedBy, choreDescription, userUid, choreComplete, choreInProgress, choreNotStarted, choreDueDate, choreAssignedDate);
            }
        });



    }
    //Method that writes new chore object to the database
    private void createChore(String choreAssignedTo, String choreAssignedBy, String choreDescription, String userUid,
            String choreComplete, String choreInProgress, String choreNotStarted, String choreDueDate, String choreAssignedDate) {
        Chore chore = new Chore();
        chore.setChoreAssignedBy(choreAssignedBy);
        chore.setChoreAssignedTo(choreAssignedTo);
        chore.setChoreDescription(choreDescription);
        chore.setUserUid(userUid);
        chore.setChoreComplete(choreComplete);
        chore.setChoreInProgress(choreInProgress);
        chore.setChoreNotStarted(choreNotStarted);
        chore.setChoreDueDate(choreDueDate);
        chore.setChoreAssignedDate(choreAssignedDate);
        //Toast.makeText(CreateChore.this," Create chore method Family Key email: " + familyKeyEmail + " Full name: " + choreAssignedBy , Toast.LENGTH_LONG).show();





        //The code below works but cannot reference the admin account to place the chore in the correct loaction. Only works from one admin account.
        myRef.child("Users").child(userID).child("Chores").push().setValue(chore).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(CreateChore.this, "Chore has been updated!", Toast.LENGTH_LONG).show();
                startActivity( new Intent(CreateChore.this, AdminHome.class));
            }
        })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateChore.this, "Failed to create chore.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private User showData(DataSnapshot dataSnapshot) {
        User uInfo = new User();
        for(DataSnapshot ds: dataSnapshot.getChildren()){

            uInfo.setFullName(ds.child(userID).getValue(User.class).getFullName());
            uInfo.setEmail(ds.child(userID).getValue(User.class).getEmail());
            uInfo.setPrimaryUserId(ds.child(userID).getValue(User.class).getPrimaryUserId());
            uInfo.setIsAdmin(ds.child(userID).getValue(User.class).getIsAdmin());

            Log.d(TAG, "showData: fullName: " + uInfo.getFullName());
            Log.d(TAG, "showData: email: " + uInfo.getEmail());
            Log.d(TAG, "showData: userUid: " + uInfo.getPrimaryUserId());
            Log.d(TAG, "showData: isAdmin: " + uInfo.getIsAdmin());

            //Toast.makeText(ViewDatabase.this ,  uInfo.getEmail() + " " + uInfo.getFullName() + " " + uInfo.getIsAdmin() + " " + uInfo.getFamilyKeyEmail(), Toast.LENGTH_LONG).show();
            /*array.add(uInfo.getFullName());
            array.add(uInfo.getEmail());
            array.add(uInfo.getFamilyKeyEmail());
            array.add(uInfo.getIsAdmin());*/


        }
        return uInfo;
    }

    public boolean validateDate(String choreDueDate){
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        try{
            sdf.parse(choreDueDate);
            return true;
        }
        catch(ParseException ex){
            return false;
        }
    }

    public String formatDate(String choreDueDate){
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat newSdf = new SimpleDateFormat("DDD/yyyy");
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