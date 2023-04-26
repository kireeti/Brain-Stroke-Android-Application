package com.example.healthapp;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Suggestion extends Activity {
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suggestion_layout);
        tv = (TextView) findViewById(R.id.suggestion);
        String output = "Previous Prediction : "+UserDetails.prediction+"\n\nSuggestions: "+UserDetails.suggestions;
        tv.setText(output);
        //tv.setTextColor(color.BLACK);
    }
}
