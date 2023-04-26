package com.example.healthapp;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.ArrayList;

public class StrokePrediction extends Activity {
    EditText age, glucose, bmi;
    Button predict, back;
    DBConnect db;
    Spinner gender, hypertension, heartdisease, smoking;
    Interpreter interpreter;
    String labels[] = {"No Brain Stroke Detected", "Brain Stroke Detected"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.strokeprediction_layout);
        String permissions[] = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if(!hasPermissions(this, permissions)){
            ActivityCompat.requestPermissions(this, permissions, 42);
        }
        try {
            interpreter = new Interpreter(loadTensorModelFile(),null);
            System.out.println("=================================interpreter "+interpreter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        db = new DBConnect(this);
        age = (EditText) findViewById(R.id.age);
        gender = (Spinner) findViewById(R.id.gender);
        hypertension = (Spinner) findViewById(R.id.hypertension);
        heartdisease = (Spinner) findViewById(R.id.heartdisease);
        glucose = (EditText) findViewById(R.id.glucose);
        bmi = (EditText) findViewById(R.id.bmi);
        smoking = (Spinner) findViewById(R.id.smokingstatus);

        List<String> gender_values = new ArrayList<String>();
        gender_values.add("Choose Your Gender");
        gender_values.add("Male");
        gender_values.add("Female");
        ArrayAdapter<String> adapter_data = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, gender_values);
        adapter_data.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter_data);

        List<String> hyper_values = new ArrayList<String>();
        hyper_values.add("Hypertension Status");
        hyper_values.add("No");
        hyper_values.add("Yes");
        ArrayAdapter<String> hyper_data = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, hyper_values);
        hyper_data.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hypertension.setAdapter(hyper_data);

        List<String> heart_values = new ArrayList<String>();
        heart_values.add("Heart Disease Status");
        heart_values.add("No");
        heart_values.add("Yes");
        ArrayAdapter<String> heart_data = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, heart_values);
        heart_data.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        heartdisease.setAdapter(heart_data);

        List<String> smoking_values = new ArrayList<String>();
        smoking_values.add("Smoking Status");
        smoking_values.add("Unknown");
        smoking_values.add("Formerly Smoked");
        smoking_values.add("Never Smoked");
        smoking_values.add("Smokes");
        ArrayAdapter<String> smoking_data = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, smoking_values);
        smoking_data.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        smoking.setAdapter(smoking_data);

        predict = (Button) findViewById(R.id.predict);
        predict.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                try {
                    predictStroke();
                } catch (Exception e) {
                    Toast.makeText(StrokePrediction.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(StrokePrediction.this, UserScreen.class);
                startActivity(intent);
            }
        });
    }


    private MappedByteBuffer loadTensorModelFile() throws IOException {
        AssetFileDescriptor asset_FileDescriptor = this.getAssets().openFd("model/health_model.tflite");
        FileInputStream file_InputStream = new FileInputStream(asset_FileDescriptor.getFileDescriptor());
        FileChannel file_Channel = file_InputStream.getChannel();
        long start_Offset = asset_FileDescriptor.getStartOffset();
        long length = asset_FileDescriptor.getLength();
        MappedByteBuffer buffer = file_Channel.map(FileChannel.MapMode.READ_ONLY,start_Offset,length);
        file_Channel.close();
        return buffer;
    }

    public void predictStroke() {
        //read values from fields
        float age_value = Float.parseFloat(age.getText().toString().trim());
        String gender_value = gender.getSelectedItem().toString().trim();
        String hyper_value = hypertension.getSelectedItem().toString().trim();
        String heart_value = heartdisease.getSelectedItem().toString().trim();
        float glucose_value = Float.parseFloat(glucose.getText().toString().trim());
        float bmi_value = Float.parseFloat(bmi.getText().toString().trim());
        String smoking_value = smoking.getSelectedItem().toString().trim();
        float smoking_status = 0.0f;
        if(smoking_value.equals("Formerly Smoked")){
            smoking_status = 1.0f;
        }
        if(smoking_value.equals("Formerly Smoked")){
            smoking_status = 1.0f;
        }
        if(smoking_value.equals("Never Smoked")){
            smoking_status = 2.0f;
        }
        if(smoking_value.equals("Smokes")){
            smoking_status = 3.0f;
        }
        float gender_status = 0.0f;
        if(gender_value.equals("Male")) {
            gender_status = 1.0f;
        }
        float hyper_status = 0.0f;
        if(hyper_value.equals("Yes")) {
            hyper_status = 1.0f;
        }
        float heart_status = 0.0f;
        if(heart_value.equals("Yes")) {
            heart_status = 1.0f;
        }
        float input_data[][][][] = new float[1][7][1][1];
        float value[][][][] = new float[1][7][1][1];
        value[0][0][0][0] = gender_status;
        value[0][1][0][0] = age_value;
        value[0][2][0][0] = hyper_status;
        value[0][3][0][0] = heart_status;
        value[0][4][0][0] = glucose_value;
        value[0][5][0][0] = bmi_value;
        value[0][6][0][0] = smoking_status;
        float outputs[][] = new float[1][2];
        interpreter.run(value,outputs);
        float out[] = new float[2];
        for(int i=0;i<outputs.length;i++){
            for(int j=0;j<outputs[i].length;j++) {
                System.out.println("output = " + outputs[i][j]);
                out[j] = outputs[i][j];
            }
        }
        int max_index = 0;
        for (int i = 0; i < out.length; i++) {
            max_index = out[i] > out[max_index] ? i : max_index;
        }
        String suggestion = "Congrats You are Healthy\nKeep Exercising & Stay Tension Free";
        if(labels[max_index].equals("Brain Stroke Detected")) {
            suggestion = "Brain Stroke Detected\nPlease exercise dialy & take healthy food\nStay Tension Free for speedy Recovery";
        }
        java.util.Date dd = new java.util.Date();
        java.sql.Timestamp time = new java.sql.Timestamp(dd.getTime());
        db.saveActivity(UserDetails.username,labels[max_index],suggestion,time.toString());
        Toast.makeText(StrokePrediction.this, "Predicted Output : "+labels[max_index], Toast.LENGTH_LONG).show();
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}



