package com.example.placesnearme.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placesnearme.R;
import com.example.placesnearme.View.DetailPlaceActivity;

import java.util.List;

class ListItemViewHolderDetailCategory extends RecyclerView.ViewHolder{
    public TextView txtNameDetailCategory;
    public ImageView imgDetailCategory;

    public ListItemViewHolderDetailCategory(@NonNull View itemView) {
        super(itemView);

        txtNameDetailCategory = itemView.findViewById(R.id.txtNameDetailCategory);
        imgDetailCategory = itemView.findViewById(R.id.imgDetailCategory);
    }
}

public class ListItemDetailCategoryAdapter extends RecyclerView.Adapter<ListItemViewHolderDetailCategory>{
    DetailPlaceActivity detailPlaceActivity;
    List<String> categoryList;

    public ListItemDetailCategoryAdapter(DetailPlaceActivity detailPlaceActivity, List<String> categoryList) {
        this.detailPlaceActivity = detailPlaceActivity;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public ListItemViewHolderDetailCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(detailPlaceActivity.getBaseContext());
        View view = inflater.inflate(R.layout.custom_list_detail_category, parent, false);

        return new ListItemViewHolderDetailCategory(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListItemViewHolderDetailCategory holder, final int position) {
        String category = categoryList.get(position);

        holder.txtNameDetailCategory.setText(category);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }
}
