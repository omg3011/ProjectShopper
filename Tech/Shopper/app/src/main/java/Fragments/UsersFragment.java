package Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.crosssellers.ChatActivity;
import com.example.crosssellers.DashboardActivity;
import com.example.crosssellers.MainActivity;
import com.example.crosssellers.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import Adapters.AdapterChat;
import Adapters.AdapterUsers;
import Models.User_Model;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment {

    //-- Setup database
    private FirebaseAuth mAuth;

    //-- View Cache
    RecyclerView recyclerView;

    //-- Adapter
    AdapterUsers adapterUsers;
    List<User_Model> userList;

    //-- Boolean
    boolean b_displayUser = false;

    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        //-- Init Views
        recyclerView = view.findViewById(R.id.users_RV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        //----------------------------------------------------------------------//
        // Cache Reference(s)                                                   //
        //----------------------------------------------------------------------//
        //-- Cache Database
        mAuth = FirebaseAuth.getInstance();


        //-- Get all users
        userList = new ArrayList<>();

        getAllUsers();

        return view;
    }

    private void getAllUsers() {
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

        // Get Path of database named "Users" containing user info
        CollectionReference dataReference = FirebaseFirestore.getInstance().collection("Users");


        // Get all data from path ^
        dataReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                // If got error, end
                if(e != null)
                    return;

                // Check until required info is received
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                    User_Model model = doc.getDocument().toObject(User_Model.class);

                    //-- Is message added / modified / removed?
                    switch(doc.getType()) {
                        case ADDED:
                            // Make sure string not empty, if not program crash
                            if (!TextUtils.isEmpty(model.getUid())) {
                                // Get all users except currently signed-in user
                                if (!model.getUid().equals(fUser.getUid())) {
                                    // Add message to list
                                    userList.add(model);

                                    //---------------------------------------------------------//
                                    // Update UI to display the changes in the list
                                    //---------------------------------------------------------//
                                    // Adapter
                                    adapterUsers = new AdapterUsers(getActivity(), userList);
                                    adapterUsers.notifyDataSetChanged();
                                    recyclerView.setAdapter(adapterUsers);
                                }
                            }

                            break;
                        case MODIFIED:
                            break;
                        case REMOVED:
                            // To be done later
                            break;
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
        inflater.inflate(R.menu.menu_main, menu);

        //-- SearchView: Handle Search User
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        b_displayUser = false;

        //-- Search Listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //-- Called when user press search button from keyboard
                //> If search query is not empty, then search
                if(!TextUtils.isEmpty(query.trim()))
                {
                    b_displayUser = true;
                    // Search text contain text, search it
                    searchUsers(query);
                }
                else
                {
                    if(b_displayUser == true)
                    {
                        b_displayUser = false;
                        userList.clear();
                        // Search text empty, get all users then
                        getAllUsers();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //> If search query is not empty, then search
                if(!TextUtils.isEmpty(newText.trim()))
                {
                    b_displayUser = true;
                    // Search text contain text, search it
                    searchUsers(newText);
                }
                else
                {
                    // Search text empty, get all users then
                    if(b_displayUser == true)
                    {
                        b_displayUser = false;
                        userList.clear();
                        // Search text empty, get all users then
                        getAllUsers();
                    }
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void searchUsers(final String query) {
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

        // Get Path of database named "Users" containing user info
        CollectionReference dataReference = FirebaseFirestore.getInstance().collection("Users");

        // Get all data from path ^
        dataReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                userList.clear();
                for (DocumentSnapshot doc : queryDocumentSnapshots) {

                    User_Model model = doc.toObject(User_Model.class);

                    // Get all users except currently signed-in user
                    if(!model.getUid().equals(fUser.getUid()))
                    {
                        /* Condition to fulfil search
                        *  1) User not current user
                        *  2) The name / email contain texts entered in SearchView (Case insensitive)
                        */
                        if(model.getName().toLowerCase().contains(query.toLowerCase()) ||
                                model.getEmail().toLowerCase().contains(query.toLowerCase())
                        )
                        {
                            userList.add(model);
                        }
                    }
                }


                // Adapter
                adapterUsers = new AdapterUsers(getActivity(), userList);
                // Refresh adapter
                adapterUsers.notifyDataSetChanged();
                // Set adapter to recycler view
                recyclerView.setAdapter(adapterUsers);
            }
        });
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
