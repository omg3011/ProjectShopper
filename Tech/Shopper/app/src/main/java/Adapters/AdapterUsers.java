package Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crosssellers.ChatActivity;
import com.example.crosssellers.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import Models.User_Model;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder>{

    //-- Cache Reference(s)
    Context context;

    //-- Private variable(s)
    List<User_Model> userList;          // Usage: List of user to display in "User" navList


    //--------------------------------------------------------------------------------------------------------//
    //
    // Built-In Function(s)
    //
    //--------------------------------------------------------------------------------------------------------//
    //------------------------------------------------------------//
    // Constructor(s)
    //------------------------------------------------------------//
    public AdapterUsers(Context context, List<User_Model> userList) {
        this.context = context;
        this.userList = userList;
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
    public void onBindViewHolder(@NonNull MyHolder holder, int i) {
        // Get data of each element
        final String hisUID = userList.get(i).getUid();
        String userImage = userList.get(i).getImage();
        String userName = userList.get(i).getName();
        final String userEmail = userList.get(i).getEmail();

        // Set data
        holder.TV_name.setText(userName);
        holder.TV_email.setText(userEmail);


        if(userImage.isEmpty())
            userImage = "Error";

        //-- Set Image
        Picasso.get()
                .load(userImage)
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
                context.startActivity(intent);
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
        TextView TV_name, TV_email;

        //------------------------------------------------------------//
        // Constructor(s)
        //------------------------------------------------------------//
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //-- Init Views
            IV_avatar = itemView.findViewById(R.id.users_avatarIV);
            TV_name = itemView.findViewById(R.id.users_nameTV);
            TV_email = itemView.findViewById(R.id.users_emailTV);
        }
    }
}
