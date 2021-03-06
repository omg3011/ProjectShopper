package com.example.crosssellers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import Adapters.AdapterUsers;
import Models.CPlatform_Model;
import Models.Notification_Model;
import Models.RequestMailBox_Model;
import Models.User_Model;

import static android.view.View.GONE;

public class CPlatformViewActivity extends AppCompatActivity {

    // Get saved data
    CPlatform_Model postData;
    List<String> uploadsImageList;

    // Views
    TextView TV_postDescription, TV_postTag, TV_postTime, TV_postDate, TV_postTitle;
    TextView TV_storeName, TV_mallName, TV_storeUnit, TV_storeTag, TV_storeRatingQuantity, TV_storeRatingValue;
    RatingBar RB_storeRating;
    ImageView IV_storeProfile;
    Button BTN_request;
    ScrollView SV_scroll;
    LinearLayout LL_uploads;

    //-- DB(s)
    FirebaseUser fUser;
    CollectionReference dataReference_User;
    FirebaseFirestore fireStore;
    CollectionReference dataReference_RequestMailBox;
    CollectionReference dataReference_Notification;
    CollectionReference dataReference_CPlatform;

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
        SV_scroll = findViewById(R.id.cplatform_view_scroller);
        TV_postDescription = findViewById(R.id.cplatform_view_postDescription_TV);
        TV_postTitle = findViewById(R.id.cplatform_view_postTitle_TV);
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
        LL_uploads = findViewById(R.id.cplatform_view_uploads_LL);

        //-- Button
        BTN_request = findViewById(R.id.cplatform_view_request_btn);

        //-- Default scroll all the way up
        SV_scroll.smoothScrollTo(0, 0);

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
        if(uploadsImageList != null && uploadsImageList.size() > 0)
        {
            for(String x : uploadsImageList)
            {
                // Convert String to Uri
                Uri myUri = Uri.parse(x);


                ImageView iv = new ImageView(CPlatformViewActivity.this);

                Picasso.get()
                        .load(myUri)
                        .placeholder(R.drawable.ic_add_image)
                        .error(R.drawable.ic_error)
                        .into(iv);

                addImageViewToLinearLayout(iv);
            }
        }

        //----------------------------------------------------------------------//
        // Action bar                                                           //
        //----------------------------------------------------------------------//
        //-- Add built-in "Actionbar" and it's "Actionbar"->title
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("View Collaboration");

