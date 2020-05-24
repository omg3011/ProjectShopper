package Fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.crosssellers.MainActivity;
import com.example.crosssellers.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Adapters.AdapterNotifications;
import Models.CPlatform_Model;
import Models.Notification_Model;

public class NotificationFragment extends Fragment  {

    //-- Setup database
    private FirebaseAuth mAuth;
    FirebaseUser fUser;
    CollectionReference dataReference_Notification;
    CollectionReference dataReference_User;


    RecyclerView RV_notificationPost;

    //-- Private variable(s)
    AdapterNotifications adapterNotificationPost;
    List<Notification_Model> notificationPostList;
    LinearLayoutManager manager;


    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        //-- Init Views
        RV_notificationPost = view.findViewById(R.id.notification_rv);


        //----------------------------------------------------------------------//
        // Cache Reference(s)                                                   //
        //----------------------------------------------------------------------//
        //-- Cache Database
        mAuth = FirebaseAuth.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        dataReference_Notification = FirebaseFirestore.getInstance().collection("Notifications");
        dataReference_User = FirebaseFirestore.getInstance().collection("Users");

        //----------------------------------------------------------------------//
        // Init RecyclerView List                                               //
        //----------------------------------------------------------------------//
        // Adapter
        notificationPostList = new ArrayList<>();
        adapterNotificationPost = new AdapterNotifications(getContext(), notificationPostList);
        manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        RV_notificationPost.setLayoutManager(manager);
        RV_notificationPost.setAdapter(adapterNotificationPost);


        //-- Retrieve data from database
        getNotificationPost();

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
    public boolean onOptionsItemSelected(MenuItem item) {
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



    void getNotificationPost()
    {
        dataReference_Notification.addSnapshotListener(new EventListener<QuerySnapshot>()
        {
           @RequiresApi(api = Build.VERSION_CODES.N)
           @Override
           public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
               // If got error, end
               if (e != null)
                   return;

               // Check until required info is received
               for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                   Notification_Model model = doc.getDocument().toObject(Notification_Model.class);

                   // We only want message targeted to me
                   if(!model.getUid().equals(fUser.getUid()))
                       continue;

                   //-- Is message added / modified / removed?
                   switch (doc.getType()) {
                       case ADDED:
                           notificationPostList.add(model);

                           notificationPostList.sort(new Comparator<Notification_Model>() {
                               @Override
                               public int compare(Notification_Model o1, Notification_Model o2) {
                                   //-- Get Timestamp
                                   Date date1 = null;
                                   Date date2 = null;
                                   try {
                                       date1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).parse(o1.getTimestamp());
                                   } catch (ParseException ex) {
                                       ex.printStackTrace();
                                   }
                                   try {
                                       date2 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).parse(o2.getTimestamp());
                                   } catch (ParseException ex) {
                                       ex.printStackTrace();
                                   }

                                   return date2.compareTo(date1);
                               }
                           });

                           adapterNotificationPost.notifyDataSetChanged();

                           break;
                       case MODIFIED:
                           break;
                       case REMOVED:
                           // To be done later
                           break;
                       default:
                           throw new IllegalStateException("Unexpected value: " + doc.getType());
                   }
               }
           }
        });
    }
}
