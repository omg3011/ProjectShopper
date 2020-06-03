package Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.crosssellers.EditProfileActivity;
import com.example.crosssellers.R;
import com.example.crosssellers.SelectMallActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

import Adapters.ViewPagerAdapter;
import Models.User_Model;

public class ProfileFragment extends Fragment {

    // Cache
    TabLayout tabLayout;
    ViewPager viewPager;
    View view;
    TextView TV_storeName, TV_mallName, TV_storeUnit, TV_storeTag, TV_ratingQuantity, TV_ratingValue;
    RatingBar RB_rating;
    ImageView IV_storeImage;
    ImageView BTN_editProfile;

    //-- Setup database
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore fireStore;

    //-- Progress Dialog
    ProgressDialog pd;
    ViewPagerAdapter adapter;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // To show menu option in fragment
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        //----------------------------------------------------------------------//
        // Cache Reference(s)                                                   //
        //----------------------------------------------------------------------//
        //-- Cache Database
        mAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

        //-- Cache views
        tabLayout = view.findViewById(R.id.profile3_tablayout);
        viewPager = view.findViewById(R.id.profile3_viewPager);

        TV_storeName = view.findViewById(R.id.profile3_store_name_TV);
        TV_mallName = view.findViewById(R.id.profile3_mall_name_TV);
        TV_storeUnit = view.findViewById(R.id.profile3_storeUnit_TV);
        TV_ratingQuantity = view.findViewById(R.id.profile3_ratingQuantity_TV);
        TV_storeTag = view.findViewById(R.id.profile3_tags_TV);
        TV_ratingValue = view.findViewById(R.id.profile3_ratingValue_TV);
        RB_rating = view.findViewById(R.id.profile3_rating_RB);
        IV_storeImage = view.findViewById(R.id.profile3_store_image_IV);

        BTN_editProfile = view.findViewById(R.id.profile3_store_edit_BTN);

        BTN_editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), EditProfileActivity.class));
                getActivity().finish();
            }
        });

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading store profile...");
        pd.show();

        //-- Retrieve data from database
        DocumentReference doc = fireStore.collection("Users").document(user.getUid());
        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final User_Model myuser = documentSnapshot.toObject(User_Model.class);
                TV_storeName.setText(documentSnapshot.getString("storeName"));
                TV_mallName.setText(documentSnapshot.getString("mallName"));
                TV_storeUnit.setText(documentSnapshot.getString("storeUnit"));
                TV_storeTag.setText(documentSnapshot.getString("storeTag"));

                String imageUri = documentSnapshot.getString("image");
                if(imageUri.isEmpty())
                    imageUri = "Empty";

                Picasso.get()
                        .load(imageUri)
                        .placeholder(R.drawable.ic_add_image)
                        .error(R.drawable.ic_error)
                        .into(IV_storeImage);


                if(myuser.getRatingList() == null || myuser.getRatingList().size() <= 0)
                {
                    RB_rating.setRating(0);
                    TV_ratingQuantity.setText("(0)");
                    TV_ratingValue.setText("0.0");
                }
                else
                {
                    RB_rating.setRating(GetRatingFromList(myuser.getRatingList()));
                    TV_ratingQuantity.setText("("+Integer.toString(myuser.getRatingList().size()) + ")");
                    TV_ratingValue.setText(Float.toString(RB_rating.getRating()));
                }
                pd.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Test", "Failed to get " + e.getMessage());
                pd.dismiss();
            }
        });

        adapter = new ViewPagerAdapter(getChildFragmentManager());

        // Adding fragments
        adapter.AddFragment(new CollaborationFragment(), "Collaborations");
        adapter.AddFragment(new PromotionFragment(), "Promotions");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }


    Float GetRatingFromList(List<Double> ratings)
    {
        String rateString = "";
        double rateValue = 0.0f;

        for(Double x : ratings)
        {
            rateValue += x;
        }

        rateValue /= ratings.size();

        return (float)rateValue;
    }

    //--------------------------------------------------------//
    // Inflate option menu
    //--------------------------------------------------------//
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_logout_only, menu);
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
            mAuth.signOut();

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
        FirebaseUser user = mAuth.getCurrentUser();

        //-- User is signed in.
        if(user != null)
        {
        }

        //-- User not signed in
        else
        {
            startActivity(new Intent(getActivity(), SelectMallActivity.class));
            getActivity().finish();
        }
    }
}
