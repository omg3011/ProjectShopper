package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crosssellers.R;

import java.util.ArrayList;
import java.util.List;

import Models.CPromotion_Model;
import Models.User_Model;

public class AdapterUser_Tenant extends RecyclerView.Adapter<AdapterUser_Tenant.ViewHolder> {

    Context context;
    List<User_Model> userList_current;
    List<User_Model> userList_copy;

    public AdapterUser_Tenant(Context context, List<User_Model> userList_current) {
        this.context = context;
        this.userList_current = userList_current;
        this.userList_copy = userList_current;
    }


    @Override
    public AdapterUser_Tenant.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_mall_insight_tenant_helper, parent, false);

        return new AdapterUser_Tenant.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterUser_Tenant.ViewHolder holder, final int position) {
        final User_Model post = userList_current.get(position);

        // Store Name
        holder.TV_storeName.setText(post.getStoreName());

        // Store Tag
        holder.TV_storeTag.setText(post.getStoreTag());

        // Store Unit
        holder.TV_storeUnit.setText(post.getStoreUnit());

        // Chat Button
        /*
        holder.Btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(context, CPlatformViewActivity.class);
                //intent.putExtra("post", post);
                //context.startActivity(intent);
            }
        });

         */
    }


    public void filter(List<String> textList) {
        if(textList.size() <= 0)
        {
        }
        else if(textList.contains("All"))
        {
            userList_current = userList_copy;
        }
        else
        {
            ArrayList<User_Model> result = new ArrayList<>();

            // Check each tag selected in the filter
            for(User_Model item: userList_copy)
            {
                for(String text : textList)
                {
                    if(item.getStoreTag().equals(text) )
                        result.add(item);
                }
            }

            userList_current = result;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return userList_current.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView TV_storeName, TV_storeTag, TV_storeUnit;
        //Button Btn_chat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            TV_storeName = itemView.findViewById(R.id.mall_insight_tenant_helper_storeName_TV);
            TV_storeTag = itemView.findViewById(R.id.mall_insight_tenant_helper_storeTag_TV);
            TV_storeUnit = itemView.findViewById(R.id.mall_insight_tenant_helper_storeUnit_TV);
            //Btn_chat = itemView.findViewById(R.id.mall_insight_tenant_helper_chat_btn);
        }
    }
    boolean CheckListContainString(List<String> tags, String check)
    {
        return tags.contains(check);
    }
}