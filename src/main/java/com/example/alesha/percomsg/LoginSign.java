package com.example.alesha.percomsg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.ref.WeakReference;

public class LoginSign extends AppCompatActivity {

    private String TAG = "LOGIN";
    private FirebaseAuth mAuth;
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button signInBtn, signUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign);
        final WeakReference<Activity> reference = new WeakReference<Activity>(this);

        mAuth = FirebaseAuth.getInstance();


        // Views
        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);
        // Buttons
        signInBtn = findViewById(R.id.emailSignInBtn);
        signUpBtn = findViewById(R.id.emailSignUpBtn);



        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginSign.this, SignUpActivity.class);
                startActivity(i);
            }
        });
    }
    public void login() {
        Toast.makeText(LoginSign.this, "Email: " + mEmailField.getText().toString() + " Password: " + mPasswordField.getText().toString(),
                Toast.LENGTH_SHORT).show();
        mAuth.signInWithEmailAndPassword(mEmailField.getText().toString(), mPasswordField.getText().toString())
                .addOnCompleteListener(LoginSign.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String uid = user.getUid();
                                Toast.makeText(LoginSign.this, "UID: " + uid,
                                        Toast.LENGTH_SHORT).show();
                            }
                            Intent i = new Intent(LoginSign.this, UserProfile.class);
                            startActivity(i);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginSign.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
}

