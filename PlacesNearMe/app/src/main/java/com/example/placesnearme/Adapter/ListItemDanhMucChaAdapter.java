package com.example.placesnearme.Adapter;

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

import com.example.placesnearme.Model.DanhMucCha;
import com.example.placesnearme.R;
import com.example.placesnearme.View.CategoryActivity;
import com.example.placesnearme.View.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

        StorageReference storage = FirebaseStorage.getInstance().getReference().child("Danh Muc Cha").child(danhMucCha.getHinhanh());
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
                SharedPreferences pref = mainActivity.getBaseContext().getSharedPreferences("shareDanhMucCha", 0); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();

                editor.putString("madanhmuccha", danhMucCha.getMadanhmuc()); // Storing string
                editor.putString("tendanhmuccha", danhMucCha.getTendanhmuc());
                editor.commit(); // commit changes

                Intent intent = new Intent(mainActivity.getApplication(), CategoryActivity.class);
                mainActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return danhMucChaList.size();
    }
}
