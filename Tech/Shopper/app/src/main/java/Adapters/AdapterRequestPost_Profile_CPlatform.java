package Adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crosssellers.ChatActivity;
import com.example.crosssellers.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import Models.CPlatform_Model;
import Models.FriendList_Model;
import Models.Notification_Model;
import Models.RequestMailBox_Model;

public class AdapterRequestPost_Profile_CPlatform extends RecyclerView.Adapter<AdapterRequestPost_Profile_CPlatform.ViewHolder> {

    Context context;
    List<RequestMailBox_Model> requestList;
    CollectionReference dataReference_User;
    CollectionReference dataReference_Request;
    CollectionReference dataReference_Notification;
    CollectionReference dataReference_CPlatform;
    CollectionReference dataReference_FriendList;
    CPlatform_Model postData;
    ProgressDialog pd;
    FirebaseUser fUser;
    int request1_accepted2;
    Activity activity;


    public AdapterRequestPost_Profile_CPlatform(Context context, List<RequestMailBox_Model> postList, CollectionReference dataReference_User, CPlatform_Model postData, CollectionReference dataReference_Request, CollectionReference dataReference_Notification, CollectionReference dataReference_CPlatform, int request1_accepted2, Activity activity, CollectionReference dataReference_FriendList) {
        this.context = context;
        this.requestList = postList;
        this.dataReference_User = dataReference_User;
        this.dataReference_Request = dataReference_Request;
        this.dataReference_CPlatform = dataReference_CPlatform;
        this.postData = postData;
        this.pd = new ProgressDialog(context);
        this.fUser = FirebaseAuth.getInstance().getCurrentUser();
        this.dataReference_Notification = dataReference_Notification;
        this.request1_accepted2 = request1_accepted2;
        this.activity = activity;
        this.dataReference_FriendList = dataReference_FriendList;
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
        final DocumentReference doc_user = dataReference_User.document(post.getRequester_UID());

        //-----------------------------------------------------------------------//
        // Update UI
        //-----------------------------------------------------------------------//
        doc_user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                //-- Set store name
                requesterStoreName[0] = (String) documentSnapshot.get("storeName");
                holder.TV_requesterStoreName.setText(requesterStoreName[0]);

                //-- Set Post Title
                holder.TV_requesterStoreTag.setText((String) documentSnapshot.get("storeTag"));

                //-- Set Store unit
                holder.TV_requesterStoreUnit.setText((String) documentSnapshot.get("storeUnit"));

                //-- Show Request Pending UI
                if(request1_accepted2 == 1)
                {
                    holder.BTN_accept.setVisibility(View.VISIBLE);
                    holder.BTN_reject.setVisibility(View.VISIBLE);
                    holder.BTN_chat.setVisibility(View.GONE);

                    //-- Accept Btn
                    holder.BTN_accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendAcceptRequestToDB(post);
                        }
                    });

                    //-- Reject Btn
                    holder.BTN_reject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendRejectRequestToDB(post);
                        }
                    });

                }
                //-- Show Request Accepted UI
                else
                {
                    holder.BTN_accept.setVisibility(View.GONE);
                    holder.BTN_reject.setVisibility(View.GONE);
                    holder.BTN_chat.setVisibility(View.VISIBLE);
                    //-- View Requester Profile
                    holder.BTN_chat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /* Click user from userList to start chatting / messaging.
                             * > Start Activity by putting UID of receiver
                             * > we will use that UID to identify the user we are gonna chat with.
                             */
                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.putExtra("hisUid", post.getRequester_UID());
                            intent.putExtra("cpostUID", post.getCplatformPost_ID());
                            context.startActivity(intent);
                            activity.finish();
                        }
                    });
                }
            }
        });




    }

    private void sendRejectRequestToDB(final RequestMailBox_Model post) {
        HashMap<String, Object> results = new HashMap<>();
        results.put("status", "rejected");
        pd.setMessage("Rejecting. Please wait...");
        pd.show();

        //-- Modify data => Query
        dataReference_Request.document(post.getRequestMailBoxID()).update(results)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        final String[] requesterStoreName = new String[1];
                        final String[] myStoreName = new String[1];
                        dataReference_User.document(post.getRequester_UID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                //-----------------------------------------------------------------------//
                                // Get Requester Store Name
                                //-----------------------------------------------------------------------//
                                requesterStoreName[0] = (String) documentSnapshot.get("storeName");

                                dataReference_User.document(fUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {


                                        //-----------------------------------------------------------------------//
                                        // Get My Store Name
                                        //-----------------------------------------------------------------------//
                                        myStoreName[0] = (String) documentSnapshot.get("storeName");

                                        final Long[] pendingRequestcount = new Long[1];
                                        dataReference_CPlatform.document(post.getCplatformPost_ID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                //-----------------------------------------------------------------------//
                                                // Get My CPlatform post->pendingRequestCount
                                                //-----------------------------------------------------------------------//
                                                pendingRequestcount[0] = (Long)documentSnapshot.get("pendingRequestCount");
                                                Log.d("Test", Long.toString(pendingRequestcount[0]));
                                                pendingRequestcount[0]-=1;
                                                Log.d("Test", Long.toString(pendingRequestcount[0]));
                                                HashMap<String, Object> result_requestCount = new HashMap<>();
                                                result_requestCount.put("pendingRequestCount", pendingRequestcount[0]);

                                                dataReference_CPlatform.document(post.getCplatformPost_ID()).update(result_requestCount)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        //--------------------------------------------------------------------------------//
                                                        // Notify database
                                                        //--------------------------------------------------------------------------------//
                                                        //-- Get current Timestamp
                                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                                        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
                                                        final String timestampPost = simpleDateFormat.format(new Date());

                                                        // Send to rejected user, you have been rejected
                                                        Notification_Model notification1 = new Notification_Model(timestampPost, post.getRequester_UID(), "You have been rejected to collaborate with " + myStoreName[0] + ".");
                                                        dataReference_Notification.document().set(notification1);

                                                        // Send to myself, i reject someone
                                                        Notification_Model notification2 = new Notification_Model(timestampPost, fUser.getUid(), "You have rejected a collaboration with " + requesterStoreName[0] + ".");
                                                        dataReference_Notification.document().set(notification2);

                                                        //--------------------------------------------------------------------------------//
                                                        // Update UI
                                                        //--------------------------------------------------------------------------------//
                                                        requestList.remove(post);
                                                        pd.dismiss();
                                                        notifyDataSetChanged();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                    }
                });
    }

    private void sendAcceptRequestToDB(final RequestMailBox_Model post)
    {
        HashMap<String, Object> results = new HashMap<>();
        results.put("status", "accepted");
        pd.setMessage("Accepting. Please wait...");
        pd.show();

        //-- Modify data => Query
        dataReference_Request.document(post.getRequestMailBoxID()).update(results)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        final String[] requesterStoreName = new String[1];
                        final String[] myStoreName = new String[1];
                        dataReference_User.document(post.getRequester_UID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                //-----------------------------------------------------------------------//
                                // Get Requester Store Name
                                //-----------------------------------------------------------------------//
                                requesterStoreName[0] = (String) documentSnapshot.get("storeName");

                                dataReference_User.document(fUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {


                                        //-----------------------------------------------------------------------//
                                        // Get My Store Name
                                        //-----------------------------------------------------------------------//
                                        myStoreName[0] = (String) documentSnapshot.get("storeName");

                                        final Long[] pendingRequestcount = new Long[1];
                                        dataReference_CPlatform.document(post.getCplatformPost_ID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                //-----------------------------------------------------------------------//
                                                // Get My CPlatform post->pendingRequestCount
                                                //-----------------------------------------------------------------------//
                                                pendingRequestcount[0] = (Long)documentSnapshot.get("pendingRequestCount");
                                                pendingRequestcount[0]-=1;
                                                HashMap<String, Object> result_requestCount = new HashMap<>();
                                                result_requestCount.put("pendingRequestCount", pendingRequestcount[0]);

                                                dataReference_CPlatform.document(post.getCplatformPost_ID()).update(result_requestCount)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                //--------------------------------------------------------------------------------//
                                                                // Notify database
                                                                //--------------------------------------------------------------------------------//
                                                                //-- Get current Timestamp
                                                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                                                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
                                                                final String timestampPost = simpleDateFormat.format(new Date());

                                                                // Send to accepted user, you have been accpted
                                                                Notification_Model notification1 = new Notification_Model(timestampPost, post.getRequester_UID(), "You have been accepted to collaborate with " + myStoreName[0] + ".");
                                                                dataReference_Notification.document().set(notification1);

                                                                // Send to myself, i accpted someone
                                                                Notification_Model notification2 = new Notification_Model(timestampPost, fUser.getUid(), "You have accepted a collaboration with " + requesterStoreName[0] + ".");
                                                                dataReference_Notification.document().set(notification2);




                                                                // Send to DB, friendList
                                                                //(String friendPost_uid, String requester_uid, String owner_uid, String cpost_uid)
                                                                String id = dataReference_FriendList.document().getId();
                                                                FriendList_Model friendModel = new FriendList_Model(id, post.getRequester_UID(), postData.getPosterUid(), postData.getCPost_uid());
                                                                dataReference_FriendList.document(id).set(friendModel);

                                                                //--------------------------------------------------------------------------------//
                                                                // Update UI
                                                                //--------------------------------------------------------------------------------//
                                                                //requestList.remove(post);
                                                                pd.dismiss();
                                                                //notifyDataSetChanged();
                                                            }
                                                        });
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView TV_requesterStoreName, TV_requesterStoreTag, TV_requesterStoreUnit;
        Button BTN_accept, BTN_reject, BTN_chat;
        RatingBar RB;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            TV_requesterStoreName = itemView.findViewById(R.id.profile_cplatform_view_helper_storeName_TV);
            TV_requesterStoreTag = itemView.findViewById(R.id.profile_cplatform_view_helper_storeTag_TV);
            TV_requesterStoreUnit = itemView.findViewById(R.id.profile_cplatform_view_helper_storeUnit_TV);
            RB = itemView.findViewById(R.id.profile_cplatform_view_helper_rating_RB);
            BTN_accept = itemView.findViewById(R.id.profile_cplatform_view_helper_accept_btn);
            BTN_reject = itemView.findViewById(R.id.profile_cplatform_view_helper_reject_btn);
            BTN_chat = itemView.findViewById(R.id.profile_cplatform_view_helper_chat_btn);
        }
    }
}