package Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crosssellers.MainActivity;
import com.example.crosssellers.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class ProfileFragment extends Fragment {

    //-- Firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseFirestore fireStore;

    //-- Firebase Storage
    StorageReference storageReference;
    String storagePath = "Users_Profile_Cover_Imgs/";

    // Create a reference to the path of database named "Users" containing user info
    CollectionReference dataReference;


    //-- View from xml
    ImageView IV_avatar, IV_cover;
    TextView TV_name, TV_email, TV_phone;
    FloatingActionButton fab;

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

    //-- Uri of picked image
    Uri image_uri;

    //-- For Checking profile or cover photo
    String profileOrCoverPhoto;

    public ProfileFragment() {
        // Required empty public constructor
    }


    //----------------------------------------------------------//
    //
    //  Main Function(s)
    //
    //----------------------------------------------------------//
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //-- Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //-- Init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        fireStore = FirebaseFirestore.getInstance();
        dataReference = fireStore.collection("Users");
        storageReference = FirebaseStorage.getInstance().getReference();

        //-- Init Views
        IV_avatar = view.findViewById(R.id.profile_avatarIV);
        IV_cover = view.findViewById(R.id.profile_coverIV);
        TV_name = view.findViewById(R.id.profile_nameTV);
        TV_email = view.findViewById(R.id.profile_emailTV);
        TV_phone = view.findViewById(R.id.profile_phoneTV);
        fab = view.findViewById(R.id.fab);

        //-- Init Progress dialog
        pd = new ProgressDialog(getActivity());

        //-- Init arrays of permissions
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //-- This not real time, to optimize.
        Query query = dataReference.whereEqualTo("email", user.getEmail());
        query.get()
             .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String name = (String) document.get("name");
                                String email = (String) document.get("email");
                                String phone = (String) document.get("phone");
                                String imageURL = (String) document.get("image");
                                String coverImageURL = (String) document.get("coverImage");

                                //-- Set Data
                                TV_name.setText(name);
                                TV_email.setText(email);
                                TV_phone.setText(phone);


                                if(imageURL.isEmpty())
                                    imageURL = "Error";

                                if(coverImageURL.isEmpty())
                                    coverImageURL = "Error";

                                //-- Set Image
                                Picasso.get()
                                        .load(imageURL)
                                        .placeholder(R.drawable.ic_add_image)
                                        .error(R.drawable.ic_error)
                                        .into(IV_avatar);


                                Picasso.get()
                                        .load(coverImageURL)
                                        .placeholder(R.drawable.ic_add_image)
                                        .error(R.drawable.ic_error)
                                        .into(IV_cover);

                                break;
                            }
                        } else {
                            Log.d("Test", "Error getting documents: ", task.getException());
                        }
                    }
                });

        // FAB Button Click
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });

        return view;
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
                        Toast.makeText(getActivity(), "Please enable camera & storage permission", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), "Please enable storage permission", Toast.LENGTH_SHORT).show();
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

                uploadProfileCoverPhoto(image_uri);
            }
            if(requestCode == IMAGE_PICK_CAMERA_CODE)
            {
                //Image is picked from camera, get uri of image

                uploadProfileCoverPhoto(image_uri);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
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
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

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

    private void updateUI(String key, String value)
    {
        if(key == "name")
            TV_name.setText(value);
        else if(key == "email")
            TV_email.setText(value);
        else if(key == "phone")
            TV_phone.setText(value);
        else if(key == "image")
            Picasso.get()
                    .load(value)
                    .placeholder(R.drawable.ic_add_image)
                    .error(R.drawable.ic_error)
                    .into(IV_avatar);
        else if(key == "coverImage")
            Picasso.get()
                    .load(value)
                    .placeholder(R.drawable.ic_add_image)
                    .error(R.drawable.ic_error)
                    .into(IV_cover);
    }

    private void uploadProfileCoverPhoto(Uri uri) {
        /* To add check, i'll add a string variable and assign it value "image" when
         * user clicks "Edit Profile Picture", and assign it value "cover" when user
         * clicks "Edit Cover Photo".
         *
         * Here: - image is the key in each user containing url of user's profile picture
         *       - cover is the key in each user containing url of user's cover photo
         */

        //----------------------------------------------------

        /* The parameter "uri" contains the uri of image picked either from camera/gallery
         * We will use UID of the current signed in user as name of the image, so there will be only one image profile
         * and one image for cover for each user
        */


        // Show progress dialog
        pd.show();

        // Path and name of image to be stored in firebase storage
        //e.g. Users_Profile_Cover_Imgs/image_e123456.jpg
        //e.g. Users_Profile_Cover_Imgs/cover_c123456.jpg
        String filePathAndName=storagePath+""+profileOrCoverPhoto+ "_" + user.getUid();

        StorageReference storageReference2nd = storageReference.child(filePathAndName);
        storageReference2nd.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                            results.put(profileOrCoverPhoto, downloadUri.toString());

                            //-- Modify data => Query
                            dataReference.document(user.getUid()).update(results)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd.dismiss();
                                    updateUI(profileOrCoverPhoto, downloadUri.toString());
                                    Toast.makeText(getActivity(), "Image Update.", Toast.LENGTH_SHORT).show();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(getActivity(), "Error: Image not updated..", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();

                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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

        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermissions(){
        // Request run-time storage permission
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }


    private boolean checkCameraPermissions()
    {
        // Check if storage permission is enabled or not
        //- return true if enabled, false if not enabled

        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result2 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return (result1 && result2);
    }

    private void requestCameraPermissions(){
        // Request run-time storage permission
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }




    //----------------------------------------------------------//
    //
    //  HANDLE Utility
    //
    //----------------------------------------------------------//
    private void showEditProfileDialog() {
        // Show dialog containing options
        //- 1) Edit Profile Picture
        //- 2) Edit Cover Photo
        //- 3) Edit Name
        //- 4) Edit Phone

        String options[] = {"Edit Profile Picture", "Edit Cover Photo", "Edit Name", "Edit Phone"};

        // Alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set Title
        builder.setTitle("Choose Action");
        // Set Items to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle dialog item clicks

                //-- Edit Profile Clicked
                if(which == 0)
                {
                    pd.setMessage("Updating Profile Picture...");
                    profileOrCoverPhoto = "image"; // Changing profile picture, make sure to assign same value
                    showImagePicDialog();
                }
                //-- Edit Cover Clicked
                else if(which == 1)
                {
                    pd.setMessage("Updating Cover Photo...");
                    profileOrCoverPhoto = "coverImage"; // Changing profile picture, make sure to assign same value
                    showImagePicDialog();
                }
                //-- Edit Name Clicked
                else if(which == 2)
                {
                    pd.setMessage("Updating Name...");

                    // Call method and use "name" for key to update value in database
                    showNamePhoneUpdateDialog("name");
                }
                //-- Edit Phone Clicked
                else if(which == 3)
                {
                    pd.setMessage("Updating Phone...");

                    // Call method and use "phone" for key to update value in database
                    showNamePhoneUpdateDialog("phone");
                }
            }
        });

        // Create and show dialog
        builder.create().show();
    }

    private void showNamePhoneUpdateDialog(final String key) {
        /* Parameter "key"
        *  "name" use as key to update user's name in database
        *  "phone" use as key to update user's phone number in database
        */

        // Custom Dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update " + key); // Update name or Update phone

        // Set layout of dialog
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10, 10, 10, 10);

        // Add edit text
        final EditText editText = new EditText(getActivity());
        editText.setHint("Enter " + key); // Edit name or Edit phone
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        // Add buttons in dialog to "update"
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String value = editText.getText().toString().trim();

                // Validate if user has entered something or not
                if(!TextUtils.isEmpty(value))
                {
                    pd.show();
                    HashMap<String, Object>results = new HashMap<>();
                    results.put(key, value);

                    //-- Modify data => Query
                    dataReference.document(user.getUid()).update(results)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd.dismiss();
                                    updateUI(key, value);
                                    Toast.makeText(getActivity(), "Updated " + key + ".", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(getActivity(), "Error: " + key + " failed to update.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else
                {
                    Toast.makeText(getActivity(), "Please enter "+key, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Add buttons in dialog to "cancel"
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Create and show dialog
        builder.create().show();
    }

    private void showImagePicDialog() {

        String options[] = {"Camera", "Gallery"};

        // Alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set Title
        builder.setTitle("Pick Image From");
        // Set Items to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        // To show menu option in fragment
        setHasOptionsMenu(true);


        super.onCreate(savedInstanceState);
    }

    //--------------------------------------------------------//
    // Inflate option menu
    //--------------------------------------------------------//
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //--------------------------------------------------------//
    // Handle menu item click event
    //--------------------------------------------------------//
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
            GoogleSignInClient googleSignInClient= GoogleSignIn.getClient(getActivity(),gso);

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


    private void checkUserStatus()
    {
        //-- Get Current User
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //-- User is signed in.
        if(user != null)
        {
        }

        //-- User not signed in
        else
        {
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }
}
