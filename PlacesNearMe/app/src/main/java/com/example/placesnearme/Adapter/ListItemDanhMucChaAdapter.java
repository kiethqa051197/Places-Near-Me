package com.example.placesnearme.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placesnearme.Model.DanhMucCha;
import com.example.placesnearme.R;
import com.example.placesnearme.View.MainActivity;

import java.util.List;

class ListItemDanhMucChaViewHolder extends RecyclerView.ViewHolder{
    public TextView txtTenDanhMuc;
    public ImageView imgDanhMuc;
    public CardView cardView;

    public ListItemDanhMucChaViewHolder(@NonNull View itemView) {
        super(itemView);

        txtTenDanhMuc = itemView.findViewById(R.id.txtTenDanhMuc);
        imgDanhMuc = itemView.findViewById(R.id.imgDanhMuc);
        cardView = itemView.findViewById(R.id.cardViewCategory);
    }
}

public class ListItemDanhMucChaAdapter extends RecyclerView.Adapter<ListItemDanhMucChaViewHolder>{
    MainActivity mainActivity;
    List<DanhMucCha> danhMucChaList;

    public ListItemDanhMucChaAdapter(MainActivity mainActivity, List<DanhMucCha> danhMucChaList) {
        this.mainActivity = mainActivity;
        this.danhMucChaList = danhMucChaList;
    }

    @NonNull
    @Override
    public ListItemDanhMucChaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mainActivity.getApplicationContext());
        View view = inflater.inflate(R.layout.list_item_category, parent, false);

        return new ListItemDanhMucChaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListItemDanhMucChaViewHolder holder, final int position) {
        final DanhMucCha danhMucCha = danhMucChaList.get(position);

        holder.txtTenDanhMuc.setText(danhMucCha.getTendanhmuc());

        switch (danhMucCha.getMadanhmuc()){
            case "food":
                holder.imgDanhMuc.setImageResource(R.drawable.ctg_parent_food);
                break;
            case "fun":
                holder.imgDanhMuc.setImageResource(R.drawable.ctg_parent_fun);
                break;
            case "health":
                holder.imgDanhMuc.setImageResource(R.drawable.ctg_parent_health);
                break;
            case "stores":
                holder.imgDanhMuc.setImageResource(R.drawable.ctg_parent_stores);
                break;
            case "service":
                holder.imgDanhMuc.setImageResource(R.drawable.ctg_parent_service);
                break;
            case "sports":
                holder.imgDanhMuc.setImageResource(R.drawable.ctg_parent_sport);
                break;
            case "travel":
                holder.imgDanhMuc.setImageResource(R.drawable.ctg_parent_travel);
                break;
            case "others":
                holder.imgDanhMuc.setImageResource(R.drawable.ctg_parent_others);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return danhMucChaList.size();
    }
}
