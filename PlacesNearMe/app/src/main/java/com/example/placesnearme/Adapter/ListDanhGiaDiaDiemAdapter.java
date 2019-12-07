package com.example.placesnearme.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placesnearme.Model.Firebase.Review;
import com.example.placesnearme.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

class ListDanhGiaDiaDiemViewHolder extends RecyclerView.ViewHolder{
    CircleImageView imgAvaUserReview;
    TextView txtUserName, txtReview;
    RatingBar ratingBarReview;
    ImageView imgCamXuc;

    public ListDanhGiaDiaDiemViewHolder(@NonNull View itemView) {
        super(itemView);

        imgAvaUserReview = itemView.findViewById(R.id.imgAvaUserReview);

        txtUserName = itemView.findViewById(R.id.txtUserName);
        txtReview = itemView.findViewById(R.id.txtReview);

        ratingBarReview = itemView.findViewById(R.id.ratingBarReview);

        imgCamXuc = itemView.findViewById(R.id.imgCamXuc);
    }
}

public class ListDanhGiaDiaDiemAdapter extends RecyclerView.Adapter<ListDanhGiaDiaDiemViewHolder>{
    List<Review> reviews;
    String madiadiem;

    public ListDanhGiaDiaDiemAdapter(List<Review> reviews, String madiadiem) {
        this.reviews = reviews;
        this.madiadiem = madiadiem;
    }

    @NonNull
    @Override
    public ListDanhGiaDiaDiemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_review, parent, false);

        return new ListDanhGiaDiaDiemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListDanhGiaDiaDiemViewHolder holder, final int position) {
        Review review = reviews.get(position);

        holder.txtUserName.setText(review.getTennguoireview());
        holder.txtReview.setText(review.getNoidungreview());

        float danhgia = Float.parseFloat(review.getDanhgia());

        holder.ratingBarReview.setRating(danhgia);

        if (danhgia > 4)
            holder.imgCamXuc.setImageResource(R.drawable.icon_happy_face);
        else if (danhgia < 2)
            holder.imgCamXuc.setImageResource(R.drawable.icon_sad_face);
        else
            holder.imgCamXuc.setImageResource(R.drawable.icon_normal_face);

        if (review.getHinhanhnguoireview().substring(0, review.getHinhanhnguoireview().indexOf(":")).equals("https")){
            Picasso.get()
                    .load(review.getHinhanhnguoireview())
                    .placeholder(R.drawable.img_loading)
                    .into(holder.imgAvaUserReview);
        }else {
            StorageReference storageImgProductType = FirebaseStorage.getInstance().getReference().child("Images")
                    .child(madiadiem).child(review.getHinhanhnguoireview());

            long ONE_MEGABYTE = 1024 * 1024;
            storageImgProductType.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    holder.imgAvaUserReview.setImageBitmap(bitmap);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }
}