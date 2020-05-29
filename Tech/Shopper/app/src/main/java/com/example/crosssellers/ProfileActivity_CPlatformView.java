package com.example.crosssellers;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
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

    // Get saved data
    CPlatform_Model postData;

    // Views
    TextView TV_postDescription, TV_postTag, TV_postTime, TV_postDate, TV_postTitle;
    RecyclerView RV_request, RV_accepted;

    // Adapter
    AdapterRequestPost_Profile_CPlatform adapterRequest;
    List<RequestMailBox_Model> requestList;
    LinearLayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_cplatform_view);

        //---------------------------------------------------------------------------//
        //  Load Data (Carried from previous activity)
        //---------------------------------------------------------------------------//
        LoadData(savedInstanceState);


        //---------------------------------------------------------------------------//
        //  Init Views
        //---------------------------------------------------------------------------//
        //-- Post
        TV_postDescription = findViewById(R.id.profile_cplatform_view_postDescription_TV);
        TV_postTitle = findViewById(R.id.profile_cplatform_view_postTitle_TV);
        TV_postTag = findViewById(R.id.profile_cplatform_view_postTags_TV);
        TV_postTime = findViewById(R.id.profile_cplatform_view_postTime_TV);
        TV_postDate = findViewById(R.id.profile_cplatform_view_postDate_TV);

        //-- Request pending
        RV_request = findViewById(R.id.profile_cplatform_view_request_RV);

        //-- Accepted
        RV_accepted = findViewById(R.id.profile_cplatform_view_accepted_RV);

        //------------------------------------------------------------------------//
        // Init DB
        //------------------------------------------------------------------------//
        dataReference_User = FirebaseFirestore.getInstance().collection("Users");
        dataReference_Request = FirebaseFirestore.getInstance().collection("RequestMailBox");
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        //------------------------------------------------------------------------//
        // Init RV
        //------------------------------------------------------------------------//
        //-- Adapter for requestList
        requestList = new ArrayList<>();
        adapterRequest = new AdapterRequestPost_Profile_CPlatform(this, requestList, dataReference_User, postData);
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        RV_request.setLayoutManager(manager);
        RV_request.setAdapter(adapterRequest);
        getRequestPostRelatedToMe();

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


        //2. Get your RequestMailBox info from db
        //   Get your requester UserProfile_Model

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

                    // Only show my post
                    if(!model.getUid().equals(fUser.getUid()))
                    {
                        continue;
                    }


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
                            requestList.add(model);

                            adapterRequest.notifyDataSetChanged();
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
}
