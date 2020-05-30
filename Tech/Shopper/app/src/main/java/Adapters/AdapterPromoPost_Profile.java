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

import com.example.crosssellers.CPromotionViewActivity;
import com.example.crosssellers.ProfileActivity_CPromotionView;
import com.example.crosssellers.R;

import java.util.List;

import Models.CPromotion_Model;

public class AdapterPromoPost_Profile  extends RecyclerView.Adapter<AdapterPromoPost_Profile.ViewHolder> {

    Context context;
    List<CPromotion_Model> postList;

    public AdapterPromoPost_Profile(Context context, List<CPromotion_Model> postList) {
        this.context = context;
        this.postList = postList;
    }


    @Override
    public AdapterPromoPost_Profile.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_profile_promo_helper, parent, false);

        return new AdapterPromoPost_Profile.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(AdapterPromoPost_Profile.ViewHolder holder, final int position) {
        final CPromotion_Model post = postList.get(position);

        //-- Set Tags
        String listString;
        if(post.getTags() != null)
        {
            listString = TextUtils.join(", ", post.getTags());
            holder.TV_tags.setText(listString);
        }

        //-- Set Description
        holder.TV_post_title.setText(post.getTitle());

        //-- Set Promo Date
        holder.TV_promoDate.setText(post.getTimestampStart() + " - " + post.getTimestampEnd());


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
                    Intent intent = new Intent(context, ProfileActivity_CPromotionView.class);
                    intent.putExtra("post", post);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView TV_post_title, TV_tags, TV_promoDate, TV_closed;
        CardView CV_click;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            TV_closed = itemView.findViewById(R.id.profile_promo_helper_closed_TV);
            TV_post_title = itemView.findViewById(R.id.profile_promo_helper_post_title_TV);
            TV_tags = itemView.findViewById(R.id.profile_promo_helper_tags_TV);
            TV_promoDate = itemView.findViewById(R.id.profile_promo_helper_date_TV);
            CV_click = itemView.findViewById(R.id.profile_promo_helper_click_CV);
        }
    }
}