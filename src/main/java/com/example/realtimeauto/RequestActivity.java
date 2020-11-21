package com.example.realtimeauto;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.skyfishjy.library.RippleBackground;


public class RequestActivity extends AppCompatActivity {
          RippleBackground ripple;
        Button accept;
        Button Decline;
        String key;
        private StorageReference mStorageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        final MediaPlayer alert=MediaPlayer.create(this,R.raw.alert);
        alert.start();



        accept=(Button)findViewById(R.id.button2);
        Decline=(Button)findViewById(R.id.button3);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String user_id = user.getUid();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RequestActivity.this,DriverHome.class);

                startActivity(intent);
                DatabaseReference riderref = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id).child("CurrentRequest");
                Query query=riderref.orderByChild("Status").equalTo("Requested");
                ValueEventListener valueEventListener=new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot ds:snapshot.getChildren()){
                                 key=ds.getKey();
                                if(key!=null){
                                    DatabaseReference checksts=FirebaseDatabase.getInstance().getReference();
                                    checksts.child("Users").child("Drivers").child(user_id).child("CurrentRequest").child(key).child("Status").setValue("Accepted");
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                query.addListenerForSingleValueEvent(valueEventListener);






            }
        });
        Decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(RequestActivity.this,DriverHome.class);

                startActivity(intent1);
                DatabaseReference riderref = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id).child("CurrentRequest");
                Query query=riderref.orderByChild("Status").equalTo("Requested");
                ValueEventListener valueEventListener=new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot ds:snapshot.getChildren()){
                                 key=ds.getKey();
                                if(key!=null){
                                    DatabaseReference checksts=FirebaseDatabase.getInstance().getReference();
                                    checksts.child("Users").child("Drivers").child(user_id).child("CurrentRequest").child(key).child("Status").setValue("Declined");
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                query.addListenerForSingleValueEvent(valueEventListener);



            }
        });


    }
}