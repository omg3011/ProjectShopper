package com.example.crosssellers;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetupProfileActivity extends AppCompatActivity {


    private List<String> selectedItems = new ArrayList<>();
    Button BTN_select_tags, BTN_submit;
    TextView TV_setup_profile_storeTags;
    EditText ET_storeName, ET_storeUnit;


    //-- Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);


        //-------------------------------------------------------------------------------
        // Handle Firebase
        //-------------------------------------------------------------------------------
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        final FirebaseFirestore fireStore = FirebaseFirestore.getInstance();

        //----------------------------------------------------------------------//
        // Action bar                                                           //
        //----------------------------------------------------------------------//
        //-- Add built-in "Actionbar" and it's "Actionbar"->title
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Setup Profile");

        TV_setup_profile_storeTags = findViewById(R.id.setup_profile_storetags_TV);
        ET_storeName = findViewById(R.id.setup_profile_storeName_et);
        ET_storeUnit = findViewById(R.id.setup_profile_storeUnit_et);
        BTN_select_tags = findViewById(R.id.setup_profile_tag_button);
        BTN_select_tags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAlertDialog();
            }
        });

        BTN_submit = findViewById(R.id.setup_profile_submitBtn);
        BTN_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(String tag : selectedItems)
                {
                    //addUserToArrayMap.put("storeTags", FieldValue.arrayUnion(tag));
                    fireStore.collection("Users").document(user.getUid()).update("storeTags", FieldValue.arrayUnion(tag));
                }

                fireStore.collection("Users").document(user.getUid()).update("setup_profile", true);
                fireStore.collection("Users").document(user.getUid()).update("storeName", ET_storeName.getText().toString());
                fireStore.collection("Users").document(user.getUid()).update("storeUnit", ET_storeUnit.getText().toString());

                startActivity(new Intent(SetupProfileActivity.this, DashboardActivity.class));
                finish();
            }
        });
    }

    void CreateAlertDialog()
    {
        selectedItems = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(SetupProfileActivity.this);
        builder.setTitle("Select Tags");

        builder.setMultiChoiceItems(R.array.store_tags, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                String[] items = getResources().getStringArray(R.array.store_tags);

                if(isChecked)
                    selectedItems.add(items[which]);
                else if(selectedItems.contains(items[which]))
                    selectedItems.remove(items[which]);
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String final_selection = "";

                for(String item:selectedItems)
                {
                    final_selection = final_selection+"( "+item+" ) ";

                }

                TV_setup_profile_storeTags.setText(final_selection);
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
