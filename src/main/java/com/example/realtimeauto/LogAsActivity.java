package com.example.realtimeauto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;



public class LogAsActivity extends AppCompatActivity {
    private Button mDriver,mRider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_as);
        mDriver=(Button) findViewById(R.id.driver);
        mRider=(Button) findViewById(R.id.rider);
        mDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LogAsActivity.this,DriverLoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
        mDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LogAsActivity.this,DriverLoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }
}