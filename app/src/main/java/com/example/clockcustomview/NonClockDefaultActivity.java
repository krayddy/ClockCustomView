package com.example.clockcustomview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class NonClockDefaultActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_clock_default);
        Button backButton = findViewById(R.id.NonDefaultClockBackButton);
        Button nextButton = findViewById(R.id.NonDefaultClockNextButton);
        backButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.NonDefaultClockBackButton:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.NonDefaultClockNextButton:
                startActivity(new Intent(this, TimeZonesActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }
}