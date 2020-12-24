package com.example.realtimeauto;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RealTimeLocationService extends Service implements LocationListener {
    LocationManager mLocationManager;
    Location mLastLocation;
    String key;
    String str;
    String requestId="";
    boolean check=true;
    private StorageReference mStorageReference;
    ImageView img;





    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
           mLastLocation=mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
           mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,(android.location.LocationListener)this);
        }
        return START_STICKY;
    }


    @Override
    public void onLocationChanged(Location location) {
        mLastLocation=location;


        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dref=FirebaseDatabase.getInstance().getReference();

        dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                str=snapshot.child("Users").child("Drivers").child(userId).child("Available").getValue().toString();
                if(str.equals("on")) {
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                    db.child("DriverAvailable").child(userId);

                    db.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DriverAvailable");
                                GeoFire geoFire = new GeoFire(ref);
                                geoFire.setLocation(userId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }

                    });
                }else{
                    String DriverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DriverAvailable");
                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.removeLocation(DriverId);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });
        DatabaseReference riderref = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("CurrentRequest");
        Query query=riderref.orderByChild("Status").equalTo("Requested");
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot ds:snapshot.getChildren()){
                        key=ds.getKey();
                        if(key!=null){
                            DatabaseReference checksts=FirebaseDatabase.getInstance().getReference();
                            checksts.child("Users").child("Drivers").child(userId).child("CurrentRequest").child(key).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        String sts=snapshot.child("Status").getValue().toString();
                                        if(sts.equals("Requested")){
                                            mStorageReference= FirebaseStorage.getInstance().getReference().child("Reports").child(key+".jpeg");
                                            mStorageReference.getDownloadUrl()
                                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {

                                                            Intent intent1 = new Intent(RealTimeLocationService.this, ReportActivity.class)
                                                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


                                                            startActivity(intent1);






                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Intent intent1 = new Intent(RealTimeLocationService.this, RequestActivity.class)
                                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


                                                    startActivity(intent1);




                                                }
                                            });
                                        }

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

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}


