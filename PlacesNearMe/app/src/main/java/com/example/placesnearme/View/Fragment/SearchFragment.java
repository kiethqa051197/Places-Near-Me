package com.example.placesnearme.View.Fragment;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placesnearme.Adapter.ListDiaDiemTimKiemAdapter;
import com.example.placesnearme.Model.Firebase.DiaDiem;
import com.example.placesnearme.R;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;

    private EditText edSearch;
    private ImageView imgSearch;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;

    public static Location mLastLocation;

    public double latitude, longtitude;

    private static final int MY_PERMISSION_CODE = 1000;

    private FirebaseFirestore db;

    private RecyclerView listDiaDiemTimKiem;
    private ListDiaDiemTimKiemAdapter adapterDiaDiemTimKiem;
    private RecyclerView.LayoutManager layoutManagerDanhMuc;

    private List<DiaDiem> diaDiems = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        db = FirebaseFirestore.getInstance();

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        edSearch = view.findViewById(R.id.edSearch);
        imgSearch = view.findViewById(R.id.imgSearch);

        //Request Runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            checkLocationPermission();

        buildLocationRequest();
        buildLocationCallBack();

        enableGPS(mLocationRequest);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

        listDiaDiemTimKiem = view.findViewById(R.id.recyclerTimKiem);
        listDiaDiemTimKiem.setHasFixedSize(true);
        layoutManagerDanhMuc = new LinearLayoutManager(getActivity());
        listDiaDiemTimKiem.setLayoutManager(layoutManagerDanhMuc);

        timkiem();

        imgSearch.setOnClickListener(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Init Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                mMap.setMyLocationEnabled(true);
        } else
            mMap.setMyLocationEnabled(true);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.imgSearch:
                //Log.d("ktra", "Click");
                //timkiem();
                break;
        }
    }

    private void timkiem(){
        db.collection("Dia Diem").whereArrayContains("danhmuc", "atm").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            DiaDiem diaDiem = documentSnapshot.toObject(DiaDiem.class);

                            /*
                            Location locationCurrent = new Location("Location Current");
                            locationCurrent.setLatitude(latitude);
                            locationCurrent.setLongiListDiaDiemTimKiemViewHoldertude(longtitude);

                            Location locationSelected = new Location("Location Selected");
                            locationSelected.setLatitude(diaDiem.getLocation().getLatitude());
                            locationSelected.setLongitude(diaDiem.getLocation().getLongitude());

                            final double distance = locationCurrent.distanceTo(locationSelected) / 1000;

                            if (distance <= 5){
                                Log.d("ktra", diaDiem.getTendiadiem() + " - " + distance);
                            }
                             */

                            diaDiems.add(diaDiem);
                        }

                        adapterDiaDiemTimKiem = new ListDiaDiemTimKiemAdapter(diaDiems, latitude, longtitude);
                        listDiaDiemTimKiem.setAdapter(adapterDiaDiemTimKiem);
                    }
                });
    }

    private void buildLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setSmallestDisplacement(10f);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationCallBack() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mLastLocation = locationResult.getLastLocation();

                latitude = mLastLocation.getLatitude();
                longtitude = mLastLocation.getLongitude();
            }
        };
    }

    private void enableGPS(LocationRequest locationRequest) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());

        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
            }
        });

        task.addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(getActivity(), 51);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION))
                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, MY_PERMISSION_CODE);
            else
                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, MY_PERMISSION_CODE);
            return false;
        } else
            return true;
    }
}