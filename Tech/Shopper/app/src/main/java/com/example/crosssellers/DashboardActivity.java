package com.example.crosssellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import Fragments.HomeFragment;
import Fragments.ProfileFragment;
import Fragments.UsersFragment;

public class DashboardActivity extends AppCompatActivity {

    //-- Setup database
    private FirebaseAuth mAuth;

    //-- Setup action bar
    ActionBar actionbar;
    BottomNavigationView navigationView;


    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    //-- Handle item clicks for bottomNavigator
                    switch(item.getItemId()){
                        case R.id.nav_home:
                            //Home fragment Transaction
                            actionbar.setTitle("Home");
                            HomeFragment fragment1 = new HomeFragment();
                            loadFragment(fragment1);
                            return true;
                        case R.id.nav_profile:
                            //Profile fragment translation
                            actionbar.setTitle("Profile");
                            ProfileFragment fragment2 = new ProfileFragment();
                            loadFragment(fragment2);
                            return true;
                        case R.id.nav_users:
                            //Users fragment translation
                            actionbar.setTitle("Users");
                            UsersFragment fragment3 = new UsersFragment();
                            loadFragment(fragment3);
                            return true;
                    }
                    return false;
                }
            };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        //----------------------------------------------------------------------//
        // Action bar                                                           //
        //----------------------------------------------------------------------//
        //-- Add built-in "Actionbar" and it's "Actionbar"->title
        actionbar = getSupportActionBar();
        actionbar.setTitle("Profile");


        //----------------------------------------------------------------------//
        // Cache Reference(s)                                                   //
        //----------------------------------------------------------------------//
        //-- Cache Database
        mAuth = FirebaseAuth.getInstance();


        //----------------------------------------------------------------------//
        // Setup BottomNavigation                                               //
        //----------------------------------------------------------------------//
        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);


        //----------------------------------------------------------------------//
        // Default Transaction: Home Fragment                                   //
        //----------------------------------------------------------------------//
        // Home fragment Transaction
        actionbar.setTitle("Home");
        HomeFragment fragment1 = new HomeFragment();
        loadFragment(fragment1);
    }


    private void checkUserStatus()
    {
        //-- Get Current User
        FirebaseUser user = mAuth.getCurrentUser();

        //-- User is signed in.
        if(user != null)
        {
        }

        //-- User not signed in
        else
        {
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
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
