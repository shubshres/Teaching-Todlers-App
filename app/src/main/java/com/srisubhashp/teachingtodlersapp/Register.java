package com.srisubhashp.teachingtodlersapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText uFirstName, uLastName, uEmail, uPassword;
    Button uRegisterBtn;

    TextView uLoginView;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ProgressBar progressBar;
    String userID;//for Firebase User collection



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        uFirstName = findViewById(R.id.firstNameText);
        uLastName = findViewById(R.id.lastNameText);
        uEmail = findViewById(R.id.emailText);
        uPassword = findViewById(R.id.passwordText);
        uRegisterBtn = findViewById(R.id.registerBtn);
        uLoginView = findViewById(R.id.loginView);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);


        // Ahmed ------------------------------------------------------------------
        if(fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        uRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int notValid = 0;
                String firstName = uFirstName.getText().toString().trim();
                String lastName = uLastName.getText().toString().trim();
                String email = uEmail.getText().toString().trim();
                String password = uPassword.getText().toString().trim();
                String level1Score="";
                String level2Score="";
                String level3Score="";
                String phoneNumber="";

                if(TextUtils.isEmpty(firstName)) {
                    uFirstName.setError("Must enter a first name.");
                    notValid++;
                }
                if(TextUtils.isEmpty(lastName)) {
                    uLastName.setError("Must enter a last name.");
                    notValid++;
                }
                if(TextUtils.isEmpty(email)) {
                    uEmail.setError("Must enter an email.");
                    notValid++;
                }

                if(password.length() > 16 || password.length() < 8) {
                    uPassword.setError("Password must be between 8 and 16 characters.");
                    notValid++;
                }

                if(notValid > 0) {
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(Register.this, "Registration Complete", Toast.LENGTH_SHORT).show();

                            userID=fAuth.getCurrentUser().getUid();//grab the Fauth id of the current User.

                            //this is used to refer to the indiviual document of the User collection in Firestore.
                            //even if we did not create a collection before, it will do it for us
                            DocumentReference documentReference=fStore.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("firstName",firstName);
                            user.put("email",email);
                            user.put("lastName",lastName);
                            user.put("phoneNumber",phoneNumber);
                            user.put("level1Score",level1Score);
                            user.put("level2Score",level2Score);
                            user.put("level3Score",level3Score);

                            //we are going to create the User data using Hashmap and store it in Firebase.

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG,"onSuccess: USer Profile is created for "+userID);
                                    //Ctrl+Alt+C -> would create the TAG constant for us
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(Register.this, "Registration Failed! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }


                    }
                });
            }
        });

        uLoginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Register.this, "Changing to login page complete", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }


}