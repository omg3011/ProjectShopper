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
import com.example.crosssellers.CPromotionViewActivity;
import com.example.crosssellers.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Models.CPromotion_Model;

public class AdapterPromoPost extends RecyclerView.Adapter<AdapterPromoPost.ViewHolder> {

    Context context;
    List<CPromotion_Model> postList;

    public AdapterPromoPost(Context context, List<CPromotion_Model> postList) {
        this.context = context;
        this.postList = postList;
    }


    @Override
    public AdapterPromoPost.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_cpromotion_home_helper, parent, false);

        return new AdapterPromoPost.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final CPromotion_Model post = postList.get(position);

        //-- Set Tags
        String listString;
        if(post.getTags() != null)
        {
            listString = TextUtils.join(", ", post.getTags());
            holder.TV_tags.setText(listString);
        }

        //-- Set Description
        holder.TV_post_description.setText(post.getDescription());

        //-- Set Promo Date
        holder.TV_promoDate.setText(post.getTimestampStart() + " - " + post.getTimestampEnd());

        holder.CV_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CPromotionViewActivity.class);
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

        TextView TV_post_description, TV_tags, TV_promoDate;
        CardView CV_click;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            TV_post_description = itemView.findViewById(R.id.cpromo_home_helper_post_description_TV);
            TV_tags = itemView.findViewById(R.id.cpromo_home_helper_tags_TV);
            TV_promoDate = itemView.findViewById(R.id.cpromo_home_helper_promoDate_TV);
            CV_click = itemView.findViewById(R.id.cpromo_home_helper_click_CV);
        }
    }
}