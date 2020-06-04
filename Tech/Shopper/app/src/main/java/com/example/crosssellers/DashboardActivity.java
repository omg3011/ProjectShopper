package com.example.crosssellers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import Fragments.HomeFragment;
import Fragments.NotificationFragment;
import Fragments.ProfileFragment;
import Fragments.ChatFragment;
import Models.Chat_Model;
import Models.Notification_Model;
import Models.RequestMailBox_Model;

public class DashboardActivity extends AppCompatActivity {

    //-- Setup database
    private FirebaseAuth mAuth;
    private FirebaseUser fUser;

    //-- Setup action bar
    ActionBar actionbar;
    BottomNavigationView navigationView;

    //-- DB
    CollectionReference dataReference_RequestMailBox;
    CollectionReference dataReference_Notification;
    CollectionReference dataReference_Chat;

    //-- Progress Dialog
    ProgressDialog pd;

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
        fUser = mAuth.getCurrentUser();
        dataReference_RequestMailBox = FirebaseFirestore.getInstance().collection("RequestMailBox");
        dataReference_Notification = FirebaseFirestore.getInstance().collection("Notifications");
        dataReference_Chat = FirebaseFirestore.getInstance().collection("Chats");


        //----------------------------------------------------------------------//
        // Setup BottomNavigation                                               //
        //----------------------------------------------------------------------//
        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);


        //----------------------------------------------------------------------//
        // Setup Notification Counter                                           //
        //----------------------------------------------------------------------//
        navigationView.getOrCreateBadge(R.id.nav_profile).setNumber(0);
        dataReference_RequestMailBox.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for (DocumentSnapshot documentSnapshot : documentSnapshots)
                {
                    if (documentSnapshot.exists())
                    {
                        RequestMailBox_Model rModel = documentSnapshot.toObject(RequestMailBox_Model.class);

                        if(rModel.getStatus().equals("pending"))
                        {
                            if(rModel.getOwner_UID().equals(fUser.getUid()))
                            {
                                navigationView.getOrCreateBadge(R.id.nav_profile).setNumber(navigationView.getOrCreateBadge(R.id.nav_profile).getNumber()+1);
                            }
                            else
                            {

                            }

                        }
                    }
                }

                if(navigationView.getOrCreateBadge(R.id.nav_profile).getNumber() == 0)
                    navigationView.getOrCreateBadge(R.id.nav_profile).setVisible(false);
            }
        });


        navigationView.getOrCreateBadge(R.id.nav_notification).setNumber(0);
        dataReference_Notification.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                // If got error, end
                if(e != null)
                    return;

                // Check until required info is received
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges())
                {
                    Notification_Model model = doc.getDocument().toObject(Notification_Model.class);

                    // Show related to me only
                   if(!model.getUid().equals(fUser.getUid()))
                        continue;

                    //-- Is message added / modified / removed?
                    switch(doc.getType())
                    {
                        case ADDED:
                            navigationView.getOrCreateBadge(R.id.nav_notification).setNumber(navigationView.getOrCreateBadge(R.id.nav_notification).getNumber() + 1);
                            break;
                        case MODIFIED:
                            break;
                        case REMOVED:
                            navigationView.getOrCreateBadge(R.id.nav_notification).setNumber(navigationView.getOrCreateBadge(R.id.nav_notification).getNumber() - 1);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + doc.getType());
                    }
                }

                if(navigationView.getOrCreateBadge(R.id.nav_notification).getNumber() == 0)
                    navigationView.getOrCreateBadge(R.id.nav_notification).setVisible(false);
                else
                    navigationView.getOrCreateBadge(R.id.nav_notification).setVisible(true);
            }
        });

        navigationView.getOrCreateBadge(R.id.nav_chat).setNumber(0);
        dataReference_Chat.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                // If got error, end
                if(e != null)
                    return;

                // Check until required info is received
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges())
                {
                    Chat_Model cmodel = doc.getDocument().toObject(Chat_Model.class);

                    // Show related to me only
                    if(!cmodel.getReceiver().equals(fUser.getUid()))
                        continue;

                    //-- Is message added / modified / removed?
                    switch(doc.getType())
                    {
                        case ADDED:
                            if(cmodel.isSeen() == false)
                                navigationView.getOrCreateBadge(R.id.nav_chat).setNumber(navigationView.getOrCreateBadge(R.id.nav_chat).getNumber() + 1);
                            break;
                        case MODIFIED:
                            break;
                        case REMOVED:
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + doc.getType());
                    }
                }

                if(navigationView.getOrCreateBadge(R.id.nav_chat).getNumber() == 0)
                    navigationView.getOrCreateBadge(R.id.nav_chat).setVisible(false);
                else
                    navigationView.getOrCreateBadge(R.id.nav_chat).setVisible(true);
            }
        });

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

                        case R.id.nav_chat:
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
