package com.example.crosssellers;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import Adapters.AdapterPromoPost_Promo;
import Models.CPromotion_Model;

public class CPromotionHomeActivity  extends AppCompatActivity {

    //-- Private Variables
    List<String> selectedItems;

    //-- DB(s)
    FirebaseUser fUser;
    CollectionReference dataReference_CPromotion;
    CollectionReference dataReference_User;

    //-- View(s)
    Button BTN_create_promotion, BTN_filter;
    RecyclerView RV_promoPost;

    //-- Private variable(s)
    AdapterPromoPost_Promo adapterPromoPostPromo;
    List<CPromotion_Model> promoPostList;
    LinearLayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpromotion_home);

        //-- Cache Reference
        BTN_create_promotion = findViewById(R.id.cpromo_home_create_promo_btn);
        BTN_filter = findViewById(R.id.cpromo_home_filter_btn);
        RV_promoPost = findViewById(R.id.cpromo_home_RV);

        //-- Init DB
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        dataReference_CPromotion = FirebaseFirestore.getInstance().collection("Promotions");
        dataReference_User = FirebaseFirestore.getInstance().collection("Users");


        //----------------------------------------------------------------------//
        // Init RecyclerView List                                               //
        //----------------------------------------------------------------------//
        // Adapter
        promoPostList = new ArrayList<>();
        adapterPromoPostPromo = new AdapterPromoPost_Promo(this, promoPostList, dataReference_User);
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        RV_promoPost.setLayoutManager(manager);
        RV_promoPost.setAdapter(adapterPromoPostPromo);

        //-- Retrieve data from database
        getPromoPostRelatedByTag();

        //----------------------------------------------------------------------//
        // Register Event Listener                                              //
        //----------------------------------------------------------------------//
        BTN_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAlertDialog_Tag();
            }
        });
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


    void CreateAlertDialog_Tag()
    {
        selectedItems = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(CPromotionHomeActivity.this);
        builder.setTitle("Select Tags");

        builder.setMultiChoiceItems(R.array.store_tags2, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                String[] items = getResources().getStringArray(R.array.store_tags2);
                if(isChecked)
                    selectedItems.add(items[which]);
                else if(selectedItems.contains(items[which]))
                    selectedItems.remove(items[which]);
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapterPromoPostPromo.filter(selectedItems);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();
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
                            //    continue;

                            if(model.isCollab_closed_flag())
                                continue;

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

                                        adapterPromoPostPromo.notifyDataSetChanged();

                                    }
                                    break;
                                case MODIFIED:
                                    break;
                                case REMOVED:
                                    // To be done later
                                    promoPostList.remove(model);
                                    adapterPromoPostPromo.notifyDataSetChanged();
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
