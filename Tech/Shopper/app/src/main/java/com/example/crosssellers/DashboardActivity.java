package com.example.crosssellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import Fragments.ChatListFragment;
import Fragments.HomeFragment;
import Fragments.ProfileFragment;
import Fragments.UsersFragment;

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
                            ProfileFragment fragment2 = new ProfileFragment();
                            loadFragment(fragment2);
                            actionbar.show();
                            return true;
                        case R.id.nav_users:
                            //Users fragment translation
                            actionbar.setTitle("Users");
                            UsersFragment fragment3 = new UsersFragment();
                            loadFragment(fragment3);
                            actionbar.show();
                            return true;

                        case R.id.nav_chat:
                            //Chat fragment translation
                            actionbar.setTitle("Chats");
                            ChatListFragment fragment4 = new ChatListFragment();
                            loadFragment(fragment4);
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
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
            finish();
        }
    }



}
