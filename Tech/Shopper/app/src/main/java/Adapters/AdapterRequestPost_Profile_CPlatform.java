package Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crosssellers.CPromotionViewActivity;
import com.example.crosssellers.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

import Models.CPlatform_Model;
import Models.RequestMailBox_Model;

public class AdapterRequestPost_Profile_CPlatform extends RecyclerView.Adapter<AdapterRequestPost_Profile_CPlatform.ViewHolder> {

    Context context;
    List<RequestMailBox_Model> requestList;
    CollectionReference dataReference_User;
    CPlatform_Model postData;

    public AdapterRequestPost_Profile_CPlatform(Context context, List<RequestMailBox_Model> postList, CollectionReference dataReference_User, CPlatform_Model postData) {
        this.context = context;
        this.requestList = postList;
        this.dataReference_User = dataReference_User;
        this.postData = postData;
    }


    @Override
    public AdapterRequestPost_Profile_CPlatform.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_profile_cplatform_view_helper, parent, false);

        return new AdapterRequestPost_Profile_CPlatform.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final AdapterRequestPost_Profile_CPlatform.ViewHolder holder, final int position) {
        final RequestMailBox_Model post = requestList.get(position);

        //-----------------------------------------------------------------------//
        // Variable(s)
        //-----------------------------------------------------------------------//
        //-- For requester store name
        final String[] requesterStoreName = new String[1];
        final DocumentReference doc_user = dataReference_User.document(post.getUid());

        //-----------------------------------------------------------------------//
        // Update UI
        //-----------------------------------------------------------------------//
        doc_user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                //-- Set Requester Store Name
                requesterStoreName[0] = (String) documentSnapshot.get("storeName");
                holder.TV_requesterStoreName.setText(requesterStoreName[0]);

                //-- Set Post Title
                holder.TV_requesterStoreName.setText(postData.getTitle());

                //-- Accept Btn
                holder.BTN_accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "Accepted", Toast.LENGTH_SHORT).show();
                    }
                });

                //-- Reject Btn
                holder.BTN_reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "Rejected", Toast.LENGTH_SHORT).show();
                    }
                });

                //-- View Requester Profile
                holder.BTN_requesterProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "View Profile", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });




    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView TV_requesterStoreName, TV_postTitle;
        Button BTN_accept, BTN_reject, BTN_requesterProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            TV_requesterStoreName = itemView.findViewById(R.id.profile_cplatform_view_helper_storeName_TV);
            TV_postTitle = itemView.findViewById(R.id.profile_cplatform_view_helper_postTitle_TV);
            BTN_accept = itemView.findViewById(R.id.profile_cplatform_view_helper_accept_btn);
            BTN_reject = itemView.findViewById(R.id.profile_cplatform_view_helper_reject_btn);
            BTN_requesterProfile = itemView.findViewById(R.id.profile_cplatform_view_helper_requesterProfile_btn);
        }
    }
}