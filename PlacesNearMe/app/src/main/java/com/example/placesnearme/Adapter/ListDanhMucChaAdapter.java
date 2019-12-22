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
import com.example.placesnearme.Model.Firebase.DanhMucCha;
import com.example.placesnearme.R;
import com.example.placesnearme.View.CategoryActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

class ListDanhMucChaViewHolder extends RecyclerView.ViewHolder{
    TextView txtTenDanhMuc;
    ImageView imgDanhMuc;
    CardView cardView;

    ListDanhMucChaViewHolder(@NonNull View itemView) {
        super(itemView);

        txtTenDanhMuc = itemView.findViewById(R.id.txtTenDanhMuc);
        imgDanhMuc = itemView.findViewById(R.id.imgDanhMuc);
        cardView = itemView.findViewById(R.id.cardViewCategory);
    }
}

public class ListDanhMucChaAdapter extends RecyclerView.Adapter<ListDanhMucChaViewHolder>{
    private List<DanhMucCha> danhMucChaList;
    private Context context;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public ListDanhMucChaAdapter(List<DanhMucCha> danhMucChaList) {
        this.danhMucChaList = danhMucChaList;
    }

    @NonNull
    @Override
    public ListDanhMucChaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_category, parent, false);

        return new ListDanhMucChaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListDanhMucChaViewHolder holder, final int position) {
        final DanhMucCha danhMucCha = danhMucChaList.get(position);

        holder.txtTenDanhMuc.setText(danhMucCha.getTendanhmuc());

        StorageReference storage = FirebaseStorage.getInstance().getReference().child(Common.DANHMUCCHA).child(danhMucCha.getHinhanh());
        long ONE_MEGABYTE = 1024 * 1024;
        storage.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.imgDanhMuc.setImageBitmap(bitmap);
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pref = context.getSharedPreferences(Common.PREF_DANHMUCCHA, 0); // 0 - for private mode
                editor = pref.edit();

                editor.putString(Common.madanhmuc, danhMucCha.getMadanhmuc());
                editor.putString(Common.tendanhmuc, danhMucCha.getTendanhmuc());
                editor.putString(Common.hinhanh, danhMucCha.getHinhanh());
                editor.commit();

                Intent intent = new Intent(context, CategoryActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return danhMucChaList.size();
    }
}