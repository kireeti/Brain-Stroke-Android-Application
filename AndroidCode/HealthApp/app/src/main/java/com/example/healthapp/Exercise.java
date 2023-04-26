package com.example.healthapp;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Exercise extends Activity {
    TextView tv;
    ListView list;
    String names[] = {
            "it can help to improve cardiovascular health, increase endurance, and reduce the risk of stroke",
            "Regular cycling can help reduce the risk of stroke by reducing risk factors such as high blood pressure and obesity",
            "Swimming is a low-impact exercise that can improve cardiovascular health and increase endurance",
            "Yoga improves balance and coordination, reducing the risk of falls that could lead to a stroke",
            "Jump rope can improve cardiovascular health, aid in weight management, improve coordination, and reduce stress, thus reducing the risk of stroke",
            "Elliptical machine exercise can improve cardiovascular health, aid in weight management, improve joint health, and reduce stress, thus reducing the risk of stroke"
    };
    int images[] = {R.drawable.jogging, R.drawable.cycling, R.drawable.swimming, R.drawable.yoga, R.drawable.jumprope, R.drawable.machine};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_layout);
        tv = (TextView) findViewById(R.id.textView1);
        tv.setText("Exercises List");
        list = (ListView) findViewById(R.id.list);
        ImageList adapter = new ImageList(Exercise.this, names,images);
        list.setAdapter(adapter);
    }
}
