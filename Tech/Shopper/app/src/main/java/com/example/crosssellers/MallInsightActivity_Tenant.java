package com.example.crosssellers;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

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

import Adapters.AdapterCollabPost_CPlatform;
import Adapters.AdapterUser_Tenant;
import Models.CPlatform_Model;
import Models.User_Model;

public class MallInsightActivity_Tenant extends AppCompatActivity {

    //-- DB(s)
    FirebaseUser fUser;
    CollectionReference dataReference_CPlatform;
    CollectionReference dataReference_User;

    //-- Private Variable(s)
    List<String> selectedItems;

    //-- View(s)
    RecyclerView RV_users;
    Button BTN_filter;

    //-- Private variable(s)
    AdapterUser_Tenant adapterUser;
    List<User_Model> userList;
    LinearLayoutManager manager;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mall_insight_tenant);

        //-- Cache Reference
        RV_users = findViewById(R.id.mall_insight_tenant_RV);
        BTN_filter = findViewById(R.id.mall_insight_tenant_filter_btn);

        //-- Init DB
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.show();

        //----------------------------------------------------------------------//
        // Init RecyclerView List                                               //
        //----------------------------------------------------------------------//
        // Adapter
        userList = new ArrayList<>();
        adapterUser = new AdapterUser_Tenant(this, userList);
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        RV_users.setLayoutManager(manager);
        RV_users.setAdapter(adapterUser);

        //-- Retrieve data from database
        getUsers();


        BTN_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAlertDialog_Tag();
            }
        });

        //----------------------------------------------------------------------//
        // Action bar                                                           //
        //----------------------------------------------------------------------//
        //-- Add built-in "Actionbar" and it's "Actionbar"->title
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Tenant Insight");

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
        Intent intent = new Intent(MallInsightActivity_Tenant.this, MallInsightActivity_Home.class);
        startActivity(intent);
        finish();
    }


    void getUsers()
    {
        // Get Path of database named "Users" containing user info
        dataReference_CPlatform = FirebaseFirestore.getInstance().collection("CPlatform");
        dataReference_User = FirebaseFirestore.getInstance().collection("Users");


        dataReference_User.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                // If got error, end
                if(e != null)
                    return;

                // Check until required info is received
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges())
                {
                    User_Model model = doc.getDocument().toObject(User_Model.class);

                    // Dont show myself
                    if(model.getUid().equals(fUser.getUid()))
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
                            userList.add(model);
                            adapterUser.notifyDataSetChanged();
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
                pd.dismiss();
            }
        });
    }


    void CreateAlertDialog_Tag()
    {
        selectedItems = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(MallInsightActivity_Tenant.this);
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
                adapterUser.filter(selectedItems);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();
    }
}