package com.example.crosssellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    //-- Setup database
    private FirebaseAuth mAuth;

    private TextView tv_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        //----------------------------------------------------------------------//
        // Action bar                                                           //
        //----------------------------------------------------------------------//
        //-- Add built-in "Actionbar" and it's "Actionbar"->title
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Profile");


        //----------------------------------------------------------------------//
        // Cache Reference(s)                                                   //
        //----------------------------------------------------------------------//
        //-- Cache Database
        mAuth = FirebaseAuth.getInstance();
        tv_email = findViewById(R.id.emailTV);

    }

    private void checkUserStatus()
    {
        //-- Get Current User
        FirebaseUser user = mAuth.getCurrentUser();

        //-- User is signed in.
        if(user != null)
        {
            tv_email.setText(user.getEmail());
        }

        //-- User not signed in
        else
        {
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            finish();
        }
    }

    protected void onStart()
    {
        checkUserStatus();
        super.onStart();
    }

    //--------------------------------------------------------//
    // Inflate option menu
    //--------------------------------------------------------//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //--------------------------------------------------------//
    // Handle menu item click event
    //--------------------------------------------------------//
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_logout)
        {
            //---------------------------------------------------------------------------------//
            // Sign out of google account
            //---------------------------------------------------------------------------------//
            //-- Check is sign-in using google
            GoogleSignInOptions gso = new GoogleSignInOptions.
                    Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                    build();
            GoogleSignInClient googleSignInClient=GoogleSignIn.getClient(this,gso);

            if(googleSignInClient != null)
            {
                googleSignInClient.signOut();
            }

            //---------------------------------------------------------------------------------//
            // Sign out of firebase database
            //---------------------------------------------------------------------------------//
            mAuth.signOut();

            //---------------------------------------------------------------------------------//
            // Go to home
            //---------------------------------------------------------------------------------//
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }


    //------------------------------------------------------------------------//
    // Built-in function: To allow back button
    //------------------------------------------------------------------------//
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
