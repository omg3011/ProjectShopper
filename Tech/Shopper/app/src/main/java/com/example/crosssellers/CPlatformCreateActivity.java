package com.example.crosssellers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import Models.CPlatform_Model;
import Models.Notification_Model;
import Models.User_Model;

public class CPlatformCreateActivity extends AppCompatActivity {

    //-- Private Variables
    List<String> selectedItems;

    //-- Views
    TextView TV_idealPartner, TV_storeName, TV_storeUnit, TV_mallName, TV_tag;
    Button BTN_choosePartner, BTN_upload_add, BTN_upload_clear, BTN_submit;
    LinearLayout LL_uploads;
    EditText ET_description, ET_title;

    //-- Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore fireStore;
    CollectionReference dataReference_CPlatform;
    CollectionReference dataReference_Notification;
    CollectionReference dataReference_User;

    //-- Progress Dialog
    ProgressDialog dialog;

    //-- Firebase Storage
    StorageReference storageReference;
    String storagePath = "Users_CPlatform_Uploads/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cplatform_create);

        //-- Init View
        TV_idealPartner = findViewById(R.id.cplatform_create_ideal_partner_TV);
        BTN_choosePartner = findViewById(R.id.cplatform_create_choose_partner_btn);
        BTN_upload_add = findViewById(R.id.cplatform_create_add_photo_btn);
        BTN_upload_clear = findViewById(R.id.cplatform_create_clear_photo_btn);
        BTN_submit = findViewById(R.id.cplatform_create_submit_btn);
        ET_description = findViewById(R.id.cplatform_create_description_et);
        ET_title = findViewById(R.id.cplatform_create_title_et);
        LL_uploads = findViewById(R.id.cplatform_create_uploads_ll);
        TV_storeName = findViewById(R.id.cplatform_create_store_name_TV);
        TV_mallName = findViewById(R.id.cplatform_create_mall_name_TV);
        TV_storeUnit = findViewById(R.id.cplatform_create_store_unit_TV);
        TV_tag = findViewById(R.id.cplatform_create_tag_name_TV);

        dialog = new ProgressDialog(CPlatformCreateActivity.this);
        dialog.setMessage("Loading your store profile...");
        dialog.show();

        //-------------------------------------------------------------------------------
        // Handle Firebase
        //-------------------------------------------------------------------------------
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        fireStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        dataReference_CPlatform = fireStore.collection("CPlatform");
        dataReference_Notification = fireStore.collection("Notifications");
        dataReference_User = fireStore.collection("Users");

