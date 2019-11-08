package com.example.placesnearme.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placesnearme.Common;
import com.example.placesnearme.Model.DiaDiem;
import com.example.placesnearme.R;
import com.example.placesnearme.View.ResultListAndMapActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

class ListItemViewHolderDiaDiem extends RecyclerView.ViewHolder{
    public TextView txtTenDiaDiem, txtDuration, txtDiaChi;
    public CircleImageView imgLocation;
    public CardView cardView;

    public ListItemViewHolderDiaDiem(@NonNull View itemView) {
        super(itemView);

        txtTenDiaDiem = itemView.findViewById(R.id.txtPlacesName);
        txtDuration = itemView.findViewById(R.id.txtKm);
        txtDiaChi = itemView.findViewById(R.id.txtDetailLocation);
        imgLocation = itemView.findViewById(R.id.imgCategory);
        cardView = itemView.findViewById(R.id.cardViewPlaces);
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

        holder.txtTenDiaDiem.setText(diaDiem.getTendiadiem());
        holder.txtDiaChi.setText(diaDiem.getDiachi());

        Location locationCurrent = new Location("Location Current");
        locationCurrent.setLatitude(resultListAndMapActivity.latitude);
        locationCurrent.setLongitude(resultListAndMapActivity.longtitude);

        Location locationSelected = new Location("Location Selected");
        locationSelected.setLatitude(diaDiem.getLocation().getLatitude());
        locationSelected.setLongitude(diaDiem.getLocation().getLongitude());

        final double distance = locationCurrent.distanceTo(locationSelected) / 1000;

        holder.txtDuration.setText(String.format("%.1f km", distance));
    }

    @Override
    public int getItemCount() {
        return diaDiemList.size();
    }
}
