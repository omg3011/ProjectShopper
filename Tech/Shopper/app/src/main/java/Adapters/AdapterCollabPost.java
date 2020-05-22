package Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crosssellers.CPlatformHomeActivity;
import com.example.crosssellers.CPlatformViewActivity;
import com.example.crosssellers.R;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Models.CPlatform_Model;

public class AdapterCollabPost extends RecyclerView.Adapter<AdapterCollabPost.ViewHolder> {

    Context context;
    List<CPlatform_Model> postList;

    public AdapterCollabPost(Context context, List<CPlatform_Model> postList) {
        this.context = context;
        this.postList = postList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_cplatform_home_helper, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final CPlatform_Model post = postList.get(position);

        //-- Set Text
        String listString;
        if(post.getCollabTag() != null)
        {
            listString = TextUtils.join(", ", post.getCollabTag());
            holder.TV_tags.setText(listString);
        }
        holder.TV_post_description.setText(post.getDescription());

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
        holder.TV_date_posted.setText(strDate);
        holder.TV_time_posted.setText(strTime);

        holder.CV_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CPlatformViewActivity.class);
                intent.putExtra("post", post);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView TV_post_description, TV_tags, TV_date_posted, TV_time_posted;
        CardView CV_click;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            TV_post_description = itemView.findViewById(R.id.cplatform_home_helper_post_description_TV);
            TV_tags = itemView.findViewById(R.id.cplatform_home_helper_tags_TV);
            TV_date_posted = itemView.findViewById(R.id.cplatform_home_helper_date_TV);
            TV_time_posted = itemView.findViewById(R.id.cplatform_home_helper_time_TV);
            CV_click = itemView.findViewById(R.id.cplatform_home_helper_click_CV);
        }
    }
}