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

import Adapters.AdapterPromoPost;
import Models.CPlatform_Model;
import Models.CPromotion_Model;

public class CPromotionHomeActivity  extends AppCompatActivity {

    //-- DB(s)
    FirebaseUser fUser;
    CollectionReference dataReference_CPromotion;
    CollectionReference dataReference_User;

    //-- View(s)
    Button BTN_create_promotion;
    RecyclerView RV_promoPost;

    //-- Private variable(s)
    AdapterPromoPost adapterPromoPost;
    List<CPromotion_Model> promoPostList;
    LinearLayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpromotion_home);

        //-- Cache Reference
        BTN_create_promotion = findViewById(R.id.cpromo_create_promo_btn);
        RV_promoPost = findViewById(R.id.cpromo_home_RV);

        //-- Init DB
        fUser = FirebaseAuth.getInstance().getCurrentUser();


        //----------------------------------------------------------------------//
        // Init RecyclerView List                                               //
        //----------------------------------------------------------------------//
        // Adapter
        promoPostList = new ArrayList<>();
        adapterPromoPost = new AdapterPromoPost(this, promoPostList);
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        RV_promoPost.setLayoutManager(manager);
        RV_promoPost.setAdapter(adapterPromoPost);

        //-- Retrieve data from database
        getPromoPostRelatedByTag();

        //----------------------------------------------------------------------//
        // Register Event Listener                                              //
        //----------------------------------------------------------------------//
        BTN_create_promotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CPromotionHomeActivity.this, CPromotionCreateActivity.class));
                finish();
            }
        });

        //----------------------------------------------------------------------//
        // Action bar                                                           //
        //----------------------------------------------------------------------//
        //-- Add built-in "Actionbar" and it's "Actionbar"->title
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Promotion Platform");

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
        Intent intent = new Intent(CPromotionHomeActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }


    void getPromoPostRelatedByTag()
    {
        // Get Path of database named "Users" containing user info
        dataReference_CPromotion = FirebaseFirestore.getInstance().collection("Promotions");
        dataReference_User = FirebaseFirestore.getInstance().collection("Users");
        DocumentReference doc = dataReference_User.document(fUser.getUid());
        final String[] userTag = new String[1];


        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //--------------------------------------------------------------------------------//
                // (1) Get this user.storeTag
                //--------------------------------------------------------------------------------//
                userTag[0] = documentSnapshot.getString("storeTag");


                //--------------------------------------------------------------------------------//
                // (2) Get all the CPlatform Post
                //--------------------------------------------------------------------------------//
                // Get all data from path ^
                dataReference_CPromotion.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        // If got error, end
                        if(e != null)
                            return;

                        // Check until required info is received
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges())
                        {
                            CPromotion_Model model = doc.getDocument().toObject(CPromotion_Model.class);

                            // Dont show my post
                            //if(model.getPosterUid().equals(fUser.getUid()))
                            //{
                            //    continue;
                            //}


                            //------------------------------------------------------------------------------//
                            // (3) If the post's tag, contains this user tag, then show it
                            //------------------------------------------------------------------------------//
                            //-- Is message added / modified / removed?
                            switch(doc.getType())
                            {
                                case ADDED:
                                    if(model.getTags().contains(userTag[0]))
                                    {
                                        //---------------------------------------------------------//
                                        // Update UI to display the changes in the list
                                        //---------------------------------------------------------//
                                        promoPostList.add(model);

                                        promoPostList.sort(new Comparator<CPromotion_Model>()
                                        {
                                            @Override
                                            public int compare(CPromotion_Model o1, CPromotion_Model o2) {
                                                //-- Get Timestamp
                                                Date date1 = null;
                                                Date date2 = null;
                                                try {
                                                    date1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).parse(o1.getTimestampPost());
                                                } catch (ParseException ex) {
                                                    ex.printStackTrace();
                                                }
                                                try {
                                                    date2 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).parse(o2.getTimestampPost());
                                                } catch (ParseException ex) {
                                                    ex.printStackTrace();
                                                }

                                                return date2.compareTo(date1);
                                            }
                                        });

                                        adapterPromoPost.notifyDataSetChanged();

                                    }
                                    break;
                                case MODIFIED:
                                    break;
                                case REMOVED:
                                    // To be done later
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + doc.getType());
                            }
                        }
                    }
                });
            }
        });

    }
}