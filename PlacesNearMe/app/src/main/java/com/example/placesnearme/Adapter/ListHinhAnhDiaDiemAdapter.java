package com.example.placesnearme.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placesnearme.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

class ListHinhAnhDiaDiemViewHolder extends RecyclerView.ViewHolder{
    ImageView imgDiaDiem;

    public ListHinhAnhDiaDiemViewHolder(@NonNull View itemView) {
        super(itemView);

        imgDiaDiem = itemView.findViewById(R.id.imgDiaDiem);
    }
}

public class ListHinhAnhDiaDiemAdapter extends RecyclerView.Adapter<ListHinhAnhDiaDiemViewHolder>{
    List<String> hinhanhs;
    String madiadiem;

    public ListHinhAnhDiaDiemAdapter(List<String> hinhanhs, String madiadiem) {
        this.hinhanhs = hinhanhs;
        this.madiadiem = madiadiem;
    }

    @NonNull
    @Override
    public ListHinhAnhDiaDiemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_image_places, parent, false);

        return new ListHinhAnhDiaDiemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListHinhAnhDiaDiemViewHolder holder, final int position) {
        String hinhanh = hinhanhs.get(position);
        if (hinhanh.substring(0, hinhanh.indexOf(":")).equals("https")){
            Picasso.get()
                    .load(hinhanh)
                    .placeholder(R.drawable.img_loading)
                    .into(holder.imgDiaDiem);
        }else {
            StorageReference storageImgProductType = FirebaseStorage.getInstance().getReference().child("Images")
                    .child(madiadiem).child(hinhanh);

            long ONE_MEGABYTE = 1024 * 1024;
            storageImgProductType.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    holder.imgDiaDiem.setImageBitmap(bitmap);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return hinhanhs.size();
    }
}