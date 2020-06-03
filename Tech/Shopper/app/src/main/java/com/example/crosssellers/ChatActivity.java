package com.example.crosssellers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import Adapters.AdapterChat;
import Models.CPlatform_Model;
import Models.CPromotion_Model;
import Models.Chat_Model;
import Models.Notification_Model;
import Models.RequestMailBox_Model;

public class ChatActivity extends AppCompatActivity {

    //-- Cache Reference(s)
    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView IV_profile;
    TextView TV_name, TV_status;
    EditText ET_message;
    ImageButton BTN_send;
    Button BTN_details;
    Button BTN_completed;

    //-- Firebase Cache Reference(s)
    FirebaseAuth firebaseAuth;              // Usage: Access firebase
    FirebaseFirestore fireStore;            // Usage: Access database
    CollectionReference dataReference_user; // Usage: (EventListener) Check if user got OnDataChanged() for "new" user
    CollectionReference dataReference_chat; // Usage: (EventListener) Check if user got OnDataChanged() for "new/modified" messages
    CollectionReference dataReference_seen; // Usage: (EventListener) Check if user has OnDataChanged() for "seen" chat messages
    CollectionReference dataReference_CPlatform;
    CollectionReference dataReference_RequestMailBox;
    CollectionReference dataReference_Notification;

    List<Chat_Model> chatList;
    AdapterChat adapterChat;

    //-- Chat (P1 and P2)
    String hisUid;                          // Usage: Receiver(Others) unique id from database
    String myUid;                           // Usage: Sender(Me) unique id from database
    String hisImage;                        // Usage: Receiver(Others) profile picture url
    RequestMailBox_Model requestPost;
    CPlatform_Model cplatformPost;

    //-- Progres Dialog
    ProgressDialog pd;

    boolean init = false;
    //-------------------------------------------------------------------------------------------------------------------------------------------//
    //
    // Built-In Function(s)
    //
    //-------------------------------------------------------------------------------------------------------------------------------------------//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //-- Init Views
        Toolbar toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        recyclerView = findViewById(R.id.chat_RV);
        IV_profile = findViewById(R.id.chat_profileIV);
        TV_name = findViewById(R.id.chat_nameTV);
        TV_status = findViewById(R.id.chat_statusTV);
        ET_message = findViewById(R.id.chat_messageET);
        BTN_send = findViewById(R.id.chat_sendBTN);
        BTN_details = findViewById(R.id.chat_view_c_details_btn);
        BTN_completed = findViewById(R.id.chat_completed_btn);

        //-- Init Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        dataReference_user = fireStore.collection("Users");
        dataReference_chat = fireStore.collection("Chats");
        dataReference_seen = fireStore.collection("Chats");

        dataReference_CPlatform = fireStore.collection("CPlatform");
        dataReference_RequestMailBox = fireStore.collection("RequestMailBox");
        dataReference_Notification = fireStore.collection("Notifications");

        pd = new ProgressDialog(this);

        //----------------------------------------------------------------------//
        // Action bar                                                           //
        //----------------------------------------------------------------------//
        //-- Add built-in "Actionbar" and it's "Actionbar"->title
        ActionBar actionbar = getSupportActionBar();

        //-- Enable "Actionbar"->back button
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayHomeAsUpEnabled(true);
        //actionbar.setDisplayShowHomeEnabled(true);

