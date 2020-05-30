package com.example.crosssellers;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import Adapters.AdapterRequestPost_Profile_CPlatform;
import Models.CPlatform_Model;
import Models.RequestMailBox_Model;

public class ProfileActivity_CPlatformView extends AppCompatActivity {

    // DB
    FirebaseUser fUser;
    CollectionReference dataReference_User;
    CollectionReference dataReference_Request;
    CollectionReference dataReference_Notification;
    CollectionReference dataReference_CPlatform;

    // Get saved data
    CPlatform_Model postData;

    // Views
    TextView TV_postDescription, TV_postTag, TV_postTime, TV_postDate, TV_postTitle;
    RecyclerView RV_request, RV_accepted;
    Button BTN_deletePost, BTN_editPost;

    // Adapter
    AdapterRequestPost_Profile_CPlatform adapterRequestPending;
    List<RequestMailBox_Model> requestPendingList;
    LinearLayoutManager manager_requestPending;


    AdapterRequestPost_Profile_CPlatform adapterRequestAccepted;
    List<RequestMailBox_Model> requestAcceptedList;
    LinearLayoutManager manager_requestAccepted;

    // Progress Dialog
    ProgressDialog pd;

    boolean runOnce = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_cplatform_view);

        //---------------------------------------------------------------------------//
        //  Load Data (Carried from previous activity)
        //---------------------------------------------------------------------------//
        LoadData(savedInstanceState);

        // Init Progress Dialog
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.show();

        //---------------------------------------------------------------------------//
        //  Init Views
        //---------------------------------------------------------------------------//
        //-- Post
        TV_postDescription = findViewById(R.id.profile_cplatform_view_postDescription_TV);
        TV_postTitle = findViewById(R.id.profile_cplatform_view_postTitle_TV);
        TV_postTag = findViewById(R.id.profile_cplatform_view_postTags_TV);
        TV_postTime = findViewById(R.id.profile_cplatform_view_postTime_TV);
        TV_postDate = findViewById(R.id.profile_cplatform_view_postDate_TV);
        BTN_deletePost = findViewById(R.id.profile_cplatform_view_delete_btn);
        BTN_editPost = findViewById(R.id.profile_cplatform_view_edit_btn);

        //-- Request pending
        RV_request = findViewById(R.id.profile_cplatform_view_request_RV);

        //-- Accepted
        RV_accepted = findViewById(R.id.profile_cplatform_view_accepted_RV);

        //------------------------------------------------------------------------//
        // Init DB
        //------------------------------------------------------------------------//
        dataReference_User = FirebaseFirestore.getInstance().collection("Users");
        dataReference_Request = FirebaseFirestore.getInstance().collection("RequestMailBox");
        dataReference_Notification = FirebaseFirestore.getInstance().collection("Notifications");
        dataReference_CPlatform = FirebaseFirestore.getInstance().collection("CPlatform");
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        //------------------------------------------------------------------------//
        // Init RV
        //------------------------------------------------------------------------//
        //-- Adapter for requestPendingList
        requestPendingList = new ArrayList<>();
        adapterRequestPending = new AdapterRequestPost_Profile_CPlatform(this, requestPendingList, dataReference_User, postData, dataReference_Request, dataReference_Notification, dataReference_CPlatform, 1);
        manager_requestPending = new LinearLayoutManager(this);
        manager_requestPending.setOrientation(LinearLayoutManager.VERTICAL);
        RV_request.setLayoutManager(manager_requestPending);
        RV_request.setAdapter(adapterRequestPending);

        //-- Adapter for requestAcceptedList
        requestAcceptedList = new ArrayList<>();
        adapterRequestAccepted = new AdapterRequestPost_Profile_CPlatform(this, requestAcceptedList, dataReference_User, postData, dataReference_Request, dataReference_Notification, dataReference_CPlatform, 2);
        manager_requestAccepted = new LinearLayoutManager(this);
        manager_requestAccepted.setOrientation(LinearLayoutManager.VERTICAL);
        RV_accepted.setLayoutManager(manager_requestAccepted);
        RV_accepted.setAdapter(adapterRequestAccepted);

        getRequestPostRelatedToMe();

        //----------------------------------------------------------------------//
        // Action bar                                                           //
        //----------------------------------------------------------------------//
        //-- Add built-in "Actionbar" and it's "Actionbar"->title
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Collaboration Post Profile");

        //-- Enable "Actionbar"->back button
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);

        //------------------------------------------------------------------------//
        // Update UI for "post" data
        //------------------------------------------------------------------------//
        //Description
        TV_postDescription.setText(postData.getDescription());
        //Title
        TV_postTitle.setText(postData.getTitle());
        //Tag
        List<String> listTag = postData.getCollabTag();
        String tags =  TextUtils.join(",", listTag);
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


        //------------------------------------------------------------------------//
        // Click Listener
        //------------------------------------------------------------------------//
        BTN_editPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity_CPlatformView.this, EditCPlatformPostActivity.class);
                intent.putExtra("post", postData);
                startActivity(intent);
                finish();
            }
        });

        BTN_deletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> results = new HashMap<>();
                results.put("collab_closed_flag", true);
                pd.setMessage("Closing Post. Please wait...");
                pd.show();

                //-- Modify data => Query
                dataReference_CPlatform.document(postData.getCPost_uid()).update(results)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                pd.dismiss();
                                Intent intent = new Intent(ProfileActivity_CPlatformView.this, DashboardActivity.class);
                                startActivity(intent);
                                finish();
                            }
                });
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
        Intent intent = new Intent(ProfileActivity_CPlatformView.this, DashboardActivity.class);
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


    void getRequestPostRelatedToMe()
    { //--------------------------------------------------------------------------------//
        // (2) Get all the CPlatform Post
        //--------------------------------------------------------------------------------//
        // Get all data from path ^
        dataReference_Request.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                // If got error, end
                if(e != null)
                    return;

                // Check until required info is received
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges())
                {
                    RequestMailBox_Model model = doc.getDocument().toObject(RequestMailBox_Model.class);
                    model.setMyRequestMailBoxID(doc.getDocument().getId());


                    // Only show my post
                    if(!model.getRequester_UID().equals(fUser.getUid()))
                        continue;


                    //------------------------------------------------------------------------------//
                    // (3) If the post's tag, contains this user tag, then show it
                    //------------------------------------------------------------------------------//
                    //-- Is message added / modified / removed?
                    switch(doc.getType())
                    {
                        case ADDED:
                            //---------------------------------------------------------//
                            // Update UI to display the changes in the list
                            //---------------------------------------------------------//
                            if(model.getStatus().equals("pending"))
                            {
                                requestPendingList.add(model);
                                adapterRequestPending.notifyDataSetChanged();
                            }
                            else if(model.getStatus().equals("accepted"))
                            {
                                requestAcceptedList.add(model);
                                adapterRequestAccepted.notifyDataSetChanged();

                            }
                            break;
                        case MODIFIED:
                            if(model.getStatus().equals("accepted") && !runOnce)
                            {
                                runOnce = true;

                                Intent intent = new Intent(ProfileActivity_CPlatformView.this, ProfileActivity_CPlatformView.class);
                                intent.putExtra("post", postData);
                                startActivity(intent);
                                //requestPendingList.remove(model);
                                //requestAcceptedList.add(model);
                                //adapterRequestPending.notifyDataSetChanged();
                                //adapterRequestAccepted.notifyDataSetChanged();
                            }
                            if(model.getStatus().equals("rejected"))
                            {
                                requestPendingList.remove(model);
                                adapterRequestPending.notifyDataSetChanged();
                            }
                            break;
                        case REMOVED:
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + doc.getType());
                    }
                }

                pd.dismiss();
            }
        });
    }
}
