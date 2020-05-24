package Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crosssellers.CPlatformViewActivity;
import com.example.crosssellers.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Models.CPlatform_Model;
import Models.Notification_Model;

public class AdapterNotifications extends RecyclerView.Adapter<AdapterNotifications.ViewHolder> {

    Context context;
    List<Notification_Model> postList;

    public AdapterNotifications(Context context, List<Notification_Model> postList) {
        this.context = context;
        this.postList = postList;
    }


    @Override
    public AdapterNotifications.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_notification_helper, parent, false);

        return new AdapterNotifications.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterNotifications.ViewHolder holder, final int position) {
        final Notification_Model post = postList.get(position);

        //-- Set Message
        holder.TV_message_description.setText(post.getMessage());

        //-- Set timestamp
        Date date1 = null;
        try {
            date1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).parse(post.getTimestamp());
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = dateFormat.format(date1);

        DateFormat dateFormat2 = new SimpleDateFormat("hh:mm aa");
        String strTime = dateFormat2.format(date1);

        holder.TV_timestamp.setText(strDate + ", " + strTime);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView TV_message_description, TV_timestamp;
        CardView CV_click;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            TV_message_description = itemView.findViewById(R.id.notification_helper_message_TV);
            TV_timestamp = itemView.findViewById(R.id.notification_helper_timestamp_TV);
            CV_click = itemView.findViewById(R.id.notification_helper_cv);
        }
    }
}