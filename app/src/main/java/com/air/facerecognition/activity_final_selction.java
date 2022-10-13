package com.air.facerecognition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class activity_final_selction extends AppCompatActivity {
    TextView t1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_selction);

        t1 = findViewById(R.id.textView2);

        Intent i = getIntent();
        t1.setText(i.getStringExtra("Result"));
    }
}