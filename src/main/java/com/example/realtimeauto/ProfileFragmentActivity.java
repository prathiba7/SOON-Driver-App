package com.example.realtimeauto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class ProfileFragmentActivity extends Fragment {

    View view;

    private TextView nameTxtView,contactTxtView;
    private TextView addressTxtView,vehiclenoTxtview;
    private ImageView driverImageView, phoneImageView,vehicleImageView;

    private final String TAG = this.getClass().getName().toUpperCase();
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private Map<String, String> driverid;

    private String userid;
    private static final String DRIVERS = "drivers";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.activity_profile_fragment,container,false);
        return view;
    }
}