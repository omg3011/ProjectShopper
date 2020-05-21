package com.example.crosssellers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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

    //-- Permission Constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    //-- Arrays Of Permissions to be requested
    String cameraPermissions[];
    String storagePermissions[];

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

        //-- Init arrays of permissions
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //----------------------------------------------------------------------//
        // Register Event Listener                                              //
        //----------------------------------------------------------------------//
        CV_profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAlertDialog_Photos();
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


    private void CreateAlertDialog_Photos() {

        String options[] = {"Camera", "Gallery"};

        // Alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(SetupProfileActivity.this);

        // Set Title
        builder.setTitle("Pick Image From");
        // Set Items to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle dialog item clicks

                //-- Camera Clicked
                if(which == 0)
                {
                    if(!checkCameraPermissions())
                        requestCameraPermissions();
                    else
                        pickFromCamera();
                }
                //-- Gallery Clicked
                else if(which == 1)
                {
                    if(!checkStoragePermissions())
                        requestStoragePermissions();
                    else
                        pickFromGallery();
                }
            }
        });

        // Create and show dialog
        builder.create().show();
    }


    //----------------------------------------------------------//
    //
    //  HANDLE PERMISSION
    //
    //----------------------------------------------------------//
    private boolean checkStoragePermissions()
    {
        // Check if storage permission is enabled or not
        //- return true if enabled, false if not enabled

        boolean result = ContextCompat.checkSelfPermission(SetupProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermissions(){
        // Request run-time storage permission
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }


    private boolean checkCameraPermissions()
    {
        // Check if storage permission is enabled or not
        //- return true if enabled, false if not enabled

        boolean result1 = ContextCompat.checkSelfPermission(SetupProfileActivity.this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result2 = ContextCompat.checkSelfPermission(SetupProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return (result1 && result2);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermissions(){
        // Request run-time storage permission
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }


    //----------------------------------------------------------//
    //
    //  HANDLE Sub Utility(s)
    //
    //----------------------------------------------------------//
    private void pickFromCamera() {
        // Intent of picking image from device camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

        // Put Image uri
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        // Intent to start camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }
    private void pickFromGallery() {
        // Pick from gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // This method is called when user press Allow or Deny from permission request dialog
        //-- Here we will handle permission cases (allow & deny)

        switch(requestCode)
        {
            case CAMERA_REQUEST_CODE:
            {
                // Picking from camera, first check if camera and storage permissions allowed or not
                if(grantResults.length > 0)
                {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    // Permission enabled
                    if(cameraAccepted && writeStorageAccepted)
                    {
                        pickFromCamera();
                    }
                    // Permission denied
                    else
                    {
                        Toast.makeText(SetupProfileActivity.this, "Please enable camera & storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:
            {
                // Picking from gallery, first check if cstorage permissions allowed or not
                if(grantResults.length > 0)
                {
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    // Permission enabled
                    if(writeStorageAccepted)
                    {
                        pickFromGallery();
                    }
                    // Permission denied
                    else
                    {
                        Toast.makeText(SetupProfileActivity.this, "Please enable storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // This method will be called after picking image from Camera/Gallery
        if(resultCode == RESULT_OK)
        {
            if(requestCode == IMAGE_PICK_GALLERY_CODE)
            {
                //Image is picked from gallery, get uri of image
                image_uri = data.getData();

                Picasso.get()
                        .load(image_uri)
                        .placeholder(R.drawable.ic_add_image)
                        .error(R.drawable.ic_error)
                        .into(CV_profileImage);

            }
            if(requestCode == IMAGE_PICK_CAMERA_CODE)
            {
                //Image is picked from camera, get uri of image
                Picasso.get()
                        .load(image_uri)
                        .placeholder(R.drawable.ic_add_image)
                        .error(R.drawable.ic_error)
                        .into(CV_profileImage);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
