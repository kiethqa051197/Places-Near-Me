package com.example.placesnearme.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placesnearme.Model.DanhMuc;
import com.example.placesnearme.R;
import com.example.placesnearme.View.CategoryActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

class ListItemDanhMucViewHolder extends RecyclerView.ViewHolder{
    public TextView txtTenDanhMuc;
    public ImageView imgDanhMuc;
    public CardView cardView;

    public ListItemDanhMucViewHolder(@NonNull View itemView) {
        super(itemView);

        txtTenDanhMuc = itemView.findViewById(R.id.txtTenDanhMuc);
        imgDanhMuc = itemView.findViewById(R.id.imgDanhMuc);
        cardView = itemView.findViewById(R.id.cardViewCategory);
    }
}

public class ListItemDanhMucAdapter extends RecyclerView.Adapter<ListItemDanhMucViewHolder>{
    CategoryActivity categoryActivity;
    List<DanhMuc> danhMucList;

    public ListItemDanhMucAdapter(CategoryActivity categoryActivity, List<DanhMuc> danhMucList) {
        this.categoryActivity = categoryActivity;
        this.danhMucList = danhMucList;
    }

    @NonNull
    @Override
    public ListItemDanhMucViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(categoryActivity.getApplicationContext());
        View view = inflater.inflate(R.layout.list_item_category, parent, false);

        return new ListItemDanhMucViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListItemDanhMucViewHolder holder, final int position) {
        final DanhMuc danhMuc = danhMucList.get(position);

        holder.txtTenDanhMuc.setText(danhMuc.getTendanhmuc());

        StorageReference storageImgProductType = FirebaseStorage.getInstance().getReference().child("Danh Muc").child(danhMuc.getHinhanh());
        long ONE_MEGABYTE = 1024 * 1024;
        storageImgProductType.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.imgDanhMuc.setImageBitmap(bitmap);
            }
        });

    }

    @Override
    public int getItemCount() {
        return danhMucList.size();
    }
}