        //--------------------------------------------------------------------------------------//
        // Setup Scrolling List UI
        //--------------------------------------------------------------------------------------//
        //-- (LinearLayout) Layout for RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        //Set recyclerView properties
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        //------------------------------------------------------------------------------------------------//
        // From previous activity, we have passed receiver(others) Uid using intent
        // -> So now, get his uid here which we use to get the profile picture, name, chat with that user
        //------------------------------------------------------------------------------------------------//
        // Get save data from previous activity
        Intent intent = getIntent();
        hisUid = intent.getStringExtra("hisUid");

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            requestPost= null;
            cplatformPost= null;
        } else {
            requestPost = (RequestMailBox_Model) extras.getSerializable("requestPost");
            cplatformPost = (CPlatform_Model) extras.getSerializable("cplatformPost");
        }





        //--------------------------------------------------------------------------------------//
        // Setup Event Listener
        //--------------------------------------------------------------------------------------//
        BTN_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAlertDialog_Details();
            }
        });

        if(cplatformPost.getPosterUid().equals(firebaseAuth.getCurrentUser().getUid()))
        {
            BTN_completed.setVisibility(View.VISIBLE);
            BTN_completed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CreateAlertDialog_Complete();
                }
            });
        }
        else
        {
            BTN_completed.setVisibility(View.GONE);
        }

        //-- Listener to update data of (UI) ChatActivity, the most TOP ui
        dataReference_user.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(e != null)
                    return;

                // Check until required info is received
                for (DocumentSnapshot doc : queryDocumentSnapshots)
                {
                    String otherUid = doc.getString("uid");

                    // If null, we ignore
                    if(TextUtils.isEmpty(otherUid) || TextUtils.isEmpty(hisUid))
                        continue;

                    // Only look for receiver (other) Uid, since we are only talking to him.
                    if(otherUid.equals(hisUid))
                    {
                        // Get data
                        String storeName = doc.getString("storeName");
                        String email = doc.getString("email");
                        String onlineStatus = doc.getString("onlineStatus");
                        hisImage = doc.getString("image");

                        //---------------------------------------------------------//
                        // Update "Status" ui
                        //---------------------------------------------------------//
                        //-- Display "online"
                        if(onlineStatus.equals("online")) {
                            TV_status.setText(onlineStatus);
                        }
                        //-- Display ""
                        else if(TextUtils.isEmpty(onlineStatus)) {
                            TV_status.setText("offline");
                        }

                        //-- Display "Last seen at: dd/mm/yyyy hh:mm am/pm"
                        else{
                            // Convert time stamp to dd/mm/yyyy hh:mm am/pm
                            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                            cal.setTimeInMillis(Long.parseLong(onlineStatus));
                            String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();
                            TV_status.setText("Last seen at: " + dateTime);
                        }

                        //---------------------------------------------------------//
                        // String cannot be empty, will crash
                        //---------------------------------------------------------//
                        if(storeName.isEmpty()) storeName = email;

                        if(hisImage.isEmpty())
                        {
                            Log.d("Test", "Error getting image");
                            hisImage = "Error";
                        }

                        //---------------------------------------------------------//
                        // Update receiver (other) "storeName" ui
                        //---------------------------------------------------------//
                        TV_name.setText(storeName);


                        //---------------------------------------------------------//
                        // Update receiver "profile picture" ui
                        //---------------------------------------------------------//
                        //-- Set Image
                        Picasso.get()
                                .load(hisImage)
                                .placeholder(R.drawable.ic_default_image)
                                .error(R.drawable.ic_error)
                                .into(IV_profile);
                    }
                }


                if(!init)
                {
                    init = true;


                    //-- Listener for click Button to send message
                    BTN_send.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Get text from editText
                            String message = ET_message.getText().toString().trim();
                            // Check if text is empty or not
                            if(TextUtils.isEmpty(message))
                            {
                                // Text empty
                                Toast.makeText(ChatActivity.this, "Cannot send empty message...", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                // Text not empty, then we send message
                                sendMessage(message);
                            }
                        }
                    });


                    //---------------------------------------------------------------------------------------------//
                    // Setup private variable (Treat this as smart pointer, don't need to "delete" after "new")
                    //---------------------------------------------------------------------------------------------//
                    chatList = new ArrayList<>();


                    // Adapter
                    adapterChat = new AdapterChat(ChatActivity.this, chatList, hisImage);
                    adapterChat.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterChat);

                    //--------------------------------------------------------------------------------------//
                    // Setup 2 more Event Listener
                    //--------------------------------------------------------------------------------------//
                    readMessages();
                    seenMessage();
                }
            }
        });




    }


    private void CreateAlertDialog_Details() {

        // Alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);

        // Custom layout for alert dialog
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_dialog_cpost_details, null);

        //-- Title
        TextView tv_title = view.findViewById(R.id.custom_dialog_cpost_cdetails_title_TV);
        tv_title.setText(cplatformPost.getTitle());

        //-- Description
        TextView tv_description = view.findViewById(R.id.custom_dialog_cpost_cdetails_description_TV);
        tv_description.setText(cplatformPost.getDescription());

        //-- Tags
        TextView tv_tag = view.findViewById(R.id.custom_dialog_cpost_cdetails_tags_TV);
        String tags = TextUtils.join(", ", cplatformPost.getCollabTag());
        tv_tag.setText(tags);

        Date date1 = null;
        try {
            date1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).parse(cplatformPost.getTimestamp());
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        java.text.DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = dateFormat.format(date1);

        java.text.DateFormat dateFormat2 = new SimpleDateFormat("hh:mm aa");
        String strTime = dateFormat2.format(date1);

        //-- Date Posted
        TextView tv_date = view.findViewById(R.id.custom_dialog_cpost_cdetails_date_TV);
        tv_date.setText(strDate);

        //-- Time Posted
        TextView tv_time = view.findViewById(R.id.custom_dialog_cpost_cdetails_time_TV);
        tv_time.setText(strTime);


        builder.setView(view)
                .setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        // Create and show dialog
        builder.create().show();
    }


    private void CreateAlertDialog_Complete() {

        // Alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);

        // Set Title
        builder.setTitle("Confirmation");
        builder.setMessage("Do you want to end the collaboration?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        pd.setMessage("Closing collaboration. Please wait..");
                        pd.show();

                        // Close CPlatform->Is_Collab_flag
                        HashMap<String, Object> cpostResult = new HashMap<>();
                        cpostResult.put("collab_closed_flag", true);
                        dataReference_CPlatform.document(cplatformPost.getCPost_uid()).update(cpostResult).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                // Remove RequestMailBox
                                HashMap<String, Object> crequestResult = new HashMap<>();
                                crequestResult.put("status", "completed");
                                dataReference_RequestMailBox.document(requestPost.getRequestMailBoxID()).update(crequestResult).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Test", "Deleted: " + requestPost.getRequestMailBoxID());
                                        pd.dismiss();
                                        CreateDialog_Rate();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("Test", "Failed");
                                    }
                                });
                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.no, null);
        // Create and show dialog
        builder.create().show();
    }

    void CreateDialog_Rate()
    {

        //-- Get Timestamp
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        String timestamp = simpleDateFormat.format(new Date());

        final String notify_id = dataReference_Notification.document().getId();
        Notification_Model notification = new Notification_Model(timestamp, cplatformPost.getPosterUid(), "You have completed a collaboration " + cplatformPost.getTitle() + ".", notify_id);
        dataReference_Notification.document(notify_id).set(notification);

        // Alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);

        // Custom layout for alert dialog
        LayoutInflater inflater = getLayoutInflater();
        View content =  inflater.inflate(R.layout.custom_alert_dialog_rate, null);
        final RatingBar ratingBar = (RatingBar)content.findViewById(R.id.custom_alert_dialog_rate_rating_RB);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            // Called when the user swipes the RatingBar
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

            }
        });
        builder.setView(content)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which) {

                        pd.setMessage("Closing collaboration...");
                        pd.show();
                        //HashMap<String, Object> userResult = new HashMap<>();
                        //userResult.put("ratingList", ratingBar.getRating());
                        dataReference_user.document(hisUid).update("ratingList", FieldValue.arrayUnion(ratingBar.getRating())).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                pd.dismiss();
                                startActivity(new Intent(ChatActivity.this, DashboardActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });



        // Create and show dialog
        builder.create().show();


    }
    @Override
    protected void onStart() {
        //--------------------------------------------------------------------------------------//
        // Check if i'm logged in, this is just safety-precaution
        //--------------------------------------------------------------------------------------//
        checkUserStatus();

        //--------------------------------------------------------------------------------------//
        // Sender is now online, so update database value, to tell them display sender "online"
        //--------------------------------------------------------------------------------------//
        checkOnlineStatus("online");

        super.onStart();
    }

    @Override
    public boolean onSupportNavigateUp() {
        //-- Go to previous activity
        onBackPressed();

        return super.onSupportNavigateUp();
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ChatActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Get current timestamp
        String timestamp = String.valueOf(System.currentTimeMillis());

        //---------------------------------------------------------------------------------------------------------//
        // Sender is now offline, so update database value, to tell them display sender "Last seen at: timestamp"
        //---------------------------------------------------------------------------------------------------------//
        checkOnlineStatus(timestamp);

    }

    @Override
    protected void onResume() {
        //--------------------------------------------------------------------------------------//
        // Sender is now online, so update database value, to tell them display sender "online"
        //--------------------------------------------------------------------------------------//
        checkOnlineStatus("online");


        super.onResume();

    }



    //-------------------------------------------------------------------------------------------------------------------------------------------//
    //
    // User-Defined Function(s)
    //
    //-------------------------------------------------------------------------------------------------------------------------------------------//
    //-------------------------------------------------------------------------------------------------//
    // Function: (EventListener) If receiver seen sender message, notify sender, change "Delivered" to "Seen"
    //-------------------------------------------------------------------------------------------------//
    private void seenMessage() {
        dataReference_seen.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                // If got error, end
                if(e != null)
                    return;

                // Check until required info is received
                List<String> seenList = new ArrayList<>();
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    Chat_Model chat = doc.toObject(Chat_Model.class);
                    if(chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid))
                    {
                        // If chat is not seen, we write to database modify it to "seen" = true
                        if(chat.isSeen() == false)
                            seenList.add(doc.getId());
                    }
                }
                updateSeen(seenList);
            }
        });
    }

    //-------------------------------------------------------------------------------------------------//
    // Function: Continuation of "seenMessage()", notify database, message seen
    //-------------------------------------------------------------------------------------------------//
    private void updateSeen(List<String> list) {
        // Get a new write batch
        WriteBatch batch = fireStore.batch();

        // Iterate through the list
        for (int k = 0; k < list.size(); k++) {

            // Update each list item
            DocumentReference ref = dataReference_seen.document(list.get(k));
            batch.update(ref, "seen", true);
        }

        // Commit the batch
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Yay its all done in one go!
            }
        });
    }

    //-------------------------------------------------------------------------------------------------//
    // Function: (EventListener) OnDataChange(), display all chat messages
    //-------------------------------------------------------------------------------------------------//
    private void readMessages() {
        Log.d("Test", "readMessages");
        final int[] sized = {0};
        dataReference_chat.orderBy("timestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                // If got error, end
                if(e != null)
                    return;

                // Check until required info is received
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                    //-- Convert data from database into a class
                    Chat_Model chat = doc.getDocument().toObject(Chat_Model.class);

                    //-----------------------------------------------------------------------//
                    //-- Receive new message, so we need update
                    //-----------------------------------------------------------------------//
                    if(chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid) ||
                            chat.getReceiver().equals(hisUid) && chat.getSender().equals(myUid)
                    )
                    {
                        //-- Is message added / modified / removed?
                        switch(doc.getType()) {
                            case ADDED:
                                // Add message to list
                                chatList.add(chat);

                                //---------------------------------------------------------//
                                // Update UI to display the changes in the list
                                //---------------------------------------------------------//
                                // Scroll all the way down
                                sized[0] = chatList.size() - 1;
                                if(sized[0] < 0) sized[0] = 0;
                                recyclerView.scrollToPosition(sized[0]);

                                // Update display ui to display modified value
                                adapterChat.notifyDataSetChanged();
                                break;
                            case MODIFIED:
                                //-----------------------------------------------------------------------//
                                //-- Receive new message, so we need update status: "delivered / seen"
                                //-----------------------------------------------------------------------//
                                if((boolean)doc.getDocument().get("seen") == true)
                                {
                                    // Update modified value
                                    chatList.set(chatList.size()-1, chat);
                                    adapterChat.setLastMessageSeen(chat);

                                    // Update display ui to display modified value
                                    adapterChat.notifyDataSetChanged();

                                    // Scroll all the way down
                                    int sized = chatList.size() - 1;
                                    if(sized < 0) sized = 0;
                                    recyclerView.scrollToPosition(sized);
                                }
                                break;
                            case REMOVED:
                                // To be done later
                                chatList.remove(chat);
                                // Update display ui to display modified value
                                adapterChat.notifyDataSetChanged();

                                // Scroll all the way down
                                sized[0] = chatList.size() - 1;
                                if(sized[0] < 0) sized[0] = 0;
                                recyclerView.scrollToPosition(sized[0]);

                                break;
                        }
                    }


                }
            }
        });
    }

    //-------------------------------------------------------------------------------------------------//
    // Function: Add new chat message to Database
    //-------------------------------------------------------------------------------------------------//
    private void sendMessage(String message) {
        /* "Chats" node will be created that will contain all chats
         * > Whenever user sends message, it will create new child in "Chats" node and that child contains
         *   - sender: UID of sender
         *   - receiver: UID of receiver
         *   - message: actual message
         */

        //-- Push to database
        String timestamp = String.valueOf(System.currentTimeMillis());
        Chat_Model chat_model = new Chat_Model(myUid, hisUid, message, timestamp, false, requestPost.getCplatformPost_ID());
        dataReference_chat.add(chat_model);

        //-- Reset editText after sending message
        ET_message.setText("");
    }

    //-------------------------------------------------------------------------------------------------//
    // Function: Sender is now online, so update database value, to tell everyone display this sender "online"
    //-------------------------------------------------------------------------------------------------//
    private void checkOnlineStatus(String status)
    {
        dataReference_user.document(myUid).update("onlineStatus", status);
    }

    //-------------------------------------------------------------------------------------------------//
    // Function: Check if i'm logged in, this is just safety-precaution
    //-------------------------------------------------------------------------------------------------//
    private void checkUserStatus()
    {
        //-- Get Current User
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //-- User is signed in.
        if(user != null)
        {
            myUid = user.getUid();
        }

        //-- User not signed in
        else
        {
            startActivity(new Intent(ChatActivity.this, SelectMallActivity.class));
            finish();
        }
    }


}
