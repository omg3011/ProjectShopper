package com.example.crosssellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Models.User_Model;

public class RegisterActivity extends AppCompatActivity {

    //-- View: Cache references
    EditText et_email, et_password;
    Button btn_register;
    TextView tv_haveAccount, tv_mall;

    //-- Progress bar: to display when user registering
    ProgressDialog progressDialog;

    //-- Setup database
    private FirebaseAuth mAuth;

    //-- Save data
    private String mallName;

    //-------------------------------------------------------------------------------------------------------------------------------------------//
    //
    // Built-In Function(s)
    //
    //-------------------------------------------------------------------------------------------------------------------------------------------//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //----------------------------------------------------------------------//
        // Action bar                                                           //
        //----------------------------------------------------------------------//
        //-- Add built-in "Actionbar" and it's "Actionbar"->title
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Create Account");

        //-- Enable "Actionbar"->back button
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);

        //----------------------------------------------------------------------//
        // Cache Reference(s)                                                   //
        //----------------------------------------------------------------------//
        //-- Cache references
        et_email = findViewById(R.id.emailET);
        et_password = findViewById(R.id.passwordET);
        btn_register = findViewById(R.id.register_registerbtn);
        tv_haveAccount = findViewById(R.id.have_account_tv);
        tv_mall = findViewById(R.id.register_mall_TV);


        // Load data
        LoadData(savedInstanceState);

        tv_mall.setText(mallName);

        //-- Cache progressbar
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");

        //-- Cache Database
        mAuth = FirebaseAuth.getInstance();


        //----------------------------------------------------------------------//
        // Register Event(s)                                                    //
        //----------------------------------------------------------------------//
        //-- Register button onClick Event
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email.getText().toString().trim();
                String password = et_password.getText().toString().trim();

                // Validate: Fail email
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    et_email.setError("Invalid Email");
                    et_email.setFocusable(true);
                }

                // Validate: Fail password
                else if(password.length() < 6)
                {
                    et_password.setError("Password must be at least 6 characters");
                    et_password.setFocusable(true);
                }

                // Register
                else
                {
                    registerUser(email, password);
                }
            }
        });

        //-- Login text onClick Event
        tv_haveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveData_And_GoToNextActivity(LoginActivity.class);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        //-- Go to previous activity
        onBackPressed();

        return super.onSupportNavigateUp();
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------//
    //
    // User-Defined Function(s)
    //
    //-------------------------------------------------------------------------------------------------------------------------------------------//
    private void registerUser(String email, String password)
    {
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        //-- Success: User Login
                        if (task.isSuccessful())
                        {
                            //-------------------------------------------------------------------------------
                            // Handle Progress bar
                            //-------------------------------------------------------------------------------
                            //-- Remove progress dialog box
                            progressDialog.dismiss();

                            //-------------------------------------------------------------------------------
                            // Handle Firebase
                            //-------------------------------------------------------------------------------
                            FirebaseUser user = mAuth.getCurrentUser();
                            FirebaseFirestore fireStore = FirebaseFirestore.getInstance();

                            //-- Update our database with new User

//User_Model(String email, String uid, String name, String phone, String image, String coverImage, String onlineStatus, String mallName, String storeName, String storeUnit, List<String> storeTags)

                            User_Model user_model = new User_Model(user.getEmail(), user.getUid(), "", "", "", "", mallName, "", "", "", false, 0, null);
                            fireStore.collection("Users").document(user.getUid()).set(user_model);

                            //-------------------------------------------------------------------------------
                            // Handle display message at bottom of the screen
                            //-------------------------------------------------------------------------------
                            //-- Display "Registered"
                            Toast.makeText(RegisterActivity.this, "Registered... \n" + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();

                            //-------------------------------------------------------------------------------
                            // Handle navigate to other activity
                            //-------------------------------------------------------------------------------
                            //-- Go to HomeActivity
                            startActivity(new Intent(RegisterActivity.this, SetupProfileActivity.class));
                            finish();
                        }
                        //-- Failed: User Login
                        else
                        {
                            progressDialog.dismiss();

                            try
                            {
                                throw task.getException();
                            }
                            // if user enters wrong email.
                            catch (FirebaseAuthWeakPasswordException weakPassword)
                            {
                                Toast.makeText(RegisterActivity.this, "Failed: weak_password...",
                                        Toast.LENGTH_SHORT).show();

                            }
                            // if user enters wrong password.
                            catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                            {
                                Toast.makeText(RegisterActivity.this, "Failed: malformed_email...",
                                        Toast.LENGTH_SHORT).show();

                            }
                            catch (FirebaseAuthUserCollisionException existEmail)
                            {
                                Toast.makeText(RegisterActivity.this, "Failed: Email already exist.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e)
                            {
                                Toast.makeText(RegisterActivity.this, e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                progressDialog.dismiss();
            }
        });
    }


    void LoadData(final Bundle savedInstanceState){
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                mallName= null;
            } else {
                mallName= extras.getString("mall");
            }
        } else {
            mallName= (String) savedInstanceState.getSerializable("mall");
        }
    }


    void SaveData_And_GoToNextActivity(Class activity)
    {
        Intent intent = new Intent(RegisterActivity.this, activity);
        intent.putExtra("mall", mallName);
        startActivity(intent);
    }
}
