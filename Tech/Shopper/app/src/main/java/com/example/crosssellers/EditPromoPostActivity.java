package com.example.crosssellers;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import Models.CPromotion_Model;
import Models.Notification_Model;

public class EditPromoPostActivity extends AppCompatActivity {

    //-- Get saved data
    CPromotion_Model postData;

    //-- Private Variable(s)
    List<String> uploadsImageList;
    List<String> selectedItems;

    //-- View(s)
    Button BTN_chooseTags, BTN_upload_add, BTN_upload_clear, BTN_apply_changes;
    EditText ET_promoStart, ET_promoEnd;
    LinearLayout LL_uploads;
    EditText ET_description, ET_title;
    TextView TV_idealTags;

    //-- Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore fireStore;
    CollectionReference dataReference_Notification;
    CollectionReference dataReference_CPromotion;
    CollectionReference dataReference_User;

    //-- Private
    DatePickerDialog pickerStartPromo, pickerEndPromo;

    //-- Progress Dialog
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_promo_post);
/*
        //-- Load Data
        LoadData(savedInstanceState);


        //------------------------------------------------------------------------//
        // Init View(s)
        //------------------------------------------------------------------------//
        //-- Init View
        BTN_chooseTags = findViewById(R.id.edit_cpromo_create_choose_tag_btn);
        BTN_upload_add = findViewById(R.id.edit_cpromo_create_add_photo_btn);
        BTN_upload_clear = findViewById(R.id.edit_cpromo_create_clear_photo_btn);
        BTN_apply_changes = findViewById(R.id.edit_cplatform_apply_changes_btn);
        ET_description = findViewById(R.id.edit_cpromo_create_description_et);
        ET_title = findViewById(R.id.edit_cpromo_create_title_et);
        ET_promoStart = findViewById(R.id.edit_cpromo_create_startDate_ET);
        ET_promoEnd = findViewById(R.id.edit_cpromo_create_endDate_ET);
        LL_uploads = findViewById(R.id.edit_cpromo_create_uploads_ll);
        TV_idealTags = findViewById(R.id.edit_cpromo_ideal_tags_TV);

        dialog = new ProgressDialog(EditPromoPostActivity.this);
        dialog.setMessage("Loading your post...");
        dialog.show();


        //------------------------------------------------------------------------//
        // Update UI(s)
        //------------------------------------------------------------------------//
        //-- Set Title
        ET_title.setText(postData.getTitle());

        //-- Set Description
        ET_description.setText(postData.getDescription());

        //-- Set Promo Start Date
        ET_promoStart.setText(postData.getTimestampStart());

        //-- Set Promo End Date
        ET_promoEnd.setText(postData.getTimestampEnd());

        //-- Set Tag
        String getTag = TextUtils.join(", ", postData.getTags());
        TV_idealTags.setText(getTag);

        //-- Set Upload Images
        uploadsImageList = new ArrayList<>();
        uploadsImageList = postData.getUploads();

        //-- For every uri string, create a ImageView and get it parent in LinearLayout
        for(String x : uploadsImageList)
        {
            // Convert String to Uri
            Uri myUri = Uri.parse(x);

            ImageView iv = new ImageView(EditPromoPostActivity.this);

            Picasso.get()
                    .load(myUri)
                    .placeholder(R.drawable.ic_add_image)
                    .error(R.drawable.ic_error)
                    .into(iv);

            addImageViewToLinearLayout(iv);
        }

        //------------------------------------------------------------------------//
        // Click Listener(s)
        //------------------------------------------------------------------------//
        BTN_apply_changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Check_Validate_Required_Field_Not_Empty())
                    CreateAlertDialog_Submit();
            }
        });

        BTN_chooseTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAlertDialog_Tag();
            }
        });
    }

    void LoadData(final Bundle savedInstanceState){
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                postData= null;
            } else {
                postData= (CPromotion_Model) extras.getSerializable("post");
            }
        } else {
            postData= (CPromotion_Model) savedInstanceState.getSerializable("post");
        }
    }


    private void CreateAlertDialog_Submit() {

        // Alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(EditPromoPostActivity.this);

        // Set Title
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to submit?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SubmitEditPromoPost();
                    }
                })
                .setNegativeButton(android.R.string.no, null);
        // Create and show dialog
        builder.create().show();
    }


    void SubmitEditPromoPost()
    {
        //-- Init Dialog
        dialog.setMessage("Editing Promotion Post...");
        dialog.show();

        //------------------------------------------------------------------------------------------------------------//
        // Upload the model into database first
        //------------------------------------------------------------------------------------------------------------//
        //-- Get Date (Now)
        //String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        //-- Get Time (Now)
        //String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        //-- Get current Timestamp
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        String timestampPost = simpleDateFormat.format(new Date());

        //-- Get Promo start Timestamp
        String timestampStart = ET_promoStart.getText().toString();

        //-- Get Promo end Timestamp
        String timestampEnd = ET_promoEnd.getText().toString();

        //-- Get Description
        String description = ET_description.getText().toString();

        //-- Get Title
        String title = ET_title.getText().toString();

        //-- Get tags
        List<String> tags = selectedItems;

        //-- Get Poster uid
        String posterUID = user.getUid();

        String duration = "";

        HashMap<String, Object> promo_results = new HashMap<>();
        promo_results.put("title", title);
        promo_results.put("description", description);
        promo_results.put("timestampPost", timestampPost);
        promo_results.put("timestampEnd", timestampEnd);
        promo_results.put("uploads", timestampEnd);
        promo_results.put("tags", tags);

        //-- Modify data => Query
        dataReference_CPromotion.document(postData.getPromotionPost_uid()).update(promo_results)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });

        Notification_Model notification = new Notification_Model(timestampPost, user.getUid(), "You have edited promotion. (" + title + ")");
        dataReference_Notification.document().set(notification);

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

                        dataReference_CPromotion.document(id).update("uploads", FieldValue.arrayUnion(photoUrl));

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

*/
    }
/*
    void addImageViewToLinearLayout(ImageView iv)
    {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 10, 10);
        iv.setLayoutParams(layoutParams);

        LL_uploads.addView(iv);
    }


    boolean Check_Validate_Required_Field_Not_Empty()
    {
        boolean pass = true;
        String error = "";

        // Check Start Time
        String checkTimeStart = ET_promoStart.getText().toString();
        if(TextUtils.isEmpty(checkTimeStart)) {
            ET_title.setError("Please input a start date.");
            error += "Please input a start date.\n";
            pass = false;
        }

        // Check End time
        String checkTimeEnd = ET_promoEnd.getText().toString();
        if(TextUtils.isEmpty(checkTimeEnd)) {
            ET_title.setError("Please input a end date.");
            error += "Please input a end date.\n";
            pass = false;
        }

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
            TV_idealTags.setError("Please choose a tag.");
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


    void CreateAlertDialog_Tag()
    {
        selectedItems = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(EditPromoPostActivity.this);
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

                TV_idealTags.setText(final_selection);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();
    }


    void CreateAlertDialog_Missing_Fields(String error)
    {
        // Alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(EditPromoPostActivity.this);

        // Set Title
        builder.setTitle("Missing Field(s)");
        builder.setMessage(error)
                .setNegativeButton(android.R.string.cancel, null);
        // Create and show dialog
        builder.create().show();
    }
    */
}
