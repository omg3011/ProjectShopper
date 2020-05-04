package Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crosssellers.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.squareup.picasso.Picasso;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    //-- Firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseFirestore fireStore;
    // Create a reference to the cities collection
    CollectionReference dataReference;


    //-- View from xml
    ImageView IV_avatar;
    TextView TV_name, TV_email, TV_phone;

    public ProfileFragment() {
        // Required empty public constructor
    }


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

        //-- Init Views
        IV_avatar = view.findViewById(R.id.profile_avatarIV);
        TV_name = view.findViewById(R.id.profile_nameTV);
        TV_email = view.findViewById(R.id.profile_emailTV);
        TV_phone = view.findViewById(R.id.profile_phoneTV);

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

                                //-- Set Data
                                TV_name.setText(name);
                                TV_email.setText(email);
                                TV_phone.setText(phone);

                                if(imageURL.isEmpty())
                                    imageURL = "Error";

                                //-- Set Image
                                Picasso.get()
                                        .load(imageURL)
                                        .placeholder(R.drawable.ic_add_image)
                                        .error(R.drawable.ic_error)
                                        .into(IV_avatar);

                                break;
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        return view;
    }
}
