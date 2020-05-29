package com.example.crosssellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Models.CPromotion_Model;

public class ProfileActivity_CPromotionView extends AppCompatActivity {

    // Get saved data
    CPromotion_Model postData;
    List<String> uploadsImageList;

    // Views
    TextView TV_postDescription, TV_postTag, TV_promoDate, TV_postTitle;
    ScrollView SV_scroller;
    LinearLayout LL_uploads;
    Button BTN_delete, BTN_edit;

    //-- DB(s)
    FirebaseUser fUser;
    FirebaseFirestore fireStore;
    CollectionReference dataReference_User;
    CollectionReference dataReference_Promo;

    //-- Progress Dialog
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_cpromotion_view);

        //---------------------------------------------------------------------------//
        //  Init Views
        //---------------------------------------------------------------------------//
        //-- Post
        TV_postDescription = findViewById(R.id.profile_promo_view_postDescription_TV);
        TV_postTitle = findViewById(R.id.profile_promo_view_postTitle_TV);
        TV_postTag = findViewById(R.id.profile_promo_view_postTags_TV);
        TV_promoDate = findViewById(R.id.profile_promo_view_promoDate_TV);

        //-- Button
        BTN_delete = findViewById(R.id.profile_promo_view_delete_btn);
        BTN_edit = findViewById(R.id.profile_promo_view_edit_btn);

        //-- Store
        SV_scroller = findViewById(R.id.profile_promo_view_scroller);
        LL_uploads = findViewById(R.id.profile_promo_view_image_LL);

        //-- Default scroll all the way up
        SV_scroller.smoothScrollTo(0, 0);

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

        //-- For every uri string, create a ImageView and get it parent in LinearLayout
        for(String x : uploadsImageList)
        {
            // Convert String to Uri
            Uri myUri = Uri.parse(x);


            ImageView iv = new ImageView(ProfileActivity_CPromotionView.this);

            Picasso.get()
                    .load(myUri)
                    .placeholder(R.drawable.ic_add_image)
                    .error(R.drawable.ic_error)
                    .into(iv);

            addImageViewToLinearLayout(iv);
        }
        pd.dismiss();

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
        dataReference_Promo = fireStore.collection("Promotions");

        //-- Update UI for "post" data
        //Description
        TV_postDescription.setText(postData.getDescription());
        //Title
        TV_postTitle.setText(postData.getTitle());
        //Tag
        String tags =  TextUtils.join(",", postData.getTags());
        TV_postTag.setText(tags);
        //Date
        TV_promoDate.setText(postData.getTimestampStart() + " - " + postData.getTimestampEnd());


        //------------------------------------------------------------------------//
        // Click Listener
        //------------------------------------------------------------------------//
        BTN_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> results = new HashMap<>();
                results.put("collab_closed_flag", true);
                pd.setMessage("Closing Post. Please wait...");
                pd.show();

                //-- Modify data => Query
                dataReference_Promo.document(postData.getPromotionPost_uid()).update(results)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                pd.dismiss();
                                Intent intent = new Intent(ProfileActivity_CPromotionView.this, DashboardActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
            }
        });
    }

    void addImageViewToLinearLayout(ImageView iv)
    {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 10, 10);
        iv.setLayoutParams(layoutParams);

        LL_uploads.addView(iv);
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
        Intent intent = new Intent(ProfileActivity_CPromotionView.this, DashboardActivity.class);
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
