package com.example.familychoretracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private EditText etEmailAddress, etPassword;
    private TextView tvRegister;
    private Button btnLogin;
    private User userInfo;
    private String mUser;
    private static final String TAG = "MainActivity";
    int count = 0;


    //add Firebase Database stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etEmailAddress = (EditText) findViewById(R.id.etEmailAddress);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        tvRegister = (TextView) findViewById(R.id.tvRegister);
        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        //mUser = mAuth.getCurrentUser().getUid();



        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            userInfo = showData(dataSnapshot);
                            if(userInfo.getIsAdmin().equals("true")){
                                Toast.makeText(MainActivity.this, "Welcome " + userInfo.getFullName() , Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this, AdminHome.class));




                            }else{
                                Toast.makeText(MainActivity.this, "Welcome " + userInfo.getFullName(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this, UserHome.class));


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });//whenever data at this location is updated.

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    //Toast.makeText(MainActivity.this, "Successfully Signed out" , Toast.LENGTH_SHORT).show();
                }
            }
        };
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterUser.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String email = etEmailAddress.getText().toString().toLowerCase().trim();
                String password = etPassword.getText().toString();
                userLogin(email, password);
            }
        });
    }

    private void userLogin(String email, String password) {
        if (email.isEmpty()) {
            etEmailAddress.setError("Email is required.");
            etEmailAddress.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmailAddress.setError("Please enter a valid email.");
            etEmailAddress.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password is required.");
            etPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            etPassword.setError("Minimum password length is 6 characters.");
            etPassword.requestFocus();
            return;
        }

        //Toast.makeText(MainActivity.this, "Email: " + email + " Password: " + password  , Toast.LENGTH_LONG).show();
        mAuth.signInWithEmailAndPassword(email, password);
    }

    private User showData(DataSnapshot dataSnapshot) {
        User uInfo = new User();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            String userID = mAuth.getUid();
            uInfo.setFullName(ds.child(userID).getValue(User.class).getFullName());
            uInfo.setEmail(ds.child(userID).getValue(User.class).getEmail());
            uInfo.setPrimaryUserId(ds.child(userID).getValue(User.class).getPrimaryUserId());
            uInfo.setIsAdmin(ds.child(userID).getValue(User.class).getIsAdmin());

            Log.d(TAG, "showData: fullName: " + uInfo.getFullName());
            Log.d(TAG, "showData: email: " + uInfo.getEmail());
            Log.d(TAG, "showData: PrimaryUserId: " + uInfo.getPrimaryUserId());
            Log.d(TAG, "showData: isAdmin: " + uInfo.getIsAdmin());
        }
        return uInfo;
    }
    @Override
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        if(mAuth != null){
            mAuth.signOut();
        }

    }

    @Override
    public void onStop(){
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}