        //-- Enable "Actionbar"->back button
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);



        //---------------------------------------------------------------------------//
        //  Database
        //---------------------------------------------------------------------------//
        //-- Init DB
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        fireStore = FirebaseFirestore.getInstance();
        dataReference_RequestMailBox = fireStore.collection("RequestMailBox");
        dataReference_User = fireStore.collection("Users");
        dataReference_Notification = fireStore.collection("Notifications");
        dataReference_CPlatform = fireStore.collection("CPlatform");

        //------------------------------------------------------------------------//
        // Update UI for "post" data
        //------------------------------------------------------------------------//
        //Description
        TV_postDescription.setText(postData.getDescription());
        //Title
        TV_postTitle.setText(postData.getTitle());
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

        //---------------------------------------------------------------------------//
        //  Add Event Listener
        //---------------------------------------------------------------------------//
        // Get all data from path ^
        dataReference_RequestMailBox.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean found = false;
                            List<DocumentSnapshot> myListOfDocuments = task.getResult().getDocuments();

                            for(DocumentSnapshot doc : myListOfDocuments)
                            {
                                RequestMailBox_Model rModel = doc.toObject(RequestMailBox_Model.class);

                                // CPostID is same
                                if(rModel.getCplatformPost_ID().equals(postData.getCPost_uid()))
                                {
                                    // I am the requester
                                    if(rModel.getRequester_UID().equals(fUser.getUid()))
                                    {
                                        found = true;
                                        BTN_request.setText(rModel.getStatus().toUpperCase());
                                        BTN_request.setBackgroundColor(Color.GRAY);
                                        break;
                                    }
                                }
                            }

                            if(!found)
                            {
                                // Edit
                                if(postData.getPosterUid().equals(fUser.getUid()))
                                {
                                    BTN_request.setText("Edit Post");
                                    BTN_request.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Create_to_EditPost();
                                        }
                                    });
                                }
                                // Request
                                else
                                {
                                    BTN_request.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            CreateAlertDialog_Request();
                                        }
                                    });
                                }
                            }
                        }
                    }
                });


        //-- Update UI for "store" data
        //-- Retrieve data from database
        DocumentReference doc = dataReference_User.document(fUser.getUid());
        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final User_Model user = documentSnapshot.toObject(User_Model.class);
                TV_storeName.setText(documentSnapshot.getString("storeName"));
                TV_mallName.setText(documentSnapshot.getString("mallName"));
                TV_storeUnit.setText(documentSnapshot.getString("storeUnit"));
                TV_storeTag.setText(documentSnapshot.getString("storeTag"));

                // Ratings

                String imageUri = documentSnapshot.getString("image");
                if(imageUri.isEmpty())
                    imageUri = "Empty";

                Picasso.get()
                        .load(imageUri)
                        .placeholder(R.drawable.ic_add_image)
                        .error(R.drawable.ic_error)
                        .into(IV_storeProfile);


                RB_storeRating.setMax(5);
                if(user.getRatingList() == null || user.getRatingList().size() <= 0)
                {
                    RB_storeRating.setRating(0);
                    TV_storeRatingQuantity.setText("(0)");
                    TV_storeRatingValue.setText("0.0");
                }
                else
                {
                    RB_storeRating.setRating(GetRatingFromList(user.getRatingList()));
                    TV_storeRatingQuantity.setText("("+Integer.toString(user.getRatingList().size()) + ")");
                    TV_storeRatingValue.setText(Float.toString(RB_storeRating.getRating()));
                }
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

    Float GetRatingFromList(List<Double> ratings)
    {
        String rateString = "";
        double rateValue = 0.0f;

        for(Double x : ratings)
        {
            rateValue += x;
        }

        rateValue /= ratings.size();

        return (float)rateValue;
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



    void addImageViewToLinearLayout(ImageView iv)
    {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 10, 10);
        iv.setLayoutParams(layoutParams);

        LL_uploads.addView(iv);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CPlatformViewActivity.this, CPlatformHomeActivity.class);
        startActivity(intent);
        finish();
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

    void Create_to_EditPost()
    {
        Intent intent = new Intent(CPlatformViewActivity.this, EditCPlatformPostActivity.class);
        intent.putExtra("post", postData);
        startActivity(intent);
        finish();
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
        String status = "pending";
        final String cplatform_postID = postData.getCPost_uid();
        final String uid = fUser.getUid();
        final String request_postID = dataReference_RequestMailBox.document().getId().toString();

        RequestMailBox_Model requestModel = new RequestMailBox_Model(status, cplatform_postID, uid, request_postID, postData.getPosterUid());

        dataReference_RequestMailBox.document(request_postID).set(requestModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //-- Get current Timestamp
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
                final String timestampPost = simpleDateFormat.format(new Date());

                // Send to poster ("Someone requested to collaborate with you")
                final String[] posterStoreName = new String[1];
                dataReference_User.document(cplatform_postID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        //-----------------------------------------------------------------------//
                        // Get Store Name
                        //-----------------------------------------------------------------------//
                        posterStoreName[0] = (String) documentSnapshot.get("storeName");

                        final String notify_id1 = dataReference_Notification.document().getId();
                        Notification_Model notification1 = new Notification_Model(timestampPost, cplatform_postID, posterStoreName[0] + " requested to collaborate with you.", notify_id1);
                        dataReference_Notification.document(notify_id1).set(notification1);

                        // Send to requester ("You have request")
                        final String notify_id2 = dataReference_Notification.document().getId();
                        Notification_Model notification2 = new Notification_Model(timestampPost, uid, "You have requested a collaboration with " + posterStoreName[0] + ".", notify_id2);
                        dataReference_Notification.document(notify_id2).set(notification2);

                        // Update count requester for CPlatform
                        HashMap<String, Object> results = new HashMap<>();
                        int incrementRequesterCount = postData.getPendingRequestCount() + 1;
                        results.put("pendingRequestCount", incrementRequesterCount);

                        //-- Modify data => Query
                        dataReference_CPlatform.document(postData.getCPost_uid()).update(results)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        pd.dismiss();
                                        CreateAlertDialog_Requested();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(CPlatformViewActivity.this, "Error: Image not updated..", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                });
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(CPlatformViewActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void CreateAlertDialog_Requested() {

        // Alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(CPlatformViewActivity.this);

        // Custom layout for alert dialog
        LayoutInflater inflater = getLayoutInflater();
        View content =  inflater.inflate(R.layout.custom_alert_dialog_collab_request, null);
        builder.setView(content)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(CPlatformViewActivity.this, CPlatformHomeActivity.class));
                        finish();
                    }
                });

        TextView TV_title = (TextView)content.findViewById(R.id.custom_alert_dialog_request_title_TV);
        TextView TV_description = (TextView) content.findViewById(R.id.custom_alert_dialog_request_description_TV);
        TextView TV_tag = (TextView) content.findViewById(R.id.custom_alert_dialog_request_tags_TV);
        TextView TV_time = (TextView) content.findViewById(R.id.custom_alert_dialog_request_time_TV);
        TextView TV_date = (TextView) content.findViewById(R.id.custom_alert_dialog_request_date_TV);

        TV_title.setText(postData.getTitle());
        TV_description.setText(postData.getDescription());
        TV_date.setText(TV_postDate.getText());
        TV_time.setText(TV_postTime.getText());
        TV_tag.setText(TV_postTag.getText());

        // Create and show dialog
        builder.create().show();
    }
}
