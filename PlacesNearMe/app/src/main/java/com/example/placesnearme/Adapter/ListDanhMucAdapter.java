package com.example.placesnearme.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.placesnearme.Common;
import com.example.placesnearme.Model.Firebase.DanhMuc;
import com.example.placesnearme.R;
import com.example.placesnearme.View.SearchResultActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

class ListDanhMucViewHolder extends RecyclerView.ViewHolder{
    TextView txtTenDanhMuc;
    ImageView imgDanhMuc;
    CardView cardView;

    ListDanhMucViewHolder(@NonNull View itemView) {
        super(itemView);

        txtTenDanhMuc = itemView.findViewById(R.id.txtTenDanhMuc);
        imgDanhMuc = itemView.findViewById(R.id.imgDanhMuc);
        cardView = itemView.findViewById(R.id.cardViewCategory);
    }
}

public class ListDanhMucAdapter extends RecyclerView.Adapter<ListDanhMucViewHolder>{
    private List<DanhMuc> danhMucList;
    private Context context;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public ListDanhMucAdapter(List<DanhMuc> danhMucList) {
        this.danhMucList = danhMucList;
    }

    @NonNull
    @Override
    public ListDanhMucViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_category, parent, false);

        return new ListDanhMucViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListDanhMucViewHolder holder, final int position) {
        final DanhMuc danhMuc = danhMucList.get(position);

        holder.txtTenDanhMuc.setText(danhMuc.getTendanhmuc());

        StorageReference storageImgProductType = FirebaseStorage.getInstance().getReference().child(Common.DANHMUC)
                .child(danhMuc.getHinhanh());

        long ONE_MEGABYTE = 1024 * 1024;
        storageImgProductType.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.imgDanhMuc.setImageBitmap(bitmap);
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pref = context.getSharedPreferences(Common.PREF_DANHMUC, 0); // 0 - for private mode
                editor = pref.edit();

                editor.putString(Common.madanhmuc, danhMuc.getMadanhmuc());
                editor.putString(Common.tendanhmuc, danhMuc.getTendanhmuc());
                editor.commit();

                Intent intent = new Intent(context, SearchResultActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return danhMucList.size();
    }
}