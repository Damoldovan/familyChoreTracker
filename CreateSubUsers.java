package com.example.familychoretracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateSubUsers extends AppCompatActivity {
    private static final String TAG = "Create Sub Users";
    private TextView tvBanner;
    private Button btnRegisterSubUser;
    private EditText etUserFullName, etUserEmail, etSetUserPassword;
    private Switch swAdminUser;


    //private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_sub_users);

        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        tvBanner = (TextView) findViewById(R.id.tvBanner);
        btnRegisterSubUser = (Button) findViewById(R.id.btnRegisterSubUser);
        etUserFullName = (EditText) findViewById(R.id.etUserFullName);
        etUserEmail = (EditText) findViewById(R.id.etUserEmail);
        swAdminUser = (Switch) findViewById(R.id.swAdminUser);
        etSetUserPassword = (EditText) findViewById(R.id.etSetUserPassword);

        

        btnRegisterSubUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "OnClick Register User Pressed");
                String email = etUserEmail.getText().toString().toLowerCase().trim();
                String password = etSetUserPassword.getText().toString().trim();
                String fullName = etUserFullName.getText().toString().trim();
                String isAdmin = null;
                String primaryUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                if (swAdminUser.isChecked()) {//checks if admin user switch is checked and sets user as an admin
                    isAdmin = "true";
                }else{
                    isAdmin = "false";
                }

                Log.d(TAG, "btnRegisterSubUser OnClick Attempting to submit to database: \n" +
                                "name: " + fullName + "\n" +
                                "email: " + email + "\n" +
                                "password: " + password + "\n" +
                                "isAdmin: " + isAdmin + "\n"+
                                "primaryUserId: " + primaryUserId);
                registerSubUser(email, password, fullName, isAdmin, primaryUserId);

            }
        });

    }

    private void registerSubUser(String email, String password, String fullName, String isAdmin, String primaryUserId) {
        Log.d(TAG, "Entered registerSubUser method \n" +
                "name: " + fullName + "\n" +
                "email: " + email + "\n" +
                "password: " + password + "\n" +
                "isAdmin: " + isAdmin + "\n"+
                "primaryUserId: " + primaryUserId);

        if (fullName.isEmpty()) {
            etUserFullName.setError("Full name is required.");
            etUserFullName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etUserEmail.setError("Email is required.");
            etUserEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etUserEmail.setError("Please provide valid email.");
            etUserEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etSetUserPassword.setError("Password is required.");
            etSetUserPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etSetUserPassword.setError("Password must be atleast 6 characters.");
            etSetUserPassword.requestFocus();
            return;
        }


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User subUser = new User();
                            subUser.setFullName(fullName);
                            subUser.setEmail(email);
                            subUser.setPrimaryUserId(primaryUserId);
                            subUser.setIsAdmin(isAdmin);


                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(subUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(CreateSubUsers.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(CreateSubUsers.this, AdminHome.class));
                                    } else {
                                        Toast.makeText(CreateSubUsers.this, "Failed to register. Please try again.", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                        } else {
                            Toast.makeText(CreateSubUsers.this, "Failed to create user. Please try again.", Toast.LENGTH_LONG).show();


                        }
                    }
                });


    }
    /*@Override
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop(){
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }*/
}