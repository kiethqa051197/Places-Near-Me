package com.example.placesnearme.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placesnearme.Model.DanhMuc;
import com.example.placesnearme.Model.DanhMucCha;
import com.example.placesnearme.Model.DiaDiem;
import com.example.placesnearme.Model.PlaceDetail;
import com.example.placesnearme.R;
import com.example.placesnearme.View.ResultListAndMapActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class ListItemViewHolderDiaDiem extends RecyclerView.ViewHolder{
    public TextView txtTenDiaDiem, txtDuration, txtDiaChi, txtCountRating;
    public ImageView imgLocation;
    public CardView cardView;
    public RatingBar ratingBar;

    public ListItemViewHolderDiaDiem(@NonNull View itemView) {
        super(itemView);

        txtTenDiaDiem = itemView.findViewById(R.id.txtPlacesName);
        txtDuration = itemView.findViewById(R.id.txtKm);
        txtDiaChi = itemView.findViewById(R.id.txtDetailLocation);
        imgLocation = itemView.findViewById(R.id.imgCategory);
        cardView = itemView.findViewById(R.id.cardViewPlaces);
        ratingBar = itemView.findViewById(R.id.ratingBar);
        txtCountRating = itemView.findViewById(R.id.txtCountReview);
    }
}

public class ListItemDiaDiemAdapter extends RecyclerView.Adapter<ListItemViewHolderDiaDiem>{
    ResultListAndMapActivity resultListAndMapActivity;
    List<DiaDiem> diaDiemList;
    Context context;

    public ListItemDiaDiemAdapter(ResultListAndMapActivity resultListAndMapActivity, List<DiaDiem> diaDiemList) {
        this.resultListAndMapActivity = resultListAndMapActivity;
        this.diaDiemList = diaDiemList;
    }

    @NonNull
    @Override
    public ListItemViewHolderDiaDiem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(resultListAndMapActivity.getBaseContext());
        View view = inflater.inflate(R.layout.list_item_places, parent, false);
        context = parent.getContext();

        return new ListItemViewHolderDiaDiem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListItemViewHolderDiaDiem holder, final int position) {
        final DiaDiem diaDiem = diaDiemList.get(position);
        final List<DanhMuc> danhMucs = new ArrayList<>();

        holder.txtTenDiaDiem.setText(diaDiem.getTendiadiem());
        holder.txtDiaChi.setText(diaDiem.getDiachi());

        resultListAndMapActivity.mService.getDetaislPlaces(resultListAndMapActivity.getPlaceDetailUrl(diaDiem.getMadiadiem()))
                .enqueue(new Callback<PlaceDetail>() {
                    @Override
                    public void onResponse(Call<PlaceDetail> call, Response<PlaceDetail> response) {
                        resultListAndMapActivity.mPlace = response.body();

                        holder.txtCountRating.setText(resultListAndMapActivity.mPlace.getResult().getUser_ratings_total()
                                + " people rating");

                        if (resultListAndMapActivity.mPlace.getResult().getRating() != null){
                            holder.ratingBar.setMax(5);
                            holder.ratingBar.setStepSize(0.01f);
                            holder.ratingBar.setRating(Float.parseFloat(resultListAndMapActivity.mPlace.getResult().getRating()));
                            holder.ratingBar.invalidate();
                        }else
                            holder.ratingBar.setRating(0);
                    }

                    @Override
                    public void onFailure(Call<PlaceDetail> call, Throwable t) {

                    }
                });

        Location locationCurrent = new Location("Location Current");
        locationCurrent.setLatitude(resultListAndMapActivity.latitude);
        locationCurrent.setLongitude(resultListAndMapActivity.longtitude);

        Location locationSelected = new Location("Location Selected");
        locationSelected.setLatitude(diaDiem.getLocation().getLatitude());
        locationSelected.setLongitude(diaDiem.getLocation().getLongitude());

        final double distance = locationCurrent.distanceTo(locationSelected) / 1000;

        holder.txtDuration.setText(String.format("%.1f km", distance));

        SharedPreferences prefCategorySelected = resultListAndMapActivity
                .getSharedPreferences("shareDanhMucSelected", 0);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Danh Muc").whereEqualTo("madanhmuc", prefCategorySelected.getString("madanhmuc", ""))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()) {
                    DanhMuc danhMuc = new DanhMuc(doc.getString("madanhmuc"),
                            doc.getString("tendanhmuc"), doc.getString("hinhanh"), doc.toObject(DanhMucCha.class));

                    danhMucs.add(danhMuc);
                }

                StorageReference storageImgProductType = FirebaseStorage.getInstance().getReference()
                        .child("Danh Muc").child(danhMucs.get(0).getHinhanh());

                long ONE_MEGABYTE = 1024 * 1024;
                storageImgProductType.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        holder.imgLocation.setImageBitmap(bitmap);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return diaDiemList.size();
    }
}
