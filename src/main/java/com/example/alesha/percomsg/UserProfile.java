package com.example.alesha.percomsg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.microsoft.band.BandClient;


public class UserProfile extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private TextView firstTv;
    private TextView lastTv;
    private TextView bioTv, locationTv;
    private TextView hobbyTv;
    private String chainTv;
    private TextView gestureTv;
    private ImageButton editProfileBtn,btnConsent;
    private DatabaseReference mDatabase;
    // private String TAG = "UserProfile";
    private BandClient client = null;
    private static final String TAG = "BroadcastTest";
    private Intent intent;
    private Button btnHistory;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        firstTv = findViewById(R.id.firstTv);
        lastTv = findViewById(R.id.lastTv);
        bioTv = findViewById(R.id.bioTv);
        hobbyTv = findViewById(R.id.hobbyTv);
        editProfileBtn = findViewById(R.id.editProfileBtn);
        btnConsent = findViewById(R.id.btnConsent);
        btnHistory = findViewById(R.id.activityHistoryBtn);


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //  intent = new Intent(this, BandCommunication.class);

        // Write a message to the database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if (user != null) {
            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserData data = dataSnapshot.getValue(UserData.class);

                    if (data != null) {
                        firstTv.setText(data.getFirst_name());
                        lastTv.setText(data.getLast_name());
                        bioTv.setText(data.getBio());
                        hobbyTv.setText(data.getHobbyArr().toString());
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                }
            };
            mDatabase.child("data").child(user.getUid()).addListenerForSingleValueEvent(postListener);

        }
        btnConsent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)  {
                Toast.makeText(UserProfile.this, "Get Consent of Band Usage",
                        Toast.LENGTH_LONG).show();
               Intent i = new Intent(UserProfile.this, BandCommunication.class);
               startActivity(i);

            }
        });
        btnHistory.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserProfile.this, ActivityHistory.class);
                startActivity(i);
            }
        });
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserProfile.this, UserProfileData.class);
                startActivity(i);
            }
        });
       Intent intent = new Intent(this, BandCommunication.class);
        startService(intent);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };
    @Override
    public void onResume() {
        super.onResume();
//        startService(intent);
  //     registerReceiver(broadcastReceiver, new IntentFilter(BandCommunication.BROADCAST_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
//        unregisterReceiver(broadcastReceiver);
  //      stopService(intent);
    }




}



