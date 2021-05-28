package com.example.familychoretracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EditChores extends AppCompatActivity {
    Button btnCreateChore, btnDeleteChore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_chores);
        btnCreateChore = (Button)findViewById(R.id.btnCreateChore);
        btnDeleteChore = (Button)findViewById(R.id.btnDeleteChore);

        btnDeleteChore.setEnabled(false); // disable delete chore button until program is updated to delete chores

        btnCreateChore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditChores.this, CreateChore.class));
            }


        });
    }

}