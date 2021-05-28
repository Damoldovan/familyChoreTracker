package com.example.familychoretracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminHome extends AppCompatActivity {
private Button btnViewProgress, btnEditChores, btnLogOut, btnCreateSubUser, btnViewUsers;

private static final String TAG = "AdminHome";

//add Firebase Database stuff
private FirebaseDatabase mFirebaseDatabase;
private FirebaseAuth mAuth;
private FirebaseAuth.AuthStateListener mAuthListener;
private DatabaseReference myRef;
private String userID;
private ListView mListView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Sets the view while using AdminHome program to activity_admin_home.xml
        setContentView(R.layout.activity_admin_home);
        //linking buttons in java to GUI buttons
        btnViewProgress = (Button)findViewById(R.id.btnViewProgress);
        btnEditChores = (Button)findViewById(R.id.btnEditChores);
        btnLogOut = (Button)findViewById(R.id.btnlogOut);
        btnCreateSubUser = findViewById(R.id.btnCreateSubUser);
        btnViewUsers = findViewById(R.id.btnViewUsers);

        //Database References
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    //user is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in: " + user.getEmail());
                    //toastMessage("Successfully signed in with: " + user.getEmail());

                }else{
                    //user is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out: " + user.getEmail());
                    //toastMessage("Successfully signed out with: " + user.getEmail());
                }
            }
        };
        //When clicking View Users the program will display all users that have logged in who are linked to the admin account.
        btnViewUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHome.this, ViewDatabase.class);
                startActivity(intent);//Sends user to Main Activity on sign out

            }
        });


        //OnClick Listener for Logout Button
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(AdminHome.this, MainActivity.class);
                startActivity(intent);//Sends user to Main Activity on sign out

            }
        });
        //Takes admin user to the creat sub user program to register a new user that falls under their admin account
        btnCreateSubUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHome.this, CreateSubUsers.class);
                startActivity(intent);//Sends user to Main Activity on sign out

            }
        });




        //Onclick Listener which redirects users to edit chores
        btnEditChores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHome.this, EditChores.class);//Sends user to Edit Chores
                startActivity(intent);
            }
        });

        //Onclick Listener which redirects users to View Progress for chores due that day by all of their sub users
        btnViewProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHome.this, ViewProgress.class);//Sends user to View Progress
                startActivity(intent);
            }
        });


        /*myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //This is called once with the initial value and again
                //whenever data at this location is updated.
                showData(dataSnapshot);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/



    }

    /*private void showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            String isAdmin;
            User uInfo = new User();
            uInfo.setFullname(ds.child(userID).getValue(User.class).getFullname());
            uInfo.setEmail(ds.child(userID).getValue(User.class).getEmail());
            uInfo.setFamilyKeyEmail(ds.child(userID).getValue(User.class).getFamilyKeyEmail());
            uInfo.setAdmin(ds.child(userID).getValue(User.class).getAdmin());

            Log.d(TAG, "showData: fullName: " + uInfo.getFullname());
            Log.d(TAG, "showData: email: " + uInfo.getEmail());
            Log.d(TAG, "showData: familyKeyEmail: " + uInfo.getFamilyKeyEmail());
            Log.d(TAG, "showData: isAdmin: " + uInfo.getAdmin());

            isAdmin = uInfo.getAdmin();

            //if (isAdmin.equals("true")) {
                //startActivity(new Intent(AdminHome.this, AdminHome.class));

            //} else {
                //startActivity(new Intent(AdminHome.this, UserHome.class));

            //}
        }
    }*/




}