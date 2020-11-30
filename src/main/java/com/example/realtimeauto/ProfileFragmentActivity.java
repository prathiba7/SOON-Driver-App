package com.example.realtimeauto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragmentActivity extends Fragment {
    View view;
    private EditText EnterName,EnterPhone,Enteraddress,VehicleNo;
    private Button UpdateButton;
    private FirebaseUser user;
    private DatabaseReference ref;
   String userID;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.activity_profile_fragment,container,false);

        user= FirebaseAuth.getInstance().getCurrentUser();
        userID=user.getUid();
        EnterName=(EditText)rootView.findViewById(R.id.EnterName);
        EnterPhone=(EditText)rootView.findViewById(R.id.EnterPhone);
        Enteraddress=(EditText)rootView.findViewById(R.id.Enteraddress);
        VehicleNo=(EditText)rootView.findViewById(R.id.VehicleNo);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Users").child("Drivers").child(userID).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("Name").getValue().toString();
                String phone = snapshot.child("Phoneno").getValue().toString();
                String address = snapshot.child("Address").getValue().toString();
                String vehicleno = snapshot.child("VehicleNo").getValue().toString();

                EnterName.setText(name);
                EnterPhone.setText(phone);
                Enteraddress.setText(address);
                VehicleNo.setText(vehicleno);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        UpdateButton=(Button)rootView.findViewById(R.id.UpdateButton);
        UpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference editref= FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userID);
                editref.child("Name").setValue(EnterName.getText().toString());
                editref.child("Phoneno").setValue(EnterPhone.getText().toString());
                editref.child("Address").setValue(Enteraddress.getText().toString());
                editref.child("VehicleNo").setValue(VehicleNo.getText().toString());
                Toast.makeText(getActivity(),"Updated Successfully",Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }


}