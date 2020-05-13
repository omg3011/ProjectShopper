package Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.crosssellers.MainActivity;
import com.example.crosssellers.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    //-- Setup database
    private FirebaseAuth mAuth;

    //-- View component
    ImageSlider imageSlider;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //----------------------------------------------------------------------//
        // Cache Reference(s)                                                   //
        //----------------------------------------------------------------------//
        //-- Cache Database
        mAuth = FirebaseAuth.getInstance();

        //-- Init Views
        imageSlider = view.findViewById(R.id.home_IS);

        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel("https://cdn.shortpixel.ai/client/q_glossy,ret_img,w_1066/https://www.knetizen.com/wp-content/uploads/2019/07/BLACKPINK-Jennie-2.jpeg", "BlackPink1"));
        slideModels.add(new SlideModel("https://lh3.googleusercontent.com/4mflEEJlSkBungqNgt7Q6ir7vQjdP30_EqtA8JOGKsYL56z3Z53K0UoO_iAAnoLIa8Tpnwqe8sIKmSWXMW9ypO-JRZ4znDU1=w1600-rw", "BlackPink2"));
        slideModels.add(new SlideModel("https://lh3.googleusercontent.com/Dr6Smjtx7pEkYnroIl13L-Ezv2SWeEmfoTJbkxU9aQeaCsJx38Kb6sP0ded8aub8aRqozUZIPhjX1Yp8TQDrBYuc8Cw0n0jB_VQ=w1600-rw", "BlackPink3"));
        slideModels.add(new SlideModel("https://lh3.googleusercontent.com/WEg0z9f75hMaWY322TUi2REGviUS54iZBJJgblijZQY2PqmuKdPgUU4Jvw7t6fdjGIPsJiTXEfmvIS2S-fhgAuzyg-TeAO5P=w1600-rw", "BlackPink4"));

        imageSlider.setImageList(slideModels, true);

        return view;
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
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }
}