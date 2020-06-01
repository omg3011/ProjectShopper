package Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crosssellers.CPromotionViewActivity;
import com.example.crosssellers.R;

import java.util.ArrayList;
import java.util.List;

import Models.CPromotion_Model;

public class AdapterPromoPost_Promo extends RecyclerView.Adapter<AdapterPromoPost_Promo.ViewHolder> {

    Context context;
    List<CPromotion_Model> postList_current;
    List<CPromotion_Model> postList_copy;

    public AdapterPromoPost_Promo(Context context, List<CPromotion_Model> postList_current) {
        this.context = context;
        this.postList_current = postList_current;
        this.postList_copy = postList_current;
    }


    @Override
    public AdapterPromoPost_Promo.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_cpromotion_home_helper, parent, false);

        return new AdapterPromoPost_Promo.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final CPromotion_Model post = postList_current.get(position);

        //-- Set Tags
        String listString;
        if(post.getTags() != null)
        {
            listString = TextUtils.join(", ", post.getTags());
            holder.TV_tags.setText(listString);
        }

        //-- Set Title
        holder.TV_post_title.setText(post.getTitle());

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
        return postList_current.size();
    }

    public void filter(List<String> textList) {
        if(textList.size() <= 0)
        {
        }
        else if(textList.contains("All"))
        {
            postList_current = postList_copy;
        }
        else
        {
            ArrayList<CPromotion_Model> result = new ArrayList<>();

            // Check each tag selected in the filter
            for(CPromotion_Model item: postList_copy)
            {
                for(String text : textList)
                {
                    if(CheckListContainString(item.getTags(), text))
                        result.add(item);
                }
            }

            postList_current = result;
        }
        notifyDataSetChanged();
    }

    boolean CheckListContainString(List<String> tags, String check)
    {
        return tags.contains(check);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView TV_post_title, TV_tags, TV_promoDate;
        CardView CV_click;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            TV_post_title = itemView.findViewById(R.id.cpromo_home_helper_post_title_TV);
            TV_tags = itemView.findViewById(R.id.cpromo_home_helper_tags_TV);
            TV_promoDate = itemView.findViewById(R.id.cpromo_home_helper_promoDate_TV);
            CV_click = itemView.findViewById(R.id.cpromo_home_helper_click_CV);
        }
    }
}