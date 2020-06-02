package Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.crosssellers.CPlatformHomeActivity;
import com.example.crosssellers.CPlatformViewActivity;
import com.example.crosssellers.CPromotionHomeActivity;
import com.example.crosssellers.MainActivity;
import com.example.crosssellers.MallInsightActivity_Home;
import com.example.crosssellers.R;
import com.example.crosssellers.ShopInsightActivity_Home;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import Models.CPlatform_Model;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    //-- Setup database
    private FirebaseAuth mAuth;
    FirebaseUser fUser;
    CollectionReference dataReference_CPlatform;
    CollectionReference dataReference_User;
    List<CPlatform_Model> modelList = new ArrayList<>();

    //-- View component
    ImageSlider imageSlider;
    GridLayout gridLayout;

    int numOfFeature = 4;

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
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        dataReference_CPlatform = FirebaseFirestore.getInstance().collection("CPlatform");
        dataReference_User = FirebaseFirestore.getInstance().collection("Users");

        //-- Init Views
        imageSlider = view.findViewById(R.id.home_IS);

        //-- Init View
        gridLayout = view.findViewById(R.id.home_GL);

        //-- Setup EventListener for clicking each element in the scrolling List
        for(int i = 0; i < gridLayout.getChildCount(); ++i)
        {
            final CardView cardView = (CardView) gridLayout.getChildAt(i);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout layout = (LinearLayout) cardView.getChildAt(0);
                    TextView TV_mall = (TextView) layout.getChildAt(1);

                    if(TV_mall.getText().equals("Shop Insights"))
                    {
                        Intent intent = new Intent(getActivity(), ShopInsightActivity_Home.class);
                        intent.putExtra("mall", TV_mall.getText());
                        startActivity(intent);
                        getActivity().finish();
                    }
                    else if(TV_mall.getText().equals("Mall Insights"))
                    {
                        Intent intent = new Intent(getActivity(), MallInsightActivity_Home.class);
                        intent.putExtra("mall", TV_mall.getText());
                        startActivity(intent);
                        getActivity().finish();
                    }
                    else if(TV_mall.getText().equals("Collaborations"))
                    {
                        Intent intent = new Intent(getActivity(), CPlatformHomeActivity.class);
                        intent.putExtra("mall", TV_mall.getText());
                        startActivity(intent);
                        getActivity().finish();
                    }
                    else if(TV_mall.getText().equals("Promotions"))
                    {
                        Intent intent = new Intent(getActivity(), CPromotionHomeActivity.class);
                        intent.putExtra("mall", TV_mall.getText());
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
            });
        }

        //-- Setup ImageSlider
        final List<SlideModel> slideModels = new ArrayList<>();
        dataReference_CPlatform.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(int i = 0; i < queryDocumentSnapshots.getDocuments().size(); ++i)
                {
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(i);
                    CPlatform_Model model = documentSnapshot.toObject(CPlatform_Model.class);

                    if(!model.isCollab_closed_flag())
                    {
                        // Add Data
                        slideModels.add(new SlideModel(model.getUploads().get(0), model.getTitle()));
                        modelList.add(model);

                        // Countdown
                        numOfFeature--;
                    }

                    if(i == queryDocumentSnapshots.getDocuments().size()-1 || numOfFeature <= 0)
                    {
                        imageSlider.setImageList(slideModels, true);
                        imageSlider.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onItemSelected(int i) {
                                Intent intent = new Intent(getContext(), CPlatformViewActivity.class);
                                intent.putExtra("post", modelList.get(i));
                                getContext().startActivity(intent);
                            }
                        });

                        break;
                    }
                }

                if(queryDocumentSnapshots.getDocuments().size() == 0)
                {
                    slideModels.add(new SlideModel("https://www.google.com/url?sa=i&url=https%3A%2F%2Fmedium.ip.sx%2Fwere-thrilled-to-welcome-our-new-software-developer-calin-d06057dd2fc&psig=AOvVaw3fPVlYZHW7bs_C_0yO_8io&ust=1591176825421000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCOCT9N_p4ukCFQAAAAAdAAAAABAD", "Welcome!"));
                    imageSlider.setImageList(slideModels, true);

                }
            }
        });

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
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }
}
