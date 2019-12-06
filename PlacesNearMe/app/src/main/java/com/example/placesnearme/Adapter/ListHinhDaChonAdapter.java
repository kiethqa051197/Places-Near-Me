package com.example.placesnearme.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placesnearme.Model.Firebase.DanhMuc;
import com.example.placesnearme.R;

import java.util.List;

class ListHinhDaChonViewHolder extends RecyclerView.ViewHolder{
    public ImageView imgHinhDuocChon, imgXoaHinh;

    public ListHinhDaChonViewHolder(@NonNull View itemView) {
        super(itemView);

        imgHinhDuocChon = itemView.findViewById(R.id.imgHinhDuocChon);
        imgXoaHinh = itemView.findViewById(R.id.imgXoaHinh);
    }
}

public class ListHinhDaChonAdapter extends RecyclerView.Adapter<ListHinhDaChonViewHolder>{
    List<Uri> uriList;
    Context context;

    public ListHinhDaChonAdapter(List<Uri> uriList) {
        this.uriList = uriList;
    }

    @NonNull
    @Override
    public ListHinhDaChonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_image_selected, parent, false);

        return new ListHinhDaChonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListHinhDaChonViewHolder holder, final int position) {
        Uri uri = uriList.get(position);

        holder.imgHinhDuocChon.setImageURI(uri);

        holder.imgXoaHinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    uriList.remove(position);
                    notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return uriList.size();
    }
}