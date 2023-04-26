package com.example.healthapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class UserScreen extends Activity {
    TextView tv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usermodules_layout);
        initClickListner();
    }

    private void initClickListner() {
        tv = (TextView) findViewById(R.id.textView1);
        tv.setText("Welcome " + UserDetails.username);
        Button account = (Button) findViewById(R.id.strokeprediction);
        account.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(UserScreen.this, StrokePrediction.class);
                startActivity(intent);
            }
        });

        Button transfer = (Button) findViewById(R.id.suggestion);
        transfer.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(UserScreen.this, Suggestion.class);
                startActivity(intent);
            }
        });

        Button statement = (Button) findViewById(R.id.exercises);
        statement.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(UserScreen.this, Exercise.class);
                startActivity(intent);
            }
        });

        Button logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(UserScreen.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}


