package com.example.crosssellers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SetupProfileActivity extends AppCompatActivity {

    //-- Private variable(s)
    private String selectedItem;

    //-- Views
    Button BTN_select_tags, BTN_submit;
    TextView TV_setup_profile_storeTags;
    EditText ET_storeName, ET_storeUnit;
    CircularImageView CV_profileImage;

    //-- Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore fireStore;
    private FirebaseUser user;
    CollectionReference dataReference;

    //-- Firebase Storage
    StorageReference storageReference;
    String storagePath = "Users_Profile_Picture/";

    //-----------------------------------------------------------------//
    // Upload image / Camera
    //-----------------------------------------------------------------//
    //-- Uri of picked image
    Uri image_uri;

    //-- Progress Dialog
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);


        //-------------------------------------------------------------------------------
        // Handle Firebase
        //-------------------------------------------------------------------------------
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        fireStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        dataReference = fireStore.collection("Users");

        //----------------------------------------------------------------------//
        // Action bar                                                           //
        //----------------------------------------------------------------------//
        //-- Add built-in "Actionbar" and it's "Actionbar"->title
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Setup Store Profile");


        //----------------------------------------------------------------------//
        // Init Views                                                           //
        //----------------------------------------------------------------------//
        TV_setup_profile_storeTags = findViewById(R.id.setup_profile_storetags_TV);
        ET_storeName = findViewById(R.id.setup_profile_storeName_et);
        ET_storeUnit = findViewById(R.id.setup_profile_storeUnit_et);
        BTN_select_tags = findViewById(R.id.setup_profile_tag_button);
        BTN_submit = findViewById(R.id.setup_profile_submitBtn);
        CV_profileImage = findViewById(R.id.setup_profile_image_cv);

        //-- Init Progress dialog
        pd = new ProgressDialog(SetupProfileActivity.this);

        //----------------------------------------------------------------------//
        // Register Event Listener                                              //
        //----------------------------------------------------------------------//
        CV_profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Add_Photo_toUpload();
            }
        });
        BTN_select_tags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAlertDialog_Tags();
            }
        });
        BTN_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Show progress dialog
                pd.setTitle("Setting up Store Profile...");
                pd.show();

                fireStore.collection("Users").document(user.getUid()).update("storeTag", selectedItem);
                fireStore.collection("Users").document(user.getUid()).update("setup_profile", true);
                fireStore.collection("Users").document(user.getUid()).update("storeName", ET_storeName.getText().toString());
                fireStore.collection("Users").document(user.getUid()).update("storeUnit", ET_storeUnit.getText().toString());

                // Path and name of image to be stored in firebase storage
                //e.g. Users_Profile_Picture/e123456.jpg
                //e.g. Users_Profile_Picture/c123456.jpg
                String filePathAndName=storagePath + user.getUid();

                storageReference.child(filePathAndName).putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // image is uploaded to storage, now we get it's uri and store in user's database
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        final Uri downloadUri = uriTask.getResult();

                        // Check if image is uploaded ot not and uri is received
                        if(uriTask.isSuccessful())
                        {
                            //> Image uploaded
                            //> Add/Update url in user's database
                            HashMap<String, Object> results = new HashMap<>();
                            results.put("image", downloadUri.toString());

                            //-- Modify data => Query
                            dataReference.document(user.getUid()).update(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            pd.dismiss();
                                            startActivity(new Intent(SetupProfileActivity.this, DashboardActivity.class));
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SetupProfileActivity.this, "Error: Image not updated..", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SetupProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }


    //----------------------------------------------------------//
    //
    //  HANDLE Utility
    //
    //----------------------------------------------------------//
    void CreateAlertDialog_Tags()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(SetupProfileActivity.this);
        builder.setTitle("Select Tags");

        builder.setSingleChoiceItems(R.array.store_tags, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] items = getResources().getStringArray(R.array.store_tags);
                selectedItem = items[which];
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TV_setup_profile_storeTags.setText(selectedItem);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();
    }


    //-------------------------------------------//
    // Pick multi-images from gallery
    //-------------------------------------------//
    void Add_Photo_toUpload()
    {
        if(ActivityCompat.checkSelfPermission(SetupProfileActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(SetupProfileActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    100);
            return;
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK)
        {
            List<Bitmap> bitmaps = new ArrayList<>();
            ClipData clipData = data.getClipData();
            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); ++i) {
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    image_uri = imageUri;

                    try {
                        InputStream is = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        bitmaps.add(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Uri imageUri = data.getData();
                image_uri = imageUri;
                try {
                    InputStream is = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    bitmaps.add(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            //-- My code
            for(Bitmap b : bitmaps)
            {
                CV_profileImage.setImageBitmap(b);
            }

        }

    }





}
