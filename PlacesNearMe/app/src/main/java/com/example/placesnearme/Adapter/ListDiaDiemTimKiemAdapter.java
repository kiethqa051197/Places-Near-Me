package com.example.placesnearme.Adapter;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placesnearme.Model.Firebase.DiaDiem;
import com.example.placesnearme.Model.Firebase.Review;
import com.example.placesnearme.R;
import com.example.placesnearme.View.DetailPlacesActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

class ListDiaDiemTimKiemViewHolder extends RecyclerView.ViewHolder{
    TextView txtTenDiaDiem, txtKm, txtDiaChi;
    ImageView imgDanhMuc;
    CardView cardViewPlaces;
    RatingBar rating;
    FirebaseFirestore db;
    List<Review> reviews = new ArrayList<>();

    public ListDiaDiemTimKiemViewHolder(@NonNull View itemView) {
        super(itemView);

        txtTenDiaDiem = itemView.findViewById(R.id.txtTenDiaDiem);
        txtKm = itemView.findViewById(R.id.txtKm);
        txtDiaChi = itemView.findViewById(R.id.txtDiaChi);

        imgDanhMuc = itemView.findViewById(R.id.imgDanhMuc);
        cardViewPlaces = itemView.findViewById(R.id.cardViewPlaces);

        rating = itemView.findViewById(R.id.rating);

        db = FirebaseFirestore.getInstance();
    }
}

public class ListDiaDiemTimKiemAdapter extends RecyclerView.Adapter<ListDiaDiemTimKiemViewHolder>{
    private List<DiaDiem> diaDiemList;
    private double lat;
    private double lng;
    private Context context;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public ListDiaDiemTimKiemAdapter(List<DiaDiem> diaDiemList, double lat, double lng) {
        this.diaDiemList = diaDiemList;
        this.lat = lat;
        this.lng = lng;
    }

    @NonNull
    @Override
    public ListDiaDiemTimKiemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_places, parent, false);

        return new ListDiaDiemTimKiemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListDiaDiemTimKiemViewHolder holder, final int position) {
        final DiaDiem diaDiem = diaDiemList.get(position);

        holder.txtTenDiaDiem.setText(diaDiem.getTendiadiem());
        holder.txtDiaChi.setText(diaDiem.getDiachi());

        Location locationCurrent = new Location("Location Current");
        locationCurrent.setLatitude(lat);
        locationCurrent.setLongitude(lng);

        Location locationSelected = new Location("Location Selected");
        locationSelected.setLatitude(diaDiem.getLocation().getLatitude());
        locationSelected.setLongitude(diaDiem.getLocation().getLongitude());

        final double distance = locationCurrent.distanceTo(locationSelected) / 1000;
        holder.txtKm.setText(String.format("%.1f km", distance));

        if(diaDiem.getHinhanh().size() > 0){
            if (diaDiem.getHinhanh().get(0).substring(0, diaDiem.getHinhanh().get(0).indexOf(":")).equals("https")){
                Picasso.get()
                        .load(diaDiem.getHinhanh().get(0))
                        .placeholder(R.drawable.img_loading)
                        .into(holder.imgDanhMuc);
            }else {
                StorageReference storageImgProductType = FirebaseStorage.getInstance().getReference().child("Images")
                        .child(diaDiem.getMadiadiem()).child(diaDiem.getHinhanh().get(0));

                long ONE_MEGABYTE = 1024 * 1024;
                storageImgProductType.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        holder.imgDanhMuc.setImageBitmap(bitmap);
                    }
                });
            }
        }

        holder.db.collection("Danh Gia").document(diaDiem.getMadiadiem()).collection("Reviews").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()) {
                    Review review = doc.toObject(Review.class);

                    holder.reviews.add(review);
                }

                int tong = 0;

                for (int i = 0; i < holder.reviews.size(); i++){
                    tong += Integer.parseInt(holder.reviews.get(i).getDanhgia());
                }

                if (tong != 0){
                    float trungbinhdanhgia = tong / holder.reviews.size();

                    holder.rating.setRating(trungbinhdanhgia);
                }else
                    holder.rating.setRating(0);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage() + "", Toast.LENGTH_SHORT).show();
            }
        });

        holder.cardViewPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pref = context.getSharedPreferences("prefMaDiaDiem", 0); // 0 - for private mode
                editor = pref.edit();

                editor.putString("maDiaDiem", diaDiem.getMadiadiem());
                editor.commit();

                Intent intent = new Intent(context, DetailPlacesActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return diaDiemList.size();
    }
}