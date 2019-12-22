package com.example.placesnearme.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placesnearme.Common;
import com.example.placesnearme.Interface.ItemClickListener;
import com.example.placesnearme.Model.Firebase.DanhMuc;
import com.example.placesnearme.R;
import com.example.placesnearme.View.AddCategoryActivity;
import com.example.placesnearme.View.SearchResultActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

class ListAddDanhMucViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnCreateContextMenuListener{
    TextView txtTenDanhMuc;
    ImageView imgDanhMuc;

    ItemClickListener itemClickListener;

    ListAddDanhMucViewHolder(@NonNull View itemView) {
        super(itemView);

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);

        txtTenDanhMuc = itemView.findViewById(R.id.txtTenDanhMuc);
        imgDanhMuc = itemView.findViewById(R.id.imgDanhMuc);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select the action");
        menu.add(0, 0, getAdapterPosition(), "Delete");
    }
}

public class ListAddDanhMucAdapter extends RecyclerView.Adapter<ListAddDanhMucViewHolder>{
    private List<DanhMuc> danhMucList;
    private Context context;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private AddCategoryActivity addCategoryActivity;

    public ListAddDanhMucAdapter(List<DanhMuc> danhMucList, AddCategoryActivity addCategoryActivity) {
        this.danhMucList = danhMucList;
        this.addCategoryActivity = addCategoryActivity;
    }

    @NonNull
    @Override
    public ListAddDanhMucViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_category, parent, false);

        return new ListAddDanhMucViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListAddDanhMucViewHolder holder, final int position) {
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

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                addCategoryActivity.edTenDanhMuc.setText(danhMuc.getTendanhmuc());

                String text = "";
                for (int i = 0; i < danhMuc.getTukhoa().size(); i++)
                   text += danhMucList.get(position).getTukhoa().get(i) + ", ";

                addCategoryActivity.edNhapTuKhoa.setText(text);
                addCategoryActivity.hinhanh = danhMuc.getHinhanh();

                addCategoryActivity.isUpdate = true;
                addCategoryActivity.idUpdate = danhMuc.getMadanhmuc();
            }
        });
    }

    @Override
    public int getItemCount() {
        return danhMucList.size();
    }
}