package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crosssellers.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterStoreImages extends RecyclerView.Adapter<AdapterStoreImages.ViewHolder> {

    Context context;
    List<String> uriList;

    public AdapterStoreImages(Context context, List<String> uriList) {
            this.context = context;
            this.uriList = uriList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.activity_cplatform_view_helper, parent, false);

            return new ViewHolder(v);
            }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        String imageUri = uriList.get(position);

        if(imageUri.isEmpty())
            imageUri = "Empty";

        Picasso.get()
                .load(imageUri)
                .placeholder(R.drawable.ic_add_image)
                .error(R.drawable.ic_error)
                .into(holder.IV_storeImage);


    }

    @Override
    public int getItemCount() {
        return uriList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView IV_storeImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            IV_storeImage = itemView.findViewById(R.id.cplatform_view_helper_IV);
        }
    }
}