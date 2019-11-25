package com.example.placesnearme.Adapter;

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

import com.example.placesnearme.Model.Firebase.DiaDiem;
import com.example.placesnearme.R;

import java.util.List;

class ListDiaDiemTimKiemViewHolder extends RecyclerView.ViewHolder{
    TextView txtTenDiaDiem, txtKm, txtDiaChi;
    ImageView imgDanhMuc;
    CardView cardViewPlaces;
    RatingBar rating;

    public ListDiaDiemTimKiemViewHolder(@NonNull View itemView) {
        super(itemView);

        txtTenDiaDiem = itemView.findViewById(R.id.txtTenDiaDiem);
        txtKm = itemView.findViewById(R.id.txtKm);
        txtDiaChi = itemView.findViewById(R.id.txtDiaChi);

        imgDanhMuc = itemView.findViewById(R.id.imgDanhMuc);
        cardViewPlaces = itemView.findViewById(R.id.cardViewPlaces);

        rating = itemView.findViewById(R.id.rating);
    }
}

public class ListDiaDiemTimKiemAdapter extends RecyclerView.Adapter<ListDiaDiemTimKiemViewHolder>{
    List<DiaDiem> diaDiemList;
    double lat;
    double lng;

    public ListDiaDiemTimKiemAdapter(List<DiaDiem> diaDiemList, double lat, double lng) {
        this.diaDiemList = diaDiemList;
        this.lat = lat;
        this.lng = lng;
    }

    @NonNull
    @Override
    public ListDiaDiemTimKiemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
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

        holder.rating.setRating(5);
    }

    @Override
    public int getItemCount() {
        return diaDiemList.size();
    }
}