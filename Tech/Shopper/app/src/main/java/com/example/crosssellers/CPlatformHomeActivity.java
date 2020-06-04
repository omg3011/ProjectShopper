package com.example.crosssellers;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Adapters.AdapterCollabPost_CPlatform;
import Models.CPlatform_Model;
import Models.User_Model;

public class CPlatformHomeActivity extends AppCompatActivity {

    //-- DB(s)
    FirebaseUser fUser;
    CollectionReference dataReference_CPlatform;
    CollectionReference dataReference_User;

    //-- View(s)
    Button BTN_create_collab;
    RecyclerView RV_collabPost;

    //-- Private variable(s)
    AdapterCollabPost_CPlatform adapterCollabPostCPlatform;
    List<CPlatform_Model> collabPostList;
    LinearLayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cplatform_home);

        //-- DB
        dataReference_CPlatform = FirebaseFirestore.getInstance().collection("CPlatform");
        dataReference_User = FirebaseFirestore.getInstance().collection("Users");

        //-- Cache Reference
        BTN_create_collab = findViewById(R.id.cplatform_create_collab_btn);
        RV_collabPost = findViewById(R.id.cplatform_home_RV);

        //-- Init DB
        fUser = FirebaseAuth.getInstance().getCurrentUser();


        //----------------------------------------------------------------------//
        // Init RecyclerView List                                               //
        //----------------------------------------------------------------------//
        // Adapter
        collabPostList = new ArrayList<>();
        adapterCollabPostCPlatform = new AdapterCollabPost_CPlatform(this, collabPostList, dataReference_User);
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        RV_collabPost.setLayoutManager(manager);
        RV_collabPost.setAdapter(adapterCollabPostCPlatform);

        //-- Retrieve data from database
        getCollabPostRelatedByTag();

        //----------------------------------------------------------------------//
        // Register Event Listener                                              //
        //----------------------------------------------------------------------//
        BTN_create_collab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CPlatformHomeActivity.this, CPlatformCreateActivity.class));
                finish();
            }
        });

        //----------------------------------------------------------------------//
        // Action bar                                                           //
        //----------------------------------------------------------------------//
        //-- Add built-in "Actionbar" and it's "Actionbar"->title
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Collaborations Platform");

        //-- Enable "Actionbar"->back button
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);
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


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CPlatformHomeActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }


    void getCollabPostRelatedByTag()
    {
        // Get Path of database named "Users" containing user info
        DocumentReference doc = dataReference_User.document(fUser.getUid());
        final String[] userTag = new String[1];


        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                final User_Model uModel = documentSnapshot.toObject(User_Model.class);
                //--------------------------------------------------------------------------------//
                // (1) Get this user.storeTag
                //--------------------------------------------------------------------------------//
                userTag[0] = documentSnapshot.getString("storeTag");


                //--------------------------------------------------------------------------------//
                // (2) Get all the CPlatform Post
                //--------------------------------------------------------------------------------//
                // Get all data from path ^
                dataReference_CPlatform.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        // If got error, end
                        if(e != null)
                            return;

                        // Check until required info is received
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges())
                        {
                            CPlatform_Model model = doc.getDocument().toObject(CPlatform_Model.class);

                            if(!uModel.getMallName().equals(model.getShoppingMall()))
                                continue;
                            // Dont show my post
                            //if(model.getPosterUid().equals(fUser.getUid()))
                            //    continue;

                            if(!model.isCollab_closed_flag())
                            {

                                //------------------------------------------------------------------------------//
                                // (3) If the post's tag, contains this user tag, then show it
                                //------------------------------------------------------------------------------//
                                //-- Is message added / modified / removed?
                                switch(doc.getType())
                                {
                                    case ADDED:
                                        if(model.getCollabTag().contains(userTag[0]))
                                        {
                                            //---------------------------------------------------------//
                                            // Update UI to display the changes in the list
                                            //---------------------------------------------------------//
                                            collabPostList.add(model);

                                            collabPostList.sort(new Comparator<CPlatform_Model>()
                                            {
                                                @Override
                                                public int compare(CPlatform_Model o1, CPlatform_Model o2) {
                                                    //-- Get Timestamp
                                                    Date date1 = null;
                                                    Date date2 = null;
                                                    try {
                                                        date1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).parse(o1.getTimestamp());
                                                    } catch (ParseException ex) {
                                                        ex.printStackTrace();
                                                    }
                                                    try {
                                                        date2 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).parse(o2.getTimestamp());
                                                    } catch (ParseException ex) {
                                                        ex.printStackTrace();
                                                    }

                                                    return date2.compareTo(date1);
                                                }
                                            });

                                            adapterCollabPostCPlatform.notifyDataSetChanged();

                                        }
                                        break;
                                    case MODIFIED:
                                        break;
                                    case REMOVED:
                                        // To be done later
                                        collabPostList.remove(model);
                                        adapterCollabPostCPlatform.notifyDataSetChanged();
                                        break;
                                    default:
                                        throw new IllegalStateException("Unexpected value: " + doc.getType());
                                }
                            }


                        }
                    }
                });
            }
        });

    }
}
