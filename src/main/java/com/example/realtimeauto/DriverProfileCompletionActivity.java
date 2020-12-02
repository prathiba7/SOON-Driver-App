package com.example.realtimeauto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverProfileCompletionActivity extends AppCompatActivity {
    private EditText name,address,vehno;
    Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile_completion);
        name=(EditText)findViewById(R.id.name);
        address=(EditText)findViewById(R.id.address);

        vehno=(EditText)findViewById(R.id.vehno);
        save=(Button)findViewById(R.id.save_button);
        save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {


                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                String user_id=user.getUid();
                if ((!name.getText().toString().equals("")) && (!address.getText().toString().equals(""))&& (!vehno.getText().toString().equals(""))) {
                    DatabaseReference current_user_name = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id).child("Name");
                    current_user_name.setValue(name.getText().toString());
                    DatabaseReference current_user_address = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id).child("Address");
                    current_user_address.setValue(address.getText().toString());


                    DatabaseReference current_user_vehno = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id).child("VehicleNo");
                    current_user_vehno.setValue(vehno.getText().toString());
                    DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id);
                    dr.child("Available").setValue("off");
                    startActivity(new Intent(DriverProfileCompletionActivity.this, DriverHome.class));
                    finish();
                    return;
                }
                else{
                    Toast.makeText(DriverProfileCompletionActivity.this, "Some fields are left blank", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}