package com.example.familychoretracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class UserHome extends AppCompatActivity {
    private static final String TAG = "UserHome";
    Button btnLogUserOut, btnTodayChores, btnWeekChores;
    RecyclerView rvDisplayChores;
    private ArrayList<Chore> choreList;
    String todaysDate = new SimpleDateFormat("DDD/yyyy", Locale.getDefault()).format(new Date());
    String oneWeeksDate = dateModifier(todaysDate);



    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private Query choreQuery;
    private String userID;
    private ListView mListView;
    private ChoreAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        btnLogUserOut = (Button) findViewById(R.id.btnLogUserOut);
        btnTodayChores = findViewById(R.id.btnTodayChores);
        btnWeekChores = findViewById(R.id.btnWeekChores);
        rvDisplayChores = findViewById(R.id.rvDisplayProgress);//CHANGED TO SEE IF DISPLAYING ON NEW SCREEN WOULD HELP
        rvDisplayChores.setHasFixedSize(true);
        rvDisplayChores.setLayoutManager(new LinearLayoutManager(this));

        //add Firebase Database stuff
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        choreList = new ArrayList<>();
        adapter = new ChoreAdapter(this, choreList);
        rvDisplayChores.setAdapter(adapter);


        btnLogUserOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(UserHome.this, MainActivity.class);
                startActivity(intent);//Sends user to Main Activity on sign out

            }
        });



        //Populates the list of chores for all users due today -- future enhancement will allow to only show currently logged in user.
        btnTodayChores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    String userPrimaryUserID = new String();
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //This is called once with the initial value and again
                        //whenever data at this location is updated.
                        userPrimaryUserID = showUserInfo(dataSnapshot);
                        String userName = showUserName(dataSnapshot);
                        Log.d(TAG, "btnTodayChores OnClick user's primary UID: " + userPrimaryUserID);
                        choreQuery = mFirebaseDatabase.getReference().child("Users").child(userPrimaryUserID).child("Chores").orderByChild("choreDueDate").equalTo(todaysDate);
                        choreQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                choreList.clear();
                                for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                    Chore chore = dataSnapshot1.getValue(Chore.class);
                                    String choreID = dataSnapshot1.getKey();
                                    chore.setChoreID(choreID);
                                    Log.d(TAG, " CHORE ID :" + choreID);
                                    if(chore.getChoreAssignedTo().equals(userName)) {
                                        choreList.add(chore);
                                    }
                                }
                                adapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        //Populates the list of chores for all users due in next 7 days -- future enhancement will allow to only show currently logged in user.
        btnWeekChores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    String userPrimaryUserID = new String();
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //This is called once with the initial value and again
                        //whenever data at this location is updated.
                        userPrimaryUserID = showUserInfo(dataSnapshot);
                        String userName = showUserName(dataSnapshot);
                        Log.d(TAG, "btnTodayChores OnClick user's primary UID: " + userPrimaryUserID);
                        choreQuery = mFirebaseDatabase.getReference().child("Users").child(userPrimaryUserID).child("Chores").orderByChild("choreDueDate").startAt(todaysDate).endAt(oneWeeksDate);
                        choreQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                choreList.clear();
                                for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                    Chore chore = dataSnapshot1.getValue(Chore.class);
                                    String choreID = dataSnapshot1.getKey();
                                    chore.setChoreID(choreID);
                                    Log.d(TAG, " CHORE ID :" + choreID);
                                    if(chore.getChoreAssignedTo().equals(userName)) {
                                        choreList.add(chore);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //This is called once with the initial value and again
                //whenever data at this location is updated.
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            User uInfo = new User();
            uInfo.setFullName(ds.child(userID).getValue(User.class).getFullName());
            uInfo.setEmail(ds.child(userID).getValue(User.class).getEmail());
            uInfo.setPrimaryUserId(ds.child(userID).getValue(User.class).getPrimaryUserId());
            uInfo.setIsAdmin(ds.child(userID).getValue(User.class).getIsAdmin());
            uInfo.setMyUserId(userID);

            Log.d(TAG, "showData method: fullName: " + uInfo.getFullName());
            Log.d(TAG, "showData method: email: " + uInfo.getEmail());
            Log.d(TAG, "showData method: primaryUserUid: " + uInfo.getPrimaryUserId());
            Log.d(TAG, "showData method: isAdmin: " + uInfo.getIsAdmin());
            Log.d(TAG, "showData method: myUserId: " + uInfo.getMyUserId());

            //This references the admin accounts sub users to verify if they are populated. If not it enters the user's information into the database under sub-users
            DatabaseReference subUserRef = myRef.child("Users").child(uInfo.getPrimaryUserId()).child("subUsers");
            subUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.child(userID).exists()) {
                        subUserRef.child(userID).setValue(uInfo);
                        Toast.makeText(UserHome.this, "User registered to Admin account in database!!!!!", Toast.LENGTH_SHORT).show();

                    } else {
                        //Toast.makeText(UserHome.this, "User exists in database!!!!!", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
    }

    private String showUserInfo(DataSnapshot dataSnapshot) {
        String primaryUserID = new String();
        User uInfo = new User();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {

            uInfo.setFullName(ds.child(userID).getValue(User.class).getFullName());
            uInfo.setEmail(ds.child(userID).getValue(User.class).getEmail());
            uInfo.setPrimaryUserId(ds.child(userID).getValue(User.class).getPrimaryUserId());
            uInfo.setIsAdmin(ds.child(userID).getValue(User.class).getIsAdmin());
            uInfo.setMyUserId(userID);
            primaryUserID = uInfo.getPrimaryUserId();
            Log.d(TAG, "showUserInfo method: primaryUserUid: " + uInfo.getPrimaryUserId());

        }
        return primaryUserID;
    }

    private String showUserName(DataSnapshot dataSnapshot) {
        String userFullName = new String();
        User uInfo = new User();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {

            uInfo.setFullName(ds.child(userID).getValue(User.class).getFullName());
            uInfo.setEmail(ds.child(userID).getValue(User.class).getEmail());
            uInfo.setPrimaryUserId(ds.child(userID).getValue(User.class).getPrimaryUserId());
            uInfo.setIsAdmin(ds.child(userID).getValue(User.class).getIsAdmin());
            uInfo.setMyUserId(userID);
            userFullName = uInfo.getFullName();
            Log.d(TAG, "showUserInfo method: primaryUserUid: " + uInfo.getPrimaryUserId());

        }
        return userFullName;
    }

    private String dateModifier(String currentDate) {
        Log.d(TAG, "dateModifier input string " + currentDate);

        String day = "";
        String newDay = "";
        String year = "";
        String newDate = "";

        System.out.println("Current date length" + currentDate.length());
        int charIndex = currentDate.indexOf('/');
        System.out.println("Index of / " + charIndex);

        for(int i = 0; i<currentDate.length(); i++){
            if(i<charIndex){
                day += currentDate.charAt(i);
                System.out.print("Current contents of day " + day);
            }
            else{
                year += currentDate.charAt(i);
                System.out.println("Current contents of year " + year);
            }
        }

        int dateNum = Integer.parseInt(day);
        System.out.println("Contents of dateNum" + dateNum);
        dateNum += 6;
        System.out.println("Contents of dateNum after adding 6" + dateNum);
        newDay = String.valueOf(dateNum);
        System.out.println("Contents of newDay" + newDay);

        newDate = newDay + year;
        System.out.println("Contents of newDate" +newDate);

        Log.d(TAG, "dateModifier output string " + newDate);
        return newDate;
    }
}






