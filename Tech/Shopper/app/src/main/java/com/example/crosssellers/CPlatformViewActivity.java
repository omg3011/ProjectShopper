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

public class CPlatformViewActivity extends AppCompatActivity {

    // Get saved data
    CPlatform_Model postData;
    List<String> uploadsImageList;
    AdapterStoreImages adapterStoreImages;
    LinearLayoutManager manager;

    // Views
    TextView TV_postDescription, TV_postTag, TV_postTime, TV_postDate;
    TextView TV_storeName, TV_mallName, TV_storeUnit, TV_storeTag, TV_storeRatingQuantity, TV_storeRatingValue;
    RatingBar RB_storeRating;
    ImageView IV_storeProfile;
    RecyclerView RV_storeUploads;
    Button BTN_request;

    //-- DB(s)
    FirebaseUser fUser;
    CollectionReference dataReference_User;

    //-- Progress Dialog
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cplatform_view);

        //---------------------------------------------------------------------------//
        //  Init Views
        //---------------------------------------------------------------------------//
        //-- Post
        TV_postDescription = findViewById(R.id.cplatform_view_postDescription_TV);
        TV_postTag = findViewById(R.id.cplatform_view_postTags_TV);
        TV_postTime = findViewById(R.id.cplatform_view_postTime_TV);
        TV_postDate = findViewById(R.id.cplatform_view_postDate_TV);

        //-- Store
        TV_storeName = findViewById(R.id.cplatform_view_storeName_TV);
        TV_mallName = findViewById(R.id.cplatform_view_mallName_TV);
        TV_storeUnit = findViewById(R.id.cplatform_view_storeUnit_TV);
        TV_storeTag = findViewById(R.id.cplatform_view_storeTag_TV);
        TV_storeRatingQuantity = findViewById(R.id.cplatform_view_ratingQuantity_TV);
        TV_storeRatingValue = findViewById(R.id.cplatform_view_storeRating_TV);
        RB_storeRating = findViewById(R.id.cplatform_view_storeRating_RB);
        IV_storeProfile = findViewById(R.id.cplatform_view_storeProfile_IV);
        RV_storeUploads = findViewById(R.id.cplatform_view_uploads_rv);

        //-- Button
        BTN_request = findViewById(R.id.cplatform_view_request_btn);

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
        actionbar.setTitle("Create a Collaboration");

        //-- Enable "Actionbar"->back button
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);

        //---------------------------------------------------------------------------//
        //  Add Event Listener
        //---------------------------------------------------------------------------//
        BTN_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAlertDialog_Request();
            }
        });

        //---------------------------------------------------------------------------//
        //  Database
        //---------------------------------------------------------------------------//
        //-- Init DB
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        dataReference_User = FirebaseFirestore.getInstance().collection("Users");

        //-- Update UI for "post" data
        //Description
        TV_postDescription.setText(postData.getDescription());
        //Tag
        String tags =  TextUtils.join(",", postData.getCollabTag());
        TV_postTag.setText(tags);
        //Date & Time
        Date date1 = null;
        try {
            date1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).parse(postData.getTimestamp());
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = dateFormat.format(date1);

        DateFormat dateFormat2 = new SimpleDateFormat("hh:mm aa");
        String strTime = dateFormat2.format(date1);
        TV_postTime.setText(strTime);
        TV_postDate.setText(strDate);

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

    void LoadData(final Bundle savedInstanceState){
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                postData= null;
            } else {
                postData= (CPlatform_Model) extras.getSerializable("post");
            }
        } else {
            postData= (CPlatform_Model) savedInstanceState.getSerializable("post");
        }
    }

    private void CreateAlertDialog_Request() {

        // Alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(CPlatformViewActivity.this);

        // Set Title
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to request?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SubmitRequestToDB();
                    }
                })
                .setNegativeButton(android.R.string.no, null);
        // Create and show dialog
        builder.create().show();
    }



    void SubmitRequestToDB()
    {
        //-- Init Dialog
        pd.setMessage("Please wait. Requesting...");
        pd.show();

        //------------------------------------------------------------------------------------------------------------//
        // Upload the model into database first
        //------------------------------------------------------------------------------------------------------------//

        CreateAlertDialog_Requested();
        pd.dismiss();

    }


    private void CreateAlertDialog_Requested() {

        // Alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(CPlatformViewActivity.this);

        // Custom layout for alert dialog
        LayoutInflater inflater = getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_alert_dialog_request, null))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(CPlatformViewActivity.this, CPlatformHomeActivity.class));
                        finish();
                    }
                });
        // Create and show dialog
        builder.create().show();
    }
}
