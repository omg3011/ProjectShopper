package com.example.crosssellers;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import Models.CPlatform_Model;
import Models.Notification_Model;

public class EditCPlatformPostActivity extends AppCompatActivity {

    //-- Private Variables
    CPlatform_Model postData;
    List<String> selectedItems;
    List<String> uploadsImageList;

    //-- Views
    TextView TV_idealPartner;
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
        setContentView(R.layout.activity_edit_cplatform_post);

        //-- Load data
        LoadData(savedInstanceState);

        //-- Init View
        TV_idealPartner = findViewById(R.id.edit_cplatform_create_ideal_partner_TV);
        BTN_choosePartner = findViewById(R.id.edit_cplatform_create_choose_partner_btn);
        BTN_upload_add = findViewById(R.id.edit_cplatform_create_add_photo_btn);
        BTN_upload_clear = findViewById(R.id.edit_cplatform_create_clear_photo_btn);
        BTN_submit = findViewById(R.id.edit_cplatform_apply_changes_btn);
        ET_description = findViewById(R.id.edit_cplatform_create_description_et);
        ET_title = findViewById(R.id.edit_cplatform_create_title_et);
        LL_uploads = findViewById(R.id.edit_cplatform_create_uploads_ll);

        dialog = new ProgressDialog(EditCPlatformPostActivity.this);
        dialog.setMessage("Loading your post...");
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

        //------------------------------------------------------------------------//
        // Update UI(s)
        //------------------------------------------------------------------------//
        //-- Set Title
        ET_title.setText(postData.getTitle());

        //-- Set Description
        ET_description.setText(postData.getDescription());

        //-- Set Tag
        String getTag = TextUtils.join(", ", postData.getCollabTag());
        TV_idealPartner.setText(getTag);
        selectedItems = postData.getCollabTag();

        //-- Set Upload Images
        uploadsImageList = new ArrayList<>();
        uploadsImageList = postData.getUploads();

        //-- For every uri string, create a ImageView and get it parent in LinearLayout
        for(String x : uploadsImageList)
        {
            // Convert String to Uri
            Uri myUri = Uri.parse(x);

            ImageView iv = new ImageView(EditCPlatformPostActivity.this);

            Picasso.get()
                    .load(myUri)
                    .placeholder(R.drawable.ic_add_image)
                    .error(R.drawable.ic_error)
                    .into(iv);

            addImageViewToLinearLayout(iv);
        }
        dialog.dismiss();
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
        Intent intent = new Intent(EditCPlatformPostActivity.this, ProfileActivity_CPlatformView.class);
        intent.putExtra("post", postData);
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

    void CreateAlertDialog_Tag()
    {
        selectedItems = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(EditCPlatformPostActivity.this);
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
        if(ActivityCompat.checkSelfPermission(EditCPlatformPostActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(EditCPlatformPostActivity.this,
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

    void addImageViewToLinearLayout(ImageView iv)
    {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(10, 0, 10, 0);
        iv.setLayoutParams(layoutParams);

        LL_uploads.addView(iv);
    }


    void CreateAlertDialog_Missing_Fields(String error)
    {
        // Alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(EditCPlatformPostActivity.this);

        // Set Title
        builder.setTitle("Missing Field(s)");
        builder.setMessage(error)
                .setNegativeButton(android.R.string.cancel, null);
        // Create and show dialog
        builder.create().show();
    }


    private void CreateAlertDialog_Submitted() {

        // Alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(EditCPlatformPostActivity.this);

        // Custom layout for alert dialog
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_alert_dialog_created_collab_post, null);


        builder.setView(view)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(EditCPlatformPostActivity.this, ProfileActivity_CPlatformView.class);
                        intent.putExtra("post", postData);
                        startActivity(intent);
                        finish();
                    }
                });
        // Create and show dialog
        builder.create().show();
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
                ImageView iv = new ImageView(EditCPlatformPostActivity.this);
                iv.setImageBitmap(b);
                addImageViewToLinearLayout(iv);
            }

        }

    }


    private void CreateAlertDialog_Submit() {

        // Alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(EditCPlatformPostActivity.this);

        // Set Title
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to submit?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SubmitEditCollaborationPost();
                    }
                })
                .setNegativeButton(android.R.string.no, null);
        // Create and show dialog
        builder.create().show();
    }


    void SubmitEditCollaborationPost()
    {
        //-- Init Dialog
        dialog.setMessage("Editing Collaboration Post...");
        dialog.show();
        //------------------------------------------------------------------------------------------------------------//
        // Upload the model into database first
        //------------------------------------------------------------------------------------------------------------//
        //-- Get Timestamp
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        final String timestampPost = simpleDateFormat.format(new Date());

        //-- Get Description
        final String description = ET_description.getText().toString();

        //-- Get Title
        final String title = ET_title.getText().toString();

        //-- Get tags
        List<String> tags = selectedItems;

        final HashMap<String, Object> cplatform_results = new HashMap<>();
        cplatform_results.put("title", title);
        cplatform_results.put("description", description);
        cplatform_results.put("tags", tags);

        //------------------------------------------------------------------------------------------------------------//
        // Upload the image into storage
        //------------------------------------------------------------------------------------------------------------//
        String filePathAndName;
        final List<String> uploadLinks = new ArrayList<>();
        for (int i = 0; i < LL_uploads.getChildCount(); ++i)
        {
            // Generate a unique file name
            filePathAndName=storagePath + postData.getCPost_uid() + Integer.toString(i);

            // Retrieve Uri
            ImageView iv = (ImageView) LL_uploads.getChildAt(i);
            // Get the data from an ImageView as bytes
            iv.setDrawingCacheEnabled(true);
            iv.buildDrawingCache();
            Bitmap bitmap = null;// = ((BitmapDrawable) iv.getDrawable()).getBitmap();

            try {
                bitmap = Bitmap.createBitmap(iv.getDrawable().getIntrinsicWidth(), iv.getDrawable().getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

                Canvas canvas = new Canvas(bitmap);
                iv.getDrawable().setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                iv.getDrawable().draw(canvas);

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
                            uploadLinks.add(photoUrl);

                            dataReference_CPlatform.document(postData.getCPost_uid()).update("uploads", FieldValue.arrayUnion(photoUrl));

                            // Reach last update, we create alert dialog that says submitted collaboration post.
                            if(finalI == LL_uploads.getChildCount()-1)
                            {

                                //-- Modify data => Query
                                dataReference_CPlatform.document(postData.getCPost_uid()).update(cplatform_results)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                dialog.dismiss();

                                                postData.setUploads(uploadLinks);
                                                postData.setTitle(title);
                                                postData.setDescription(description);
                                                postData.setCollabTag(selectedItems);

                                                Intent intent = new Intent(EditCPlatformPostActivity.this, ProfileActivity_CPlatformView.class);
                                                intent.putExtra("post", postData);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });

                                Notification_Model notification = new Notification_Model(timestampPost, user.getUid(), "You have edited a collaboration post. (" + title + ")");
                                dataReference_Notification.document().set(notification);
                            }
                        }
                    }
                });
            } catch (OutOfMemoryError e) {
                // Handle the error
            }
        }
    }
}
