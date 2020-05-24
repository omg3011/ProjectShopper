package com.example.crosssellers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.firestore.WriteBatch;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import Adapters.AdapterChat;
import Models.Chat_Model;

public class ChatActivity extends AppCompatActivity {

    //-- Cache Reference(s)
    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView IV_profile;
    TextView TV_name, TV_status;
    EditText ET_message;
    ImageButton BTN_send;

    //-- Firebase Cache Reference(s)
    FirebaseAuth firebaseAuth;              // Usage: Access firebase
    FirebaseFirestore fireStore;            // Usage: Access database
    CollectionReference dataReference_user; // Usage: (EventListener) Check if user got OnDataChanged() for "new" user
    CollectionReference dataReference_chat; // Usage: (EventListener) Check if user got OnDataChanged() for "new/modified" messages
    CollectionReference dataReference_seen; // Usage: (EventListener) Check if user has OnDataChanged() for "seen" chat messages

    List<Chat_Model> chatList;
    AdapterChat adapterChat;

    //-- Chat (P1 and P2)
    String hisUid;                          // Usage: Receiver(Others) unique id from database
    String myUid;                           // Usage: Sender(Me) unique id from database
    String hisImage;                        // Usage: Receiver(Others) profile picture url

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

        //-- Init Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        dataReference_user = fireStore.collection("Users");
        dataReference_chat = fireStore.collection("Chats");
        dataReference_seen = fireStore.collection("Chats");


        //----------------------------------------------------------------------//
        // Action bar                                                           //
        //----------------------------------------------------------------------//
        //-- Add built-in "Actionbar" and it's "Actionbar"->title
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Chat");

        //-- Enable "Actionbar"->back button
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);

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




        //--------------------------------------------------------------------------------------//
        // Setup Event Listener
        //--------------------------------------------------------------------------------------//
        //-- Listener to update data of (UI) ChatActivity, the most TOP ui
        dataReference_user.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

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
                        String name = doc.getString("name");
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
                            TV_status.setText("");
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
                        if(name.isEmpty()) name = email;

                        if(hisImage.isEmpty()) hisImage = "Error";

                        //---------------------------------------------------------//
                        // Update receiver (other) "name" ui
                        //---------------------------------------------------------//
                        TV_name.setText(name);


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
            }
        });



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

        //--------------------------------------------------------------------------------------//
        // Setup 2 more Event Listener
        //--------------------------------------------------------------------------------------//
        readMessages();
        seenMessage();
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

    //-------------------------------------------------------------------------------------------------//
    // Function: Display Buttons in the action bar
    //-------------------------------------------------------------------------------------------------//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Hide SearchView, as we dont need it here
        menu.findItem(R.id.action_search).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    //-------------------------------------------------------------------------------------------------//
    // Function: Click Buttons in the action bar
    //-------------------------------------------------------------------------------------------------//
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_logout)
        {
            //---------------------------------------------------------------------------------//
            // Sign out of google account
            //---------------------------------------------------------------------------------//
            //-- Check is sign-in using google
            GoogleSignInOptions gso = new GoogleSignInOptions.
                    Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                    build();
            GoogleSignInClient googleSignInClient= GoogleSignIn.getClient(this,gso);

            if(googleSignInClient != null)
            {
                googleSignInClient.signOut();
            }

            //---------------------------------------------------------------------------------//
            // Sign out of firebase database
            //---------------------------------------------------------------------------------//
            firebaseAuth.signOut();

            //---------------------------------------------------------------------------------//
            // Go to home
            //---------------------------------------------------------------------------------//
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
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
                                // Adapter
                                adapterChat = new AdapterChat(ChatActivity.this, chatList, hisImage);
                                adapterChat.notifyDataSetChanged();
                                recyclerView.setAdapter(adapterChat);


                                // Scroll all the way down
                                recyclerView.scrollToPosition(chatList.size() - 1);
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
                                    recyclerView.scrollToPosition(chatList.size() - 1);
                                }
                                break;
                            case REMOVED:
                                // To be done later
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
        Chat_Model chat_model = new Chat_Model(myUid, hisUid, message, timestamp, false);
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
            startActivity(new Intent(ChatActivity.this, MainActivity.class));
            finish();
        }
    }


}
