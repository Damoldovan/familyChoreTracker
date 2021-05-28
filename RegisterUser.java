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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity {
    private static final String TAG = "RegisterUser";
    private TextView tvBanner;
    private Button btnRegisterUser;
    private EditText etFullName, etEmail, etSetPassword;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();
        tvBanner = (TextView) findViewById(R.id.tvBanner);
        btnRegisterUser = (Button) findViewById(R.id.btnRegisterUser);
        etFullName = (EditText)findViewById(R.id.etQuestion);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etSetPassword = (EditText)findViewById(R.id.etSetPassword);
        btnRegisterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "OnClick Register User Pressed");
                String email = etEmail.getText().toString().toLowerCase().trim();
                String password = etSetPassword.getText().toString().trim();
                String fullName = etFullName.getText().toString().trim();
                String isAdmin = "true";

                Log.d(TAG, "OnClick Attempting to submit to database: \n" +
                        "name: " + fullName + "\n"+
                        "email: " + email + "\n"+
                        "password: " + password + "\n"+
                        "isAdmin: " + isAdmin + "\n"
                );
            registerUser(email, password, fullName, isAdmin);
            }
        });

        tvBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterUser.this, MainActivity.class));
            }
        });
    }



    //Register user method to create new users
    private void registerUser(String email, String password, String fullName, String isAdmin) {
        Log.d(TAG, "Entered registerUser method \n" +
                "name: " + fullName + "\n"+
                "email: " + email + "\n"+
                "password: " + password + "\n"+
                "isAdmin: " + isAdmin + "\n"
        );

        if(fullName.isEmpty()){
            etFullName.setError("Full name is required.");
            etFullName.requestFocus();
            return;
        }

       if(email.isEmpty()){
            etEmail.setError("Email is required.");
            etEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError("Please provide valid email.");
            etEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            etSetPassword.setError("Password is required.");
            etSetPassword.requestFocus();
            return;
        }

        if(password.length() < 6){
            etSetPassword.setError("Password must be atleast 6 characters.");
            etSetPassword.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful()){
                           String primaryUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                           User user = new User();
                           user.setFullName(fullName);
                           user.setEmail(email);
                           user.setPrimaryUserId(primaryUserId);
                           user.setIsAdmin(isAdmin);

                           FirebaseDatabase.getInstance().getReference("Users")
                           .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                   .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if(task.isSuccessful()){
                                       Toast.makeText(RegisterUser.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                                       startActivity( new Intent(RegisterUser.this, MainActivity.class));
                                   }else{
                                       Toast.makeText(RegisterUser.this, "Failed to register. Please try again.", Toast.LENGTH_LONG).show();
                                   }

                               }
                           });
                       }else{
                           Toast.makeText(RegisterUser.this, "Failed to create user. Please try again.", Toast.LENGTH_LONG).show();
                       }
                    }
                });


    }


}