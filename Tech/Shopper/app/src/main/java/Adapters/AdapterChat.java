package Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crosssellers.ChatActivity;
import com.example.crosssellers.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import Models.Chat_Model;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder> {

    //-- Use to indicate what xml to show for receiver and sender in ChatActivity
    //    > MSG_TYPE_LEFT: receiver
    //    > MSG_TYPE_RIGHT: sender
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    //-- Cache Reference(s)
    Context context;                // Usage: Cache reference for view
    FirebaseUser firebaseUser;      // Usage: Get database user info

    //-- Private variable(s)
    List<Chat_Model> chatList;      // Usage: List of chat messages
    String imageUrl;                // Usage: receiver image url, to show their jiao bian in the chat message


    //--------------------------------------------------------------------------------------------------------//
    //
    // Built-In Function(s)
    //
    //--------------------------------------------------------------------------------------------------------//
    //------------------------------------------------------------//
    // Constructor(s)
    //------------------------------------------------------------//
    public AdapterChat(Context context, List<Chat_Model> chatList, String hisImage) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = hisImage;
    }

    //------------------------------------------------------------//
    // Bind xml
    //------------------------------------------------------------//
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        //-- Inflate layouts: row_chat_left.xml for receiver, row_chat_right.xml for sender
        if(i == MSG_TYPE_RIGHT)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false);
            return new MyHolder(view, 1);
        }
        else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false);
            return new MyHolder(view, 2);
        }
    }

    //------------------------------------------------------------//
    // Set value to every element in the scrolling chat list
    //------------------------------------------------------------//
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int i) {
        // Get Data
        String message = chatList.get(i).getMessage();
        String timeStamp = chatList.get(i).getTimestamp();

        // Convert time stamp to dd/mm/yyyy hh:mm am/pm
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

        // Set data
        holder.TV_message.setText(message);
        holder.TV_time.setText(dateTime);

        if(imageUrl.isEmpty())
            imageUrl = "Error";

        //-- Set Image
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.ic_add_image)
                .error(R.drawable.ic_error)
                .into(holder.IV_profile);

        //-- Set seen/delivered status of message
        if(i == chatList.size()-1)
        {
            if(chatList.get(i).isSeen())
            {
                holder.TV_isSeen.setText("Seen");

                // Owner
                if(holder.getMyType() == 1)
                {
                    holder.TV_isSeen.setVisibility(View.VISIBLE);
                }
                else
                {
                    holder.TV_isSeen.setVisibility(View.GONE);
                }
            }
            else
            {
                holder.TV_isSeen.setText("Delivered");
            }
        }
        else
        {
            holder.TV_isSeen.setVisibility(View.GONE);
        }
    }

    //------------------------------------------------------------//
    // Getter(s)
    //------------------------------------------------------------//
    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        //Get currently signed in user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(chatList.get(position).getSender().equals(firebaseUser.getUid()))
        {
            return MSG_TYPE_RIGHT;
        }
        else
        {
            return MSG_TYPE_LEFT;
        }
    }

    //--------------------------------------------------------------------------------------------------------//
    //
    // User-Defined Function(s)
    //
    //--------------------------------------------------------------------------------------------------------//
    //------------------------------------------------------------//
    // Function: Setter(s)
    //------------------------------------------------------------//
    public void setLastMessageSeen(Chat_Model newChat)
    {
        chatList.set(chatList.size()-1, newChat);
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    //
    // View holder class
    // (This is because to do scrolling UI, need RecyclerView component, we must prepare a custom class for it to access each element in the scrolling chat list)
    //
    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
    class MyHolder extends RecyclerView.ViewHolder{

        //-- Cache Reference(s)
        ImageView IV_profile;
        TextView TV_message, TV_time, TV_isSeen;

        //-- Helper Variable(s)
        int type_1owner_2other;         // Usage: to ensure that we will only display textView "Seen"/"Delivered" on sender chat screen


        //------------------------------------------------------------//
        // Constructor(s)
        //------------------------------------------------------------//
        public MyHolder(@NonNull View itemView, int type_1owner_2other) {
            super(itemView);

            // Type 1 = owner
            // Type 2 = other
            this.type_1owner_2other = type_1owner_2other;

            //-- Init View
            IV_profile = itemView.findViewById(R.id.row_chat_profileIV);
            TV_message = itemView.findViewById(R.id.row_chat_messageTV);
            TV_time = itemView.findViewById(R.id.row_chat_timeTV);
            TV_isSeen = itemView.findViewById(R.id.row_chat_isSeenTV);
        }

        //------------------------------------------------------------//
        // Getter(s)
        //------------------------------------------------------------//
        public int getMyType() {
            return type_1owner_2other;
        }


    }
}