        DocumentReference doc = dataReference_User.document(user.getUid());
        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
           @Override
           public void onSuccess(DocumentSnapshot documentSnapshot) {
               //--------------------------------------------------------------------------------//
               // (1) Get this store details
               //--------------------------------------------------------------------------------//
               TV_storeName.setText(documentSnapshot.getString("storeName"));
               TV_mallName.setText(documentSnapshot.getString("mallName"));
               TV_storeUnit.setText(documentSnapshot.getString("storeUnit"));
               TV_tag.setText(documentSnapshot.getString("storeTag"));
               dialog.dismiss();
        }
       }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CPlatformCreateActivity.this, "Failed to get store profile...", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        BTN_upload_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Add_Photo_toUpload();
            }
        });

        BTN_upload_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Clear_UploadPhotos();
            }
        });

        BTN_choosePartner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAlertDialog_Tag();
            }
        });

        BTN_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Check_Validate_Required_Field_Not_Empty())
                    CreateAlertDialog_Submit();
            }
        });

        //----------------------------------------------------------------------//
        // Action bar                                                           //
        //----------------------------------------------------------------------//
        //-- Add built-in "Actionbar" and it's "Actionbar"->title
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Create a Collaboration");

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
        Intent intent = new Intent(CPlatformCreateActivity.this, CPlatformHomeActivity.class);
        startActivity(intent);
        finish();
    }


    void CreateAlertDialog_Tag()
    {
        selectedItems = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(CPlatformCreateActivity.this);
        builder.setTitle("Select Ideal Partners");

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

                TV_idealPartner.setText(final_selection);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();
    }

    boolean Check_Validate_Required_Field_Not_Empty()
    {
        boolean pass = true;
        String error = "";

        // Check Title
        String checkTitle = ET_title.getText().toString();
        if(TextUtils.isEmpty(checkTitle)) {
            ET_title.setError("Please input a title.");
            error += "Please input a title.\n";
            pass = false;
        }

        // Check Description
        String checkDescription = ET_description.getText().toString();
        if(TextUtils.isEmpty(checkDescription)) {
            ET_description.setError("Please input a description.");
            error += "Please input a description.\n";
            pass = false;
        }

        // Check Tag
        if(selectedItems == null || selectedItems.size() == 0) {
            TV_idealPartner.setError("Please choose a tag.");
            error += "Please choose a tag.\n";
            pass = false;
        }

        // Check Image
        if(LL_uploads.getChildCount() == 0)
        {
            error += "Please upload a photo to continue.\n";
            pass = false;
        }

        if(pass == false)
            CreateAlertDialog_Missing_Fields(error);

        return pass;
    }

    //-------------------------------------------//
    // Pick multi-images from gallery
    //-------------------------------------------//
    void Add_Photo_toUpload()
    {
        if(ActivityCompat.checkSelfPermission(CPlatformCreateActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(CPlatformCreateActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    100);
            return;
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    void Clear_UploadPhotos()
    {
        if(((LinearLayout) LL_uploads).getChildCount() > 0)
            ((LinearLayout) LL_uploads).removeAllViews();
    }

    void addImageViewToLinearLayout(ImageView iv, int width, int height)
    {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(10, 0, 10, 0);
        iv.setLayoutParams(layoutParams);

        LL_uploads.addView(iv);
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
                ImageView iv = new ImageView(CPlatformCreateActivity.this);
                iv.setImageBitmap(b);
                addImageViewToLinearLayout(iv, 200, 100);
            }

        }

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void CreateAlertDialog_Submit() {

        // Alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(CPlatformCreateActivity.this);

        // Set Title
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to submit?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SubmitCollaborationPost();
                    }
                })
                .setNegativeButton(android.R.string.no, null);
        // Create and show dialog
        builder.create().show();
    }

    void CreateAlertDialog_Missing_Fields(String error)
    {
        // Alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(CPlatformCreateActivity.this);

        // Set Title
        builder.setTitle("Missing Field(s)");
        builder.setMessage(error)
                .setNegativeButton(android.R.string.cancel, null);
        // Create and show dialog
        builder.create().show();
    }

    private void CreateAlertDialog_Submitted() {

        // Alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(CPlatformCreateActivity.this);

        // Custom layout for alert dialog
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_alert_dialog_created_collab_post, null);


        builder.setView(view)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(CPlatformCreateActivity.this, CPlatformHomeActivity.class));
                        finish();
                    }
                });
        // Create and show dialog
        builder.create().show();
    }

    void SubmitCollaborationPost()
    {
        //-- Init Dialog
        dialog.setMessage("Creating Collaboration Post...");
        dialog.show();
        //------------------------------------------------------------------------------------------------------------//
        // Upload the model into database first
        //------------------------------------------------------------------------------------------------------------//
        //-- Get Date (Now)
        //String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        //-- Get Time (Now)
        //String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        //-- Get Timestamp
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        final String timestamp = simpleDateFormat.format(new Date());

        //-- Get Description
        final String description = ET_description.getText().toString();

        //-- Get Title
        final String title = ET_title.getText().toString();

        dataReference_User.document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User_Model uModel = documentSnapshot.toObject(User_Model.class);
                final String id = dataReference_CPlatform.document().getId();
                final CPlatform_Model collab = new CPlatform_Model(id, timestamp, null, user.getUid(), selectedItems, title, description, 0, false, uModel.getMallName());
                dataReference_CPlatform.document(id).set(collab);


                final String notify_id = dataReference_Notification.document().getId();
                Notification_Model notification = new Notification_Model(timestamp, user.getUid(), "You have posted a collaboration " + title + ".", notify_id);
                dataReference_Notification.document(notify_id).set(notification);

                //------------------------------------------------------------------------------------------------------------//
                // Upload the image into storage
                //------------------------------------------------------------------------------------------------------------//
                String filePathAndName;
                for (int i = 0; i < LL_uploads.getChildCount(); ++i) {
                    // Generate a unique file name
                    filePathAndName=storagePath + id + Integer.toString(i);

                    // Retrieve Uri
                    ImageView iv = (ImageView) LL_uploads.getChildAt(i);
                    // Get the data from an ImageView as bytes
                    iv.setDrawingCacheEnabled(true);
                    iv.buildDrawingCache();
                    Bitmap bitmap = ((BitmapDrawable) iv.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    final int finalI = i;
                    UploadTask uploadTask = storageReference.child(filePathAndName).putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure( Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            final Uri downloadUri = uriTask.getResult();

                            // Check if image is uploaded ot not and uri is received
                            if (uriTask.isSuccessful())
                            {
                                String photoUrl = downloadUri.toString();

                                dataReference_CPlatform.document(id).update("uploads", FieldValue.arrayUnion(photoUrl));

                                // Reach last update, we create alert dialog that says submitted collaboration post.
                                if(finalI == LL_uploads.getChildCount()-1)
                                {
                                    CreateAlertDialog_Submitted();
                                    dialog.dismiss();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

}
