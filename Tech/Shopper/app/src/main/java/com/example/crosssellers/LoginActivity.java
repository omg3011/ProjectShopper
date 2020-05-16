package com.example.crosssellers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Models.User_Model;

public class LoginActivity extends AppCompatActivity {

    //-- For google sign-in
    private static final int RC_SIGN_IN = 100;
    GoogleSignInClient mGoogleSignInClient;

    //-- Cache References
    EditText et_email, et_password;
    TextView tv_notHaveAccount, tv_recoverPassword, tv_mall;
    Button btn_login;
    ProgressDialog progressDialog;
    SignInButton googleLoginBtn;

    //-- Firebase
    private FirebaseAuth mAuth;

    //-- Save Data
    String mallName;


    //-------------------------------------------------------------------------------------------------------------------------------------------//
    //
    // Built-In Function(s)
    //
    //-------------------------------------------------------------------------------------------------------------------------------------------//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //-- Get Saved data
        LoadData(savedInstanceState);

        //----------------------------------------------------------------------//
        // Action bar                                                           //
        //----------------------------------------------------------------------//
        //-- Add built-in "Actionbar" and it's "Actionbar"->title
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Login");

        //-- Enable "Actionbar"->back button
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);

        //----------------------------------------------------------------------//
        // Cache Reference(s)                                                   //
        //----------------------------------------------------------------------//
        //-- Cache Reference
        et_email = findViewById(R.id.login_emailET);
        et_password = findViewById(R.id.login_passwordET);
        tv_notHaveAccount = findViewById(R.id.dont_have_account_tv);
        tv_recoverPassword = findViewById(R.id.recover_password_tv);
        btn_login = findViewById(R.id.login_loginbtn);
        googleLoginBtn = findViewById(R.id.googleLoginBtn);
        tv_mall = findViewById(R.id.login_mall_TV);



        tv_mall.setText(mallName);

        //-- Cache progressbar
        progressDialog = new ProgressDialog(this);

        //-- Before get firebase, we configure google sign-in,
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //-- Firebase database
        mAuth = FirebaseAuth.getInstance();


        //----------------------------------------------------------------------//
        // Register Event(s)                                                    //
        //----------------------------------------------------------------------//
        //-- Login button
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email.getText().toString();
                String password = et_password.getText().toString().trim();

                //-- Validate: Email failed
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    et_email.setError("Invalid email");
                    et_email.setFocusable(true);
                }
                // Validate: Passed
                else
                {
                    loginUser(email, password);
                }
            }
        });

        //-- Don't have account click
        tv_notHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveData_And_GoToNextActivity(RegisterActivity.class);
            }
        });

        //-- Recover password click
        tv_recoverPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPasswordDialog();
            }
        });

        //-- Google Sign-in Button
        googleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    //------------------------------------------------------------------------//
    // Function: To allow back button
    //------------------------------------------------------------------------//
    @Override
    public boolean onSupportNavigateUp() {
        //-- Go to previous activity
        onBackPressed(); // Built-in function

        return super.onSupportNavigateUp();
    }

    //------------------------------------------------------------------------//
    // Function: Handle google-login onSuccess, do what?
    //------------------------------------------------------------------------//
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------//
    //
    // User-Defined Function(s)
    //
    //-------------------------------------------------------------------------------------------------------------------------------------------//
    //------------------------------------------------------------------------//
    // Function: To recover password
    //------------------------------------------------------------------------//
    private void showRecoverPasswordDialog()
    {
        //-- AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");

        //-- Set layout linear layout
        LinearLayout linearLayout = new LinearLayout(this);

        //-- Get references from views, to set dialog
        final EditText emailET = new EditText(this);
        emailET.setHint("Email");
        emailET.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        //-- Set min width of EditView to fit a text of n 'M' letters regardless of the actual
        //   text extension and text size
        emailET.setMinEms(16);

        linearLayout.addView(emailET);
        linearLayout.setPadding(10, 10, 10, 10);

        builder.setView(linearLayout);

        //-- Create "recover" button
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = emailET.getText().toString().trim();
                beginRecovery(email);
            }
        });

        //-- Create "cancel" button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //-- Show dialog
        builder.create().show();
    }

    //------------------------------------------------------------------------//
    // Function: Recover password
    //------------------------------------------------------------------------//
    private void beginRecovery(String email)
    {
        //-- Display progress dialog
        progressDialog.setMessage("Sending email...");
        progressDialog.show();

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Email sent", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Failed to sent email", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //------------------------------------------------------------------------//
    // Function: Login user
    //------------------------------------------------------------------------//
    private void loginUser(String email, String password)
    {
        //-- Display progress dialog
        progressDialog.setMessage("Logging In...");
        progressDialog.show();

        final FirebaseUser user = mAuth.getCurrentUser();
        final FirebaseFirestore fireStore = FirebaseFirestore.getInstance();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        //-- Success: User Login
                        if (task.isSuccessful())
                        {
                            progressDialog.dismiss();

                            //-- Private Variable(s)
                            final boolean[] setupProfile = {false};
                            final String[] dbMallName = new String[1];

                            DocumentReference doc = fireStore.collection("Users").document(user.getUid());
                            doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    setupProfile[0] = documentSnapshot.getBoolean("setup_profile");

                                    dbMallName[0] = documentSnapshot.getString("mallName");


                                    if(!dbMallName[0].equals(tv_mall.getText()))
                                    {
                                        Toast.makeText(LoginActivity.this, "Wrong Mall " + dbMallName[0] + " != " + tv_mall.getText(), Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        if(setupProfile[0] == false)
                                        {
                                            startActivity(new Intent(LoginActivity.this, SetupProfileActivity.class));
                                            finish();
                                        }
                                        else
                                        {
                                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                                            finish();
                                        }
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("Test", "Failed to get " + e.getMessage());
                                }
                            });



                        }
                        //-- Failed: User Login
                        else
                        {
                            progressDialog.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                progressDialog.dismiss();


                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    //------------------------------------------------------------------------//
    // Function: When press Google Sign-in button, check the following;
    // > is new user -> register it in database
    // > success -> show Toast ("success") -> go to dashboardActivity
    // > fail    -> show Toast ("failed")  -> remain on current Activity
    //------------------------------------------------------------------------//
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //-------------------------------------------------------------------------------
                            // Handle Firebase
                            //-------------------------------------------------------------------------------
                            FirebaseUser user = mAuth.getCurrentUser();
                            FirebaseFirestore fireStore = FirebaseFirestore.getInstance();

                            //-- If logging in for the first time. we add new user to database
                            if(task.getResult().getAdditionalUserInfo().isNewUser())
                            {
                                //-- Update our database with new User
                                User_Model user_model = new User_Model(user.getEmail(), user.getUid(), "", "", "", "", mallName, "", "", null, false, 0, null);
                                fireStore.collection("Users").document(user.getUid()).set(user_model);

                                //-------------------------------------------------------------------------------
                                // Handle navigate to other activity
                                //-------------------------------------------------------------------------------
                                startActivity(new Intent(LoginActivity.this, SetupProfileActivity.class));
                            }
                            else
                            {
                                // Local Variable(s)
                                final boolean[] setupProfile = {false};
                                final String[] dbMallName = new String[1];

                                DocumentReference doc = fireStore.collection("Users").document(user.getUid());
                                doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        setupProfile[0] = documentSnapshot.getBoolean("setup_profile");
                                        dbMallName[0] = documentSnapshot.getString("mallName");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("Test", "Failed to get " + e.getMessage());
                                    }
                                });

                                if(TextUtils.isEmpty(dbMallName[0]))
                                {
                                    Toast.makeText(LoginActivity.this, "Wrong Mall", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    if(setupProfile[0] == false)
                                    {
                                        startActivity(new Intent(LoginActivity.this, SetupProfileActivity.class));
                                    }
                                    else
                                    {
                                        startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                                    }
                                }
                            }

                            //-------------------------------------------------------------------------------
                            // Handle display message at bottom of the screen
                            //-------------------------------------------------------------------------------
                            Toast.makeText(LoginActivity.this, "Welcome: "+user.getEmail(), Toast.LENGTH_SHORT).show();

                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Login failed...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(LoginActivity.this, activity);
        intent.putExtra("mall", mallName);
        startActivity(intent);
    }
}
