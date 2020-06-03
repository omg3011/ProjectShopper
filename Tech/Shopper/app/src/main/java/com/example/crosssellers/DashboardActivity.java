package com.example.crosssellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import Fragments.HomeFragment;
import Fragments.NotificationFragment;
import Fragments.ProfileFragment;
import Fragments.ChatFragment;

public class DashboardActivity extends AppCompatActivity {

    //-- Setup database
    private FirebaseAuth mAuth;

    //-- Setup action bar
    ActionBar actionbar;
    BottomNavigationView navigationView;


    //-------------------------------------------------------------------------------------------------------------------------------------------//
    //
    // Built-In Function(s)
    //
    //-------------------------------------------------------------------------------------------------------------------------------------------//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        //----------------------------------------------------------------------//
        // Action bar                                                           //
        //----------------------------------------------------------------------//
        //-- Add built-in "Actionbar" and it's "Actionbar"->title
        actionbar = getSupportActionBar();


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


        navigationView.getOrCreateBadge(R.id.nav_notification).setNumber(2);
        navigationView.getOrCreateBadge(R.id.nav_profile).setNumber(4);

        //----------------------------------------------------------------------//
        // Default Transaction: Home Fragment                                   //
        //----------------------------------------------------------------------//
        // Home fragment Transaction
        actionbar.setTitle("Home");
        HomeFragment fragment1 = new HomeFragment();
        loadFragment(fragment1);
    }


    @Override
    protected void onStart()
    {
        checkUserStatus();
        super.onStart();
    }


    //------------------------------------------------------------------------//
    // Usage: To allow back button
    //------------------------------------------------------------------------//
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }



    //-------------------------------------------------------------------------------------------------------------------------------------------//
    //
    // User-Defined Function(s)
    //
    //-------------------------------------------------------------------------------------------------------------------------------------------//
    //------------------------------------------------------------------------------------//
    // EventListener: On click navList button, run that Fragment to display different XML
    //------------------------------------------------------------------------------------//
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
                            actionbar.show();
                            return true;
                        case R.id.nav_profile:
                            //Profile fragment translation
                            actionbar.setTitle("Profile");
                            //ProfileFragment fragment2 = new ProfileFragment();
                            ProfileFragment fragment2 = new ProfileFragment();
                            loadFragment(fragment2);
                            actionbar.show();
                            return true;

                        case R.id.nav_notification:
                            //Users fragment translation
                            actionbar.setTitle("Notifications");
                            NotificationFragment fragment6 = new NotificationFragment();
                            loadFragment(fragment6);
                            actionbar.show();
                            return true;

                        case R.id.nav_partner:
                            //Users fragment translation
                            actionbar.setTitle("Partners");
                            ChatFragment fragment3 = new ChatFragment();
                            loadFragment(fragment3);
                            actionbar.show();
                            return true;
                    }
                    return false;
                }
            };

    //------------------------------------------------------------------------------------//
    // Helper Function: Load Fragment
    //------------------------------------------------------------------------------------//
    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            transaction.setReorderingAllowed(false);
        }

        transaction.replace(R.id.content, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }



    //-------------------------------------------------------------------------------------------------//
    // Check if i'm logged in, this is just safety-precaution
    //-------------------------------------------------------------------------------------------------//
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
            startActivity(new Intent(DashboardActivity.this, SelectMallActivity.class));
            finish();
        }
    }



}
