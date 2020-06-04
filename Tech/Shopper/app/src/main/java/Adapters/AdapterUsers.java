package Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crosssellers.ChatActivity;
import com.example.crosssellers.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Models.CPlatform_Model;
import Models.Chat_Model;
import Models.RequestMailBox_Model;
import Models.User_Model;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder>{

    //-- Cache Reference(s)
    Context context;
    Activity activity;

    //-- Private variable(s)
    List<User_Model> userList;          // Usage: List of user to display in "User" navList
    List<RequestMailBox_Model> requestList;          // Usage: List of user to display in "User" navList
    CollectionReference dataReference_CPlatform;
    CollectionReference dataReference_Chat;

    //--------------------------------------------------------------------------------------------------------//
    //
    // Built-In Function(s)
    //
    //--------------------------------------------------------------------------------------------------------//
    //------------------------------------------------------------//
    // Constructor(s)
    //------------------------------------------------------------//
    public AdapterUsers(Context context, List<User_Model> userList, CollectionReference dataReference_CPlatform, List<RequestMailBox_Model> requestList, Activity activity, CollectionReference dataReference_Chat) {
        this.context = context;
        this.userList = userList;
        this.dataReference_CPlatform = dataReference_CPlatform;
        this.requestList = requestList;
        this.activity = activity;
        this.dataReference_Chat = dataReference_Chat;
    }

    //------------------------------------------------------------//
    // Bind xml
    //------------------------------------------------------------//
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout (row_users.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.row_users, parent, false);

        return new MyHolder(view);
    }

    //------------------------------------------------------------//
    // Set value to every element in the scrolling list
    //------------------------------------------------------------//
    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int i) {
        // Get data of each element
        final String hisUID = userList.get(i).getUid();
        final String[] userImage = {userList.get(i).getImage()};
        final String userName = userList.get(i).getStoreName();
        final RequestMailBox_Model rModel = requestList.get(i);
        final String[] cpostTitle = {""};

        final int[] newChatCounter = {0};
        dataReference_Chat.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                // If got error, end
                if(e != null)
                    return;

                // Check until required info is received
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges())
                {
                    Chat_Model cmodel = doc.getDocument().toObject(Chat_Model.class);

                    // show only my post
                    if(!cmodel.getCpost_uid().equals(rModel.getCplatformPost_ID()))
                        continue;
                    //-- Is message added / modified / removed?
                    switch(doc.getType())
                    {
                        case ADDED:
                            if(cmodel.isSeen() == false && cmodel.getSender().equals(hisUID))
                                newChatCounter[0]++;
                            break;
                        case MODIFIED:
                            break;
                        case REMOVED:
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + doc.getType());
                    }
                }

                if(newChatCounter[0] <= 0)
                {
                    holder.TV_newChatCounter.setVisibility(View.INVISIBLE);
                }
                else
                {
                    holder.TV_newChatCounter.setVisibility(View.VISIBLE);
                    holder.TV_newChatCounter.setText(Integer.toString(newChatCounter[0]));
                }
            }
        });

        dataReference_CPlatform.document(rModel.getCplatformPost_ID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final CPlatform_Model cModel = documentSnapshot.toObject(CPlatform_Model.class);
                cpostTitle[0] = cModel.getTitle();

                // Set data
                holder.TV_storeName.setText(userName);
                holder.TV_collabTitle.setText(cpostTitle[0]);


                if(userImage[0].isEmpty())
                    userImage[0] = "Error";

                //-- Set Image
                Picasso.get()
                        .load(userImage[0])
                        .placeholder(R.drawable.ic_add_image)
                        .error(R.drawable.ic_error)
                        .into(holder.IV_avatar);

                //-- Handle item/element click
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /* Click user from userList to start chatting / messaging.
                         * > Start Activity by putting UID of receiver
                         * > we will use that UID to identify the user we are gonna chat with.
                         */
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("hisUid", hisUID);
                        intent.putExtra("requestPost", rModel);
                        intent.putExtra("cplatformPost", cModel);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                });
            }
        });

    }

    //------------------------------------------------------------//
    // Getter(s)
    //------------------------------------------------------------//
    @Override
    public int getItemCount() {
        return userList.size();
    }


    //----------------------------------------------------------------------------//
    //
    // View Holder Class
    //
    //----------------------------------------------------------------------------//
    class MyHolder extends RecyclerView.ViewHolder {

        //-- Cache Reference(s)
        ImageView IV_avatar;
        TextView TV_storeName, TV_collabTitle, TV_newChatCounter;

        //------------------------------------------------------------//
        // Constructor(s)
        //------------------------------------------------------------//
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //-- Init Views
            TV_newChatCounter = itemView.findViewById(R.id.users_counterTV);
            IV_avatar = itemView.findViewById(R.id.users_avatarIV);
            TV_storeName = itemView.findViewById(R.id.users_nameTV);
            TV_collabTitle = itemView.findViewById(R.id.users_collabTitleTV);
        }
    }
}
