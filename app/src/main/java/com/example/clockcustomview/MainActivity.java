package com.example.clockcustomview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button backButton = findViewById(R.id.DefaultClockBackButton);
        Button nextButton = findViewById(R.id.DefaultClockNextButton);
        backButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.DefaultClockBackButton:
                startActivity(new Intent(this, TimeZonesActivity.class));
                break;
            case R.id.DefaultClockNextButton:
                startActivity(new Intent(this, NonClockDefaultActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, TimeZonesActivity.class));
    }
}