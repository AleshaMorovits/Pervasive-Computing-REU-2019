package com.example.alesha.percomsg;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class UserProfile extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private TextView firstTv;
    private TextView lastTv;
    private TextView bioTv, locationTv;
    private TextView hobbyTv;
    private Uri uri;
    private ImageView profileUserPic;
    private String id;
    private TextView gestureTv;
    private DatabaseReference mDatabase;
    private static final String TAG = "BroadcastTest";
    private Button btnHistory, btnBandSettings,btnEditProfile;
    public StorageReference stoargeReference, imagesRef;
    public StorageReference pathReference;
    private FirebaseStorage storage;
    private Uri profilePicUri;




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
        profileUserPic = findViewById(R.id.profileUserPic);
        btnBandSettings = findViewById(R.id.btnBandSettings);
        btnHistory = findViewById(R.id.activityHistoryBtn);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        btnEditProfile = findViewById(R.id.btnEditProfile);
        id = user.getUid();


        storage = FirebaseStorage.getInstance();
        stoargeReference = storage.getReference();
        pathReference = stoargeReference;
        pathReference = stoargeReference.child("images/"+id);


        File image = null;
        try {
            image = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        final File finalImage = image;
        pathReference.getFile(image)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Successfully downloaded data to local file
                        profilePicUri = Uri.fromFile(finalImage);

                        profileUserPic.setImageURI(profilePicUri);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // ...
            }
        });



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
        btnBandSettings.setOnClickListener(new View.OnClickListener(){
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
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserProfile.this, UserProfileData.class);
                startActivity(i);
            }
        });

       Intent intent = new Intent(this, BandCommunication.class);
        startService(intent);

    }


    @Override
    public void onResume() {
        super.onResume();
  }

    @Override
    public void onPause() {
        super.onPause();
   }

}



