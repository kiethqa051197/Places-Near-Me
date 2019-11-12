package com.example.placesnearme.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placesnearme.R;
import com.example.placesnearme.View.DetailPlaceActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

class ListItemViewHolderImagePlace extends RecyclerView.ViewHolder{
    public ImageView imgPlaces;

    public ListItemViewHolderImagePlace(@NonNull View itemView) {
        super(itemView);

        imgPlaces = itemView.findViewById(R.id.imgPlaces);
    }
}

public class ListItemImagePlacesAdapter extends RecyclerView.Adapter<ListItemViewHolderImagePlace>{
    DetailPlaceActivity detailPlaceActivity;
    List<String> image;

    public ListItemImagePlacesAdapter(DetailPlaceActivity detailPlaceActivity, List<String> image) {
        this.detailPlaceActivity = detailPlaceActivity;
        this.image = image;
    }

    @NonNull
    @Override
    public ListItemViewHolderImagePlace onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(detailPlaceActivity.getBaseContext());
        View view = inflater.inflate(R.layout.list_image_place, parent, false);

        return new ListItemViewHolderImagePlace(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListItemViewHolderImagePlace holder, final int position) {
        Picasso.get()
                .load(detailPlaceActivity.getPhotoOfPlace(image.get(position), 1000))
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_error)
                .into(holder.imgPlaces);
    }

    @Override
    public int getItemCount() {
        return image.size();
    }
}
