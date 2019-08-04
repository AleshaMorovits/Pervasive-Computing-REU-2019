package com.example.alesha.percomsg;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class UserProfileData extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    public StorageReference stoargeReference, imagesRef;
    private FirebaseUser user;
    private String TAG = " DATABASE";
    public StorageReference pathReference;
    private ImageView viewProfilePic;
    private Button saveB, btnSelectProfilePic,btnUpdateLocation;
    public int cnt = 0;
    private EditText first_name, last_name, bio;
    private DatabaseReference mDatabase;
    private String id, hobbiesChecked;
    private ArrayList<String> hobbyArr = new ArrayList<String>();
    private ImageView imageView;
    private Uri profilePicUri;
    private int location;
    private TextView locationTv;
    public GPSTracker gpsTracker;
    private static final int REQUEST_CODE_PERMISSION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_data);
        String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;

        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        locationTv = findViewById(R.id.locationTv);
        btnUpdateLocation = findViewById(R.id.btnUpdateLocation);
        saveB = findViewById(R.id.saveBtn);
       first_name = findViewById(R.id.field_fname);
        last_name = findViewById(R.id.field_lname);
        viewProfilePic = findViewById(R.id.viewProfilePic);
        bio = findViewById(R.id.field_bio);
        btnSelectProfilePic = findViewById(R.id.btnSelectProfilePic);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        id = user.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        saveB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {

                    UserData data = new UserData( first_name.getText().toString(),last_name.getText().toString(),bio.getText().toString(), hobbyArr);

                    mDatabase.child("data").child(user.getUid()).setValue(data, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Toast.makeText(UserProfileData.this, "Data could not be saved " + databaseError.getMessage(),
                                        Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(UserProfileData.this, "Data saved successfully.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    ValueEventListener postListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get Post object and use the values to update the UI
                            UserData data = dataSnapshot.getValue(UserData.class);

                            if (data != null) {
                             first_name.setText(data.getFirst_name());
                               last_name.setText(data.getLast_name());
                                bio.setText(data.getBio());
                                hobbyArr = (data.getHobbyArr());
                            }
                            Intent i = new Intent(UserProfileData.this, UserProfile.class);
                            startActivity(i);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        }
                    };
                    mDatabase.child("data").child(user.getUid()).addListenerForSingleValueEvent(postListener);
                }
            }
        });
    //https://www.tutorialspoint.com/android/android_location_based_services.htm
        // show location button click event
        btnUpdateLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // create class object
                gpsTracker = new GPSTracker(UserProfileData.this);

                // check if GPS enabled
                if(gpsTracker.canGetLocation()){

                    double latitude = gpsTracker.getLatitude();
                    double longitude = gpsTracker.getLongitude();

                    // \n is for new line;
                    locationTv.setText( "Your Location is - \nLat: "
                            + latitude + "\nLong: " + longitude);

                }else{
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gpsTracker.showSettingsAlert();
                }

            }
        });







        //https://www.c-sharpcorner.com/UploadFile/e14021/capture-image-from-camera-and-selecting-image-from-gallery-o/
        //
        btnSelectProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BandCommunication bc = new BandCommunication();
                BandCommunication.verifyStoragePermissions(UserProfileData.this);
                selectProfilePic();
            }
        });

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

                       viewProfilePic.setImageURI(profilePicUri);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // ...
            }
        });



    }//end oncreate

    private void selectProfilePic() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileData.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);
                    viewProfilePic.setImageBitmap(bitmap);
                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, System.currentTimeMillis() + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                Log.w("path of image...", picturePath + "");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.PNG,100,stream);


                pathReference.putFile(selectedImage)
                       .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                           @Override
                           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                           }
                       })
                       .addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception exception) {
                               // Handle unsuccessful uploads
                               // ...
                           }
                       });



            }
        }
    }


    public void onCheckBoxClick(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.readBox:
                if (checked) {
                    hobbyArr.add(" Read");
                    cnt++;
                }
                break;
            case R.id.hangoutBox:
                if (checked) {
                    hobbyArr.add(" Hangout with friends");
                    cnt++;
                }
                break;
            case R.id.videogameBox:
                if (checked) {
                    hobbyArr.add(" Play video games");
                    cnt++;
                }
                break;
            case R.id.workoutBox:
                if (checked) {
                    hobbyArr.add(" Workout");
                    cnt++;
                }
                break;
            case R.id.sleepBox:
                if (checked) {
                    hobbyArr.add(" Sleep");
                    cnt++;
                }
                break;
            case R.id.danceBox:
                if (checked) {
                    hobbyArr.add(" Dance");
                    cnt++;
                }
                break;
            case R.id.huntingBox:
                if (checked) {
                    hobbyArr.add(" Hunting");
                    cnt++;
                }
                break;
            case R.id.cookingBox:
                if (checked) {
                    hobbyArr.add(" Cook");
                    cnt++;
                }
                break;
            case R.id.musicBox:
                if (checked) {
                    hobbyArr.add(" Listen to music");
                    cnt++;
                }
                break;
        }
    }
}



