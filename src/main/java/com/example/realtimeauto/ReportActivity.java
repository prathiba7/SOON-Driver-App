package com.example.realtimeauto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class ReportActivity extends AppCompatActivity {
    Button accept;
    Button Decline;
    String key;
    private StorageReference mStorageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        final MediaPlayer alert=MediaPlayer.create(this,R.raw.alert);
        alert.start();
        accept=(Button)findViewById(R.id.button2);
        Decline=(Button)findViewById(R.id.button3);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String user_id = user.getUid();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        DatabaseReference riderref = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id).child("CurrentRequest");
        Query query=riderref.orderByChild("Status").equalTo("Requested");
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot ds:snapshot.getChildren()){
                        key=ds.getKey();
                        mStorageReference= FirebaseStorage.getInstance().getReference().child("Reports").child(key+".jpeg");
                        if(mStorageReference!=null) {
                            try {
                                final File LocalFile = File.createTempFile(key, "jpeg");
                                mStorageReference.getFile(LocalFile)
                                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                Toast.makeText(getApplication(),"retreived",Toast.LENGTH_SHORT).show();
                                                Bitmap bitmap= BitmapFactory.decodeFile(LocalFile.getAbsolutePath());
                                                ( (ImageView)findViewById(R.id.reportimage))
                                                        .setImageBitmap(bitmap);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplication(),"not success image",Toast.LENGTH_SHORT).show();
                                    }
                                });


                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        query.addListenerForSingleValueEvent(valueEventListener);
       /*


*/

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                    checksts.child("Users").child("Drivers").child(user_id).child("CurrentRequest").child(key).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.exists()){


                                                Intent intent=new Intent(ReportActivity.this,DriverHome.class);

                                                startActivity(intent);
                                                DatabaseReference setsts=FirebaseDatabase.getInstance().getReference();
                                                setsts.child("Users").child("Drivers").child(user_id).child("CurrentRequest").child(key).child("Status").setValue("Accepted");

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

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
                DatabaseReference riderref = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id).child("CurrentRequest");
                Query query=riderref.orderByChild("Status").equalTo("Declined");
                ValueEventListener valueEventListener=new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot ds:snapshot.getChildren()){
                                key=ds.getKey();
                                if(key!=null){
                                    DatabaseReference checksts=FirebaseDatabase.getInstance().getReference();
                                    checksts.child("Users").child("Drivers").child(user_id).child("CurrentRequest").child(key).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.exists()){
                                                Intent intent=new Intent(ReportActivity.this,DriverHome.class);

                                                startActivity(intent);
                                                DatabaseReference setsts=FirebaseDatabase.getInstance().getReference();
                                                setsts.child("Users").child("Drivers").child(user_id).child("CurrentRequest").child(key).child("Status").setValue("Declined");

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

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