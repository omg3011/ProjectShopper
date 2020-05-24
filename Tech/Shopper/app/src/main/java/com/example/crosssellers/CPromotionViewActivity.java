package com.example.crosssellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Adapters.AdapterStoreImages;
import Models.CPlatform_Model;
import Models.CPromotion_Model;
import Models.RequestMailBox_Model;

public class CPromotionViewActivity extends AppCompatActivity {

    // Get saved data
    CPromotion_Model postData;
    List<String> uploadsImageList;
    AdapterStoreImages adapterStoreImages;
    LinearLayoutManager manager;

    // Views
    TextView TV_postDescription, TV_postTag, TV_promoDate;
    TextView TV_storeName, TV_mallName, TV_storeUnit, TV_storeTag, TV_storeRatingQuantity, TV_storeRatingValue;
    RatingBar RB_storeRating;
    ImageView IV_storeProfile;
    RecyclerView RV_storeUploads;

    //-- DB(s)
    FirebaseUser fUser;
    FirebaseFirestore fireStore;
    CollectionReference dataReference_User;

    //-- Progress Dialog
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpromotion_view);

        //---------------------------------------------------------------------------//
        //  Init Views
        //---------------------------------------------------------------------------//
        //-- Post
        TV_postDescription = findViewById(R.id.cpromo_view_postDescription_TV);
        TV_postTag = findViewById(R.id.cpromo_view_postTags_TV);
        TV_promoDate = findViewById(R.id.cpromo_view_promoDate_TV);

        //-- Store
        TV_storeName = findViewById(R.id.cpromo_view_storeName_TV);
        TV_mallName = findViewById(R.id.cpromo_view_mallName_TV);
        TV_storeUnit = findViewById(R.id.cpromo_view_storeUnit_TV);
        TV_storeTag = findViewById(R.id.cpromo_view_storeTag_TV);
        TV_storeRatingQuantity = findViewById(R.id.cpromo_view_ratingQuantity_TV);
        TV_storeRatingValue = findViewById(R.id.cpromo_view_storeRating_TV);
        RB_storeRating = findViewById(R.id.cpromo_view_storeRating_RB);
        IV_storeProfile = findViewById(R.id.cpromo_view_storeProfile_IV);
        RV_storeUploads = findViewById(R.id.cpromo_view_uploads_rv);

        //---------------------------------------------------------------------------//
        //  Load Data (Carried from previous activity)
        //---------------------------------------------------------------------------//
        LoadData(savedInstanceState);

        //---------------------------------------------------------------------------//
        //  Progress Dialog
        //---------------------------------------------------------------------------//
        pd = new ProgressDialog(this);
        pd.setMessage("Loading post...");
        pd.show();


        //---------------------------------------------------------------------------//
        // Init RecyclerView List
        //---------------------------------------------------------------------------//
        // Adapter
        uploadsImageList = new ArrayList<>();
        uploadsImageList = postData.getUploads();
        adapterStoreImages = new AdapterStoreImages(this, uploadsImageList);
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        RV_storeUploads.setLayoutManager(manager);
        RV_storeUploads.setAdapter(adapterStoreImages);

        //----------------------------------------------------------------------//
        // Action bar                                                           //
        //----------------------------------------------------------------------//
        //-- Add built-in "Actionbar" and it's "Actionbar"->title
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("View Promotion");

        //-- Enable "Actionbar"->back button
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);

        //---------------------------------------------------------------------------//
        //  Database
        //---------------------------------------------------------------------------//
        //-- Init DB
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        fireStore = FirebaseFirestore.getInstance();
        dataReference_User = fireStore.collection("Users");

        //-- Update UI for "post" data
        //Description
        TV_postDescription.setText(postData.getDescription());
        //Tag
        String tags =  TextUtils.join(",", postData.getTags());
        TV_postTag.setText(tags);
        //Date
        TV_promoDate.setText(postData.getTimestampStart() + " - " + postData.getTimestampEnd());

        //-- Update UI for "store" data
        //-- Retrieve data from database
        DocumentReference doc = dataReference_User.document(fUser.getUid());
        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                TV_storeName.setText(documentSnapshot.getString("storeName"));
                TV_mallName.setText(documentSnapshot.getString("mallName"));
                TV_storeUnit.setText(documentSnapshot.getString("storeUnit"));
                TV_storeTag.setText(documentSnapshot.getString("storeTag"));

                String imageUri = documentSnapshot.getString("image");
                if(imageUri.isEmpty())
                    imageUri = "Empty";

                Picasso.get()
                        .load(imageUri)
                        .placeholder(R.drawable.ic_add_image)
                        .error(R.drawable.ic_error)
                        .into(IV_storeProfile);


                //-- ToDO
                RB_storeRating.setMax(5);
                RB_storeRating.setRating(4);
                TV_storeRatingQuantity.setText("(4)");
                TV_storeRatingValue.setText("4.0");
                pd.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Test", "Failed to get " + e.getMessage());
                pd.dismiss();
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


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CPromotionViewActivity.this, CPromotionHomeActivity.class);
        startActivity(intent);
        finish();
    }

    void LoadData(final Bundle savedInstanceState){
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                postData= null;
            } else {
                postData= (CPromotion_Model) extras.getSerializable("post");
            }
        } else {
            postData= (CPromotion_Model) savedInstanceState.getSerializable("post");
        }
    }
}
