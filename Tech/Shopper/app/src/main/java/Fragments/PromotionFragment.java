package Fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.crosssellers.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
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

import Adapters.AdapterPromoPost_Profile;
import Models.CPlatform_Model;
import Models.CPromotion_Model;

public class PromotionFragment extends Fragment {

    //-- DB(s)
    FirebaseUser fUser;
    CollectionReference dataReference_Promo;
    CollectionReference dataReference_User;

    //-- View(s)
    RecyclerView RV_list;
    View view;

    //-- Private variable(s)
    AdapterPromoPost_Profile adapterPromoPost;
    List<CPromotion_Model> promoPostList;
    GridLayoutManager manager;

    public PromotionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_promotion, container, false);


        //-- Cache Reference
        RV_list = view.findViewById(R.id.fragment_promo_rv);

        //-- Init DB
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        dataReference_Promo = FirebaseFirestore.getInstance().collection("Promotions");
        dataReference_User = FirebaseFirestore.getInstance().collection("Users");


        //----------------------------------------------------------------------//
        // Init RecyclerView List                                               //
        //----------------------------------------------------------------------//
        // Adapter
        promoPostList = new ArrayList<>();
        adapterPromoPost = new AdapterPromoPost_Profile(getContext(), promoPostList);
        int numberOfColumns = 2;
        manager = new GridLayoutManager(getContext(), numberOfColumns);
        RV_list.setLayoutManager(manager);
        RV_list.setAdapter(adapterPromoPost);

        getPromoPostByMe();

        return view;
    }


    void getPromoPostByMe()
    {
        //--------------------------------------------------------------------------------//
        // (2) Get all the CPlatform Post
        //--------------------------------------------------------------------------------//
        // Get all data from path ^
        dataReference_Promo.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                // If got error, end
                if(e != null)
                    return;

                // Check until required info is received
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges())
                {
                    CPromotion_Model model = doc.getDocument().toObject(CPromotion_Model.class);

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
                            promoPostList.add(model);

                            promoPostList.sort(new Comparator<CPromotion_Model>()
                            {
                                @Override
                                public int compare(CPromotion_Model o1, CPromotion_Model o2) {
                                    //-- Get Timestamp
                                    Date date1 = null;
                                    Date date2 = null;
                                    try {
                                        date1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).parse(o1.getTimestampPost());
                                    } catch (ParseException ex) {
                                        ex.printStackTrace();
                                    }
                                    try {
                                        date2 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).parse(o2.getTimestampPost());
                                    } catch (ParseException ex) {
                                        ex.printStackTrace();
                                    }

                                    return date2.compareTo(date1);
                                }
                            });

                            adapterPromoPost.notifyDataSetChanged();
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
