package com.example.realtimeauto;

import android.content.Intent;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    FirebaseUser user;

    {
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @RequiresApi(api = VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(user!=null){
            Intent intent=new Intent(this,ShakeService.class);
            startService(intent);
            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            DatabaseReference databaseReference = (DatabaseReference) db;
            databaseReference.child("Users");
            databaseReference.child("Riders");
            databaseReference.child(user.getUid());
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
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
}