package com.example.sharedlistapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailET, passwordET, confirmPasswordET;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    public RegisterActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        confirmPasswordET = findViewById(R.id.confirmPasswordET);
        registerButton = findViewById(R.id.registerButton);
        mAuth = FirebaseAuth.getInstance();


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("register", emailET.getText().toString() + passwordET.getText().toString() + confirmPasswordET.getText().toString());
                String emailTxt = emailET.getText().toString();
                String passwordTxt = passwordET.getText().toString();
                String confirmPasswordTxt = confirmPasswordET.getText().toString();

                if (emailTxt.isEmpty() || passwordTxt.isEmpty() || confirmPasswordTxt.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Empty fields", Toast.LENGTH_SHORT).show();
                } else {
                    if (confirmPasswordTxt.equals(passwordTxt) && passwordTxt.length() >= 6) {
                        addNewUser(emailTxt, passwordTxt, confirmPasswordTxt);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    private void addNewUser(String email, String password, String confirmPassword) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    String userID = firebaseUser.getUid();
                    myRef = FirebaseDatabase.getInstance().getReference("Users").child(userID);

                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("username", email);

                    myRef.setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();
                            }
                        }
                    });

                } else {
                    Log.i("register", "Something went wrong");
                }

            }
        });
    }
}