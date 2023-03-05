package com.example.clockcustomview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TimeZonesActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_zones);
        Button backButton = findViewById(R.id.TimeZonesBackButton);
        Button nextButton = findViewById(R.id.TimeZonesNextButton);
        backButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.TimeZonesBackButton:
                startActivity(new Intent(this, NonClockDefaultActivity.class));
                break;
            case R.id.TimeZonesNextButton:
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, NonClockDefaultActivity.class));
    }
}