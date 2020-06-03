package Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.crosssellers.R;
import com.example.crosssellers.SelectMallActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import Adapters.AdapterUsers;
import Models.RequestMailBox_Model;
import Models.User_Model;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    //-- Setup database
    private FirebaseAuth mAuth;

    //-- View Cache
    RecyclerView recyclerView;

    //-- Adapter
    AdapterUsers adapterUsers;
    List<User_Model> userList;
    List<RequestMailBox_Model> requestList;

    //-- DB
    CollectionReference dataReference_RequestMailBox;
    CollectionReference dataReference_CPlatform;
    CollectionReference dataReference_User;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        //-- Init Views
        recyclerView = view.findViewById(R.id.users_RV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        //----------------------------------------------------------------------//
        // Cache Reference(s)                                                   //
        //----------------------------------------------------------------------//
        //-- Cache Database
        mAuth = FirebaseAuth.getInstance();
        dataReference_RequestMailBox = FirebaseFirestore.getInstance().collection("RequestMailBox");
        dataReference_CPlatform = FirebaseFirestore.getInstance().collection("CPlatform");
        dataReference_User = FirebaseFirestore.getInstance().collection("Users");


        //-- Get all users
        userList = new ArrayList<>();
        requestList = new ArrayList<>();

        // Adapter
        adapterUsers = new AdapterUsers(getActivity(), userList, dataReference_CPlatform, requestList, getActivity());
        adapterUsers.notifyDataSetChanged();
        recyclerView.setAdapter(adapterUsers);

        getAllPartners();
        //getAllUsers();

        return view;
    }

    private void getAllPartners()
    {
        dataReference_RequestMailBox.get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> myListOfDocuments = task.getResult().getDocuments();

                        for(DocumentSnapshot doc : myListOfDocuments)
                        {
                            final RequestMailBox_Model rModel = doc.toObject(RequestMailBox_Model.class);

                            if(rModel.getStatus().equals("accepted"))
                            {
                                // I am owner of cpost
                                if(rModel.getOwner_UID().equals(mAuth.getCurrentUser().getUid()))
                                {
                                    dataReference_User.document(rModel.getRequester_UID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            User_Model uModel = documentSnapshot.toObject(User_Model.class);

                                            requestList.add(rModel);
                                            userList.add(uModel);
                                            adapterUsers.notifyDataSetChanged();
                                        }
                                    });
                                }
                                // I am requester of cpost
                                else if(rModel.getRequester_UID().equals(mAuth.getCurrentUser().getUid()))
                                {
                                    dataReference_User.document(rModel.getOwner_UID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            User_Model uModel = documentSnapshot.toObject(User_Model.class);

                                            requestList.add(rModel);
                                            userList.add(uModel);
                                            adapterUsers.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            });
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
            startActivity(new Intent(getActivity(), SelectMallActivity.class));
            getActivity().finish();
        }
    }
}
