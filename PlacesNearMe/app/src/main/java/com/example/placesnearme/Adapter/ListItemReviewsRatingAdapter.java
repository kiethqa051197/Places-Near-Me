package com.example.placesnearme.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placesnearme.Model.Reviews;
import com.example.placesnearme.R;
import com.example.placesnearme.View.DetailPlaceActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

class ListItemViewHolderReviewRating extends RecyclerView.ViewHolder{
    public ImageView imgAvaUserReview, imgFace;
    public TextView txtUserName, txtDayReview, txtReview;
    public RatingBar ratingBar;

    public ListItemViewHolderReviewRating(@NonNull View itemView) {
        super(itemView);

        imgAvaUserReview = itemView.findViewById(R.id.imgAvaUserReview);
        imgFace = itemView.findViewById(R.id.imgFace);
        txtUserName = itemView.findViewById(R.id.txtUserName);
        txtDayReview = itemView.findViewById(R.id.txtDayReview);
        txtReview = itemView.findViewById(R.id.txtReview);
        ratingBar = itemView.findViewById(R.id.ratingBar);
    }
}

public class ListItemReviewsRatingAdapter extends RecyclerView.Adapter<ListItemViewHolderReviewRating>{
    DetailPlaceActivity detailPlaceActivity;
    List<Reviews> reviewsList;

    public ListItemReviewsRatingAdapter(DetailPlaceActivity detailPlaceActivity, List<Reviews> reviewsList) {
        this.detailPlaceActivity = detailPlaceActivity;
        this.reviewsList = reviewsList;
    }

    @NonNull
    @Override
    public ListItemViewHolderReviewRating onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(detailPlaceActivity.getBaseContext());
        View view = inflater.inflate(R.layout.list_item_reviews, parent, false);

        return new ListItemViewHolderReviewRating(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListItemViewHolderReviewRating holder, final int position) {
        Reviews reviews = reviewsList.get(position);

        StringBuilder url = new StringBuilder(reviews.getProfile_photo_url());

        Picasso.get()
                .load(String.valueOf(url))
                .placeholder(R.drawable.ava_man)
                .error(R.drawable.ic_error)
                .into(holder.imgAvaUserReview);

        holder.ratingBar.setStepSize(0.01f);
        holder.ratingBar.setRating(Float.parseFloat(reviews.getRating()));
        holder.ratingBar.invalidate();

        if (Integer.parseInt(reviews.getRating()) <= 2)
            holder.imgFace.setImageResource(R.drawable.icon_sad_face);
        else if (Integer.parseInt(reviews.getRating()) >= 4)
            holder.imgFace.setImageResource(R.drawable.icon_happy_face);
        else
            holder.imgFace.setImageResource(R.drawable.icon_normal_face);

        holder.txtUserName.setText(reviews.getAuthor_name());
        holder.txtDayReview.setText(reviews.getRelative_time_description());
        holder.txtReview.setText(reviews.getText());
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }
}
