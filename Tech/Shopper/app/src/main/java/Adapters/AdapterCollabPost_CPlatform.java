package Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crosssellers.CPlatformViewActivity;
import com.example.crosssellers.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Models.CPlatform_Model;
import Models.User_Model;

public class AdapterCollabPost_CPlatform extends RecyclerView.Adapter<AdapterCollabPost_CPlatform.ViewHolder> {

    Context context;
    List<CPlatform_Model> postList;
    CollectionReference dataReference_user;

    public AdapterCollabPost_CPlatform(Context context, List<CPlatform_Model> postList, CollectionReference dataReference_user) {
        this.context = context;
        this.postList = postList;
        this.dataReference_user = dataReference_user;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_cplatform_home_helper, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
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

        holder.CV_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CPlatformViewActivity.class);
                intent.putExtra("post", post);
                context.startActivity(intent);
            }
        });

        dataReference_user.document(post.getPosterUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final User_Model user = documentSnapshot.toObject(User_Model.class);
                if(user.getRatingList() == null || user.getRatingList().size() <= 0)
                {
                    holder.RB.setRating(0.0f);
                }
                else
                    holder.RB.setRating(GetRatingFromList(user.getRatingList()));
            }
        });
    }

    Float GetRatingFromList(List<Double> ratings)
    {
        String rateString = "";
        double rateValue = 0.0f;

        for(Double x : ratings)
        {
            rateValue += x;
        }

        rateValue /= ratings.size();

        return (float)rateValue;
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView TV_post_title, TV_tags, TV_date_posted, TV_time_posted;
        CardView CV_click;
        RatingBar RB;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            RB = itemView.findViewById(R.id.cplatform_home_helper_rating_RB);
            TV_post_title = itemView.findViewById(R.id.cplatform_home_helper_post_title_TV);
            TV_tags = itemView.findViewById(R.id.cplatform_home_helper_tags_TV);
            TV_date_posted = itemView.findViewById(R.id.cplatform_home_helper_date_TV);
            TV_time_posted = itemView.findViewById(R.id.cplatform_home_helper_time_TV);
            CV_click = itemView.findViewById(R.id.cplatform_home_helper_click_CV);
        }
    }
}