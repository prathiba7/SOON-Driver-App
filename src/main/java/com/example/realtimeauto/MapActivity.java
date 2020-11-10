package com.example.realtimeauto;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MapActivity extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, LocationListener {
        View view;
        GoogleMap mMap;
        SupportMapFragment mapFragment;
        GoogleApiClient googleApiClient;
        Location mLastLocation;
        private String customerId="";
        LocationRequest lr;
        private Object Bundle;


        @Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_map, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        mapFragment = SupportMapFragment.newInstance();
        ft.replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        getAssignedRider();
        return view;
        }
        private void getAssignedRider(){
                String DriverId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference assignedRiderRef=FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(DriverId).child("RequestId");
                assignedRiderRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                                customerId=snapshot.getValue().toString();
                                                getAssignedRiderPickupLocation();
                                }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                });

        }
        private void getAssignedRiderPickupLocation(){

                DatabaseReference assignedRiderPickupLocationRef=FirebaseDatabase.getInstance().getReference().child("Requests").child(customerId).child("l");
                assignedRiderPickupLocationRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                        List<Object> map=(List<Object>)snapshot.getValue();
                                        double locationLat=0;
                                        double locationLng=0;
                                        if(map.get(0) != null){
                                                locationLat = Double.parseDouble(map.get(0).toString());
                                        }
                                        if(map.get(1) != null){
                                                locationLng = Double.parseDouble(map.get(1).toString());
                                        }
                                        LatLng driverLatLng = new LatLng(locationLat,locationLng);
                                        mMap.addMarker(new MarkerOptions().position(driverLatLng).title("pickup location"));


                                }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                });

        }

@Override
public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        return;
        }
        mMap.setMyLocationEnabled(true);
        buildClient();
//         LatLng latLng=new LatLng(22.5,88.7);
        //       MarkerOptions markerOptions=new MarkerOptions();
        //    markerOptions.position(latLng);
        //  markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        //  markerOptions.title("driver");
        //markerOptions.snippet("mY POSITION");
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        //mMap.addMarker(markerOptions);
        }

private void buildClient() {
        googleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
        .addConnectionCallbacks(this)
        .addApi(LocationServices.API)
        .build();
        googleApiClient.connect();
        }

@Override
public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(getActivity(), "Map Connected", Toast.LENGTH_SHORT).show();
        LocationRequest lr = new LocationRequest();
        lr.setInterval(1000);
        lr.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


        return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, lr, this);
        }

@Override
public void onConnectionSuspended(int i) {

        }



        public void onLocationChanged(final Location location) {

                        mLastLocation = location;
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        final MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                        markerOptions.title("driver");
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
                       mMap.addMarker(markerOptions);
                        String DriverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DriverAvailable");
                        GeoFire geoFire = new GeoFire(ref);
                        geoFire.setLocation(DriverId, new GeoLocation(location.getLatitude(), location.getLongitude()));




        }





}