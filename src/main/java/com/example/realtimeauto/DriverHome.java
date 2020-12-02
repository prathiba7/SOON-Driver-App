package com.example.realtimeauto;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class DriverHome extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{


    DrawerLayout drawer;
    NavigationView navigationView;
    FrameLayout frameLayout;
    FrameLayout frameMap;
    FrameLayout nav;
    ImageView imageView;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    View header;
    TextView name, phone;
    Switch sw;
    String driver;
    private GoogleMap mMap;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private SupportMapFragment mapFragment;
    GoogleApiClient mGoogleApiClient;
    Boolean first=true;
      String key1;
    private StorageReference mStorageReference;
    BottomNavigationView bottomNavigationView;
    BottomNavigationView reportbottomNavigationView;
     Marker ridermarker;
    Marker drivermarker;
     Boolean check=true;




    LatLng riderLatLng;
    double locationLat=0;
    double locationLng=0;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);


        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        frameLayout = (FrameLayout) findViewById(R.id.frame);
        frameMap = (FrameLayout) findViewById(R.id.map);
        nav = (FrameLayout) findViewById(R.id.frame1);
        header = navigationView.getHeaderView(0);
        imageView = (ImageView) header.findViewById(R.id.nav_img);
        name = (TextView) header.findViewById(R.id.username);
        phone = (TextView) header.findViewById(R.id.userphone);

        reportbottomNavigationView=(BottomNavigationView)findViewById(R.id.reportbottomNav);
        sw = (Switch) findViewById(R.id.switch5);
        bottomNavigationView=(BottomNavigationView)findViewById(R.id.bottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.driverdet:
                        RiderDetails();
                        break;
                    case R.id.driverroute:
                        showroute(riderLatLng,mLastLocation);
                        break;
                    case R.id.complete:
                        removeRequest();
                        break;
                }
                return true;
            }
        });
        reportbottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){

                    case R.id.driverroute:
                        showroute(riderLatLng,mLastLocation);
                        break;
                    case R.id.complete:
                        removeRequest();
                        break;
                }
                return true;
            }
        });



        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        final Intent intentlocservice = new Intent(this, RealTimeLocationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intentlocservice);
        }

        final Intent intentlocs = new Intent(this, RealTimeLocationService.class);
        startService(intentlocs);

        mapFragment.getMapAsync(this);
        frameMap.setVisibility(View.GONE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String user_id = user.getUid();
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    DatabaseReference dr = FirebaseDatabase.getInstance().getReference();

                    dr.child("Users").child("Drivers").child(user_id).child("Available").setValue("on");

                    frameLayout.setVisibility(View.GONE);
                    frameMap.setVisibility(View.VISIBLE);

                } else {
                    DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
                    dr.child("Users").child("Drivers").child(user_id).child("Available").setValue("off");

                    frameLayout.setVisibility(View.VISIBLE);
                    frameMap.setVisibility(View.GONE);
                    disConnectDriver();


                }

            }
        });
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id).child("Available");
        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue().equals("on")){
                    frameMap.setVisibility(View.VISIBLE);
                    frameLayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        DatabaseReference dr1 = FirebaseDatabase.getInstance().getReference();

        ((com.google.firebase.database.DatabaseReference) dr1).child("Users").child("Drivers").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                String uname = snapshot.child("Name").getValue().toString();
                String phno = snapshot.child("Phoneno").getValue().toString();
                name.setText(uname);
                phone.setText(phno);}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.nav_Home) {
                    DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id).child("Available");
                    dr.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.getValue().equals("on")){
                                frameMap.setVisibility(View.VISIBLE);
                                nav.setVisibility(View.GONE);
                            }
                            else{
                                frameLayout.setVisibility(View.VISIBLE);
                                nav.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                } else if (id == R.id.nav_about) {
                    loadFragment(new AboutFragmentActivity());

                } else if (id == R.id.nav_profile) {
                    loadFragment(new ProfileFragmentActivity());

                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });


    }



    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame1, fragment);
        frameLayout.setVisibility(View.GONE);
        frameMap.setVisibility(View.GONE);
        nav.setVisibility(View.VISIBLE);
        transaction.commit();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        buildApiClient();
        mMap.setMyLocationEnabled(true);


    }

    protected synchronized void buildApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void  RiderDetails(){

        final AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle("Casualty Details");
        DatabaseReference dref=FirebaseDatabase.getInstance().getReference().child("Users").child("Riders").child(key1);
        if(dref!=null) {
            dref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("Name").getValue().toString();

                        String phno = snapshot.child("Phoneno").getValue().toString();
                        String vhno = snapshot.child("VehicleNo").getValue().toString();
                        String emergencyContact1 = snapshot.child("EmergencyContact1").getValue().toString();
                        String emergencyContact2 = snapshot.child("EmergencyContact2").getValue().toString();

                        dialog.setMessage("Name: " + name + "\nEmergencycontact: " + emergencyContact1 + "\n"+emergencyContact2 + "\nPhone Number: " + phno + "\nVehicle Number: " + vhno);
                        dialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        AlertDialog dialog1 = dialog.create();
                        dialog1.show();
                    }

                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }




    }
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation=location;
        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
        if(drivermarker!=null){
            drivermarker.remove();
        }

            drivermarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_auto))
                .title("Driver")
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userid=user.getUid();

        DatabaseReference dr1 = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userid).child("Available");
        if(dr1!=null) {
            dr1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.getValue().equals("on")) {
                            sw.setChecked(true);
                            String DriverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DriverAvailable");
                            GeoFire geoFire = new GeoFire(ref);
                            geoFire.setLocation(DriverId, new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()));
                        }else{
                            sw.setChecked(false);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }



         driver=FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rider = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driver).child("CurrentRequest");

        Query query=rider.orderByChild("Status").equalTo("Accepted");
        if (rider!=null) {
            ValueEventListener valueEventListener1 = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            key1 = ds.getKey();
                            if (key1 != null) {
                                DatabaseReference checksts = FirebaseDatabase.getInstance().getReference();
                                checksts.child("Users").child("Drivers").child(driver).child("CurrentRequest").child(key1).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            String sts = snapshot.child("Status").getValue().toString();
                                            if (sts.equals("Accepted")) {


                                                getRiderLocation();

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
            query.addListenerForSingleValueEvent(valueEventListener1);
        }

    }




    public  void getRiderLocation(){
        mStorageReference= FirebaseStorage.getInstance().getReference().child("Reports").child(key1+".jpeg");
        mStorageReference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        reportbottomNavigationView.setVisibility(View.VISIBLE);
                        bottomNavigationView.setVisibility(View.GONE);




                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                bottomNavigationView.setVisibility(View.VISIBLE);
                reportbottomNavigationView.setVisibility(View.GONE);




            }
        });

        if(check==true) {

            DatabaseReference assignedRiderPickupLocationRef = FirebaseDatabase.getInstance().getReference().child("Requests").child(key1).child("l");

            assignedRiderPickupLocationRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        List<Object> map = (List<Object>) snapshot.getValue();


                        if (map.get(0) != null) {
                            locationLat = Double.parseDouble(map.get(0).toString());
                        }
                        if (map.get(1) != null) {
                            locationLng = Double.parseDouble(map.get(1).toString());
                        }
                        riderLatLng = new LatLng(locationLat, locationLng);
                        if (ridermarker != null) {
                            ridermarker.remove();
                        }

                        ridermarker = mMap.addMarker(new MarkerOptions()
                                .position(riderLatLng)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                .title("Casualty")
                        );


                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }


    private void showroute(LatLng riderLatLng, Location mLastLocation) {
        Uri uri=Uri.parse("https://www.google.co.in/maps/dir/"+mLastLocation.getLatitude()+","+mLastLocation.getLongitude()+"/"+locationLat+","+locationLng);
        Intent intent =new Intent(Intent.ACTION_VIEW,uri);
        intent.setPackage("com.google.android.apps.maps");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }




    private void removeRequest() {
        mStorageReference= FirebaseStorage.getInstance().getReference().child("Reports").child(key1+".jpeg");
        if(mStorageReference!=null){
            mStorageReference.delete();
        }
        ridermarker.remove();
        DatabaseReference rider1 = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driver).child("CurrentRequest").child(key1).child("Status");
        rider1.setValue("completed");
        check=false;

        bottomNavigationView.setVisibility(View.GONE);
        reportbottomNavigationView.setVisibility(View.GONE);


    }


    public  void disConnectDriver(){
        String DriverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DriverAvailable");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(DriverId);
    }
}