package com.example.placesnearme.Adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placesnearme.Common;
import com.example.placesnearme.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

class ListHinhAnhDiaDiemViewHolder extends RecyclerView.ViewHolder{
    ImageView imgDiaDiem;
    FrameLayout frameLayout;
    TextView txtSoHinh;

    ListHinhAnhDiaDiemViewHolder(@NonNull View itemView) {
        super(itemView);

        imgDiaDiem = itemView.findViewById(R.id.imgDiaDiem);
        frameLayout = itemView.findViewById(R.id.khungsohinh);
        txtSoHinh = itemView.findViewById(R.id.txtSoHinh);
    }
}

public class ListHinhAnhDiaDiemAdapter extends RecyclerView.Adapter<ListHinhAnhDiaDiemViewHolder>{
    private List<String> hinhanhs;
    private String madiadiem;

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

        if (hinhanh.substring(0, 5).equals("https")){
            Picasso.get()
                    .load(hinhanh)
                    .placeholder(R.drawable.img_loading)
                    .into(holder.imgDiaDiem);
        }else {
            StorageReference storageImgProductType = FirebaseStorage.getInstance().getReference().child(Common.IMAGE)
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

        if (position == 3){
            int sohinhconlai = hinhanhs.size() - 4;

            if(sohinhconlai > 0) {
                holder.frameLayout.setVisibility(View.VISIBLE);
                holder.txtSoHinh.setText("+" + sohinhconlai);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (hinhanhs.size() < 4) {
            return hinhanhs.size();
        } else
            return 4;
    }
}