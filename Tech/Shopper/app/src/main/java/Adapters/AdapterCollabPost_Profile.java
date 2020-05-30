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

import com.example.crosssellers.ProfileActivity_CPlatformView;
import com.example.crosssellers.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Models.CPlatform_Model;

public class AdapterCollabPost_Profile extends RecyclerView.Adapter<AdapterCollabPost_Profile.ViewHolder> {

    Context context;
    List<CPlatform_Model> postList;

    public AdapterCollabPost_Profile(Context context, List<CPlatform_Model> postList) {
        this.context = context;
        this.postList = postList;
    }


    @Override
    public AdapterCollabPost_Profile.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_profile_cplatform_helper, parent, false);

        return new AdapterCollabPost_Profile.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCollabPost_Profile.ViewHolder holder, final int position) {
        final CPlatform_Model post = postList.get(position);

        //-- Set Text
        String listString;
        if(post.getCollabTag() != null)
        {
            listString = TextUtils.join(", ", post.getCollabTag());
            holder.TV_tags.setText(listString);
        }
        holder.TV_post_title.setText(post.getTitle());

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

        //-- Set closed or not
        if(post.isCollab_closed_flag())
        {
            holder.TV_closed.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.TV_closed.setVisibility(View.INVISIBLE);

            holder.CV_click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProfileActivity_CPlatformView.class);
                    intent.putExtra("post", post);
                    context.startActivity(intent);
                }
            });
        }

        //-- Set Request Count
        int requestCount = post.getPendingRequestCount();
        holder.TV_requestCount.setText(Integer.toString(requestCount));

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView TV_post_title, TV_tags, TV_date_posted, TV_time_posted, TV_requestCount, TV_closed;
        CardView CV_click;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            TV_closed = itemView.findViewById(R.id.profile_cplatform_helper_closed_TV);
            TV_post_title = itemView.findViewById(R.id.profile_cplatform_helper_post_title_TV);
            TV_tags = itemView.findViewById(R.id.profile_cplatform_helper_tags_TV);
            TV_date_posted = itemView.findViewById(R.id.profile_cplatform_helper_date_TV);
            TV_time_posted = itemView.findViewById(R.id.profile_cplatform_helper_time_TV);
            CV_click = itemView.findViewById(R.id.profile_cplatform_helper_click_CV);
            TV_requestCount = itemView.findViewById(R.id.profile_cplatform_helper_requestCount_TV);
        }
    }
}