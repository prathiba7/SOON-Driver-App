package com.example.realtimeauto;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    final private int REQ_PERMISSION=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED)
                &&(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)){
            goNext();
        }
        else {
            requestAllPermission();
        }


    }
    void goNext(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            DatabaseReference userref = db.child("Users").child("Riders").child(user.getUid());
            userref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Intent intent = new Intent(MainActivity.this, DriverHome.class);
                        startActivity(intent);
                        finish();
                        return;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
        else {
            Intent intent = new Intent(MainActivity.this, DriverLoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
    }
    @Override
  public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        switch(requestCode){
            case REQ_PERMISSION:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED&&grantResults[1]==PackageManager.PERMISSION_GRANTED) {
                goNext();
                }
                else{
                    Toast.makeText(this,"Some Permissions Denied",Toast.LENGTH_SHORT);
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }
    void requestAllPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},REQ_PERMISSION);
    }
}