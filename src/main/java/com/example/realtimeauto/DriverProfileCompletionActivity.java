package com.example.realtimeauto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverProfileCompletionActivity extends AppCompatActivity {
    private EditText name,email,address,contactno,yearofexperiance,vehno;
    Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile_completion);
        name=(EditText)findViewById(R.id.name);
        address=(EditText)findViewById(R.id.address);
        contactno=(EditText)findViewById(R.id.contactno);
        email=(EditText)findViewById(R.id.email);
        yearofexperiance=(EditText)findViewById(R.id.yearofexperiance);
        vehno=(EditText)findViewById(R.id.vehno);
        save=(Button)findViewById(R.id.save_button);
        save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                String user_id=user.getUid();
                 DatabaseReference current_user_name = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id).child("Name");
                 current_user_name.setValue(name.getText().toString());
                DatabaseReference current_user_address = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id).child("Address");
                current_user_address.setValue(address.getText().toString());
                DatabaseReference current_user_contactno = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id).child("ContactNo");
                current_user_contactno.setValue(contactno.getText().toString());
                DatabaseReference current_user_email = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id).child("Email");
                current_user_email.setValue(email.getText().toString());
                DatabaseReference current_user_yearofexperiance= FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id).child("YearOfExperiance");
                current_user_yearofexperiance.setValue(yearofexperiance.getText().toString());
                DatabaseReference current_user_vehno = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id).child("VehicleNo");
                current_user_vehno.setValue(vehno.getText().toString());
                startActivity(new Intent(DriverProfileCompletionActivity.this, DriverHome.class));
                finish();
                return;
            }
        });
    }
}