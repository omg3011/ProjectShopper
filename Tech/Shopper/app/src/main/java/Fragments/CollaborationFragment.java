package Fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.crosssellers.R;
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

import Adapters.AdapterCollabPost_CPlatform;
import Adapters.AdapterCollabPost_Profile;
import Models.CPlatform_Model;

public class CollaborationFragment extends Fragment {

    //-- DB(s)
    FirebaseUser fUser;
    CollectionReference dataReference_CPlatform;
    CollectionReference dataReference_User;
    CollectionReference dataReference_Request;

    //-- View(s)
    RecyclerView RV_list;
    View view;

    //-- Private variable(s)
    AdapterCollabPost_Profile adapterCollabPost;
    List<CPlatform_Model> collabPostList;
    GridLayoutManager manager;

    public CollaborationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_collaboration, container, false);


        //-- Cache Reference
        RV_list = view.findViewById(R.id.fragment_collab_rv);

        //-- Init DB
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        dataReference_CPlatform = FirebaseFirestore.getInstance().collection("CPlatform");
        dataReference_User = FirebaseFirestore.getInstance().collection("Users");
        dataReference_Request = FirebaseFirestore.getInstance().collection("RequestMailBox");


        //----------------------------------------------------------------------//
        // Init RecyclerView List                                               //
        //----------------------------------------------------------------------//
        // Adapter
        collabPostList = new ArrayList<>();
        adapterCollabPost = new AdapterCollabPost_Profile(getContext(), collabPostList, dataReference_Request);
        int numberOfColumns = 2;
        manager = new GridLayoutManager(getContext(), numberOfColumns);
        RV_list.setLayoutManager(manager);
        RV_list.setAdapter(adapterCollabPost);

        getCollabPostByMe();

        return view;

    }


    void getCollabPostByMe()
    {
        //--------------------------------------------------------------------------------//
        // (2) Get all the CPlatform Post
        //--------------------------------------------------------------------------------//
        // Get all data from path ^
        dataReference_CPlatform.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                // If got error, end
                if(e != null)
                    return;

                // Check until required info is received
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges())
                {
                    CPlatform_Model model = doc.getDocument().toObject(CPlatform_Model.class);

                    // Only show my post
                    if(!model.getPosterUid().equals(fUser.getUid()))
                    {
                        continue;
                    }

                    //-- Is message added / modified / removed?
                    switch(doc.getType())
                    {
                        case ADDED:
                            //---------------------------------------------------------//
                            // Update UI to display the changes in the list
                            //---------------------------------------------------------//
                            collabPostList.add(model);

                            collabPostList.sort(new Comparator<CPlatform_Model>()
                            {
                                @Override
                                public int compare(CPlatform_Model o1, CPlatform_Model o2) {
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

                            adapterCollabPost.notifyDataSetChanged();
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
