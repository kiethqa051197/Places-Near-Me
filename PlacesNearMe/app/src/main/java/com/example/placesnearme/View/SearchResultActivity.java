package com.example.placesnearme.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.placesnearme.Adapter.ListDiaDiemTimKiemAdapter;
import com.example.placesnearme.Model.Firebase.DanhMuc;
import com.example.placesnearme.Model.Firebase.DiaDiem;
import com.example.placesnearme.Model.PolylineData;
import com.example.placesnearme.R;
import com.example.placesnearme.Remote.SortDiaDiem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener,
        GoogleMap.OnPolylineClickListener, AdapterView.OnItemSelectedListener{

    private ImageView imgBack;
    private TextView txtTenDanhMuc;

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;

    private Spinner spinContextSapXep, spinContextKieuXem;

    private GeoApiContext mGeoApiContext;

    private FirebaseFirestore db;

    private Marker mMarker;

    private RecyclerView listDiaDiemTimKiem;
    private ListDiaDiemTimKiemAdapter adapterDiaDiemTimKiem;
    private RecyclerView.LayoutManager layoutManagerDanhMuc;
    private Marker mSelectedMarker = null;

    private ArrayAdapter<String> arrayAdapterSapXep, arrayAdapterKieuXem;

    private List<DiaDiem> diaDiems = new ArrayList<>();
    private ArrayList<Marker> mTripMarkers = new ArrayList<>();
    private ArrayList<PolylineData> mPolylinesData = new ArrayList<>();
    private List<String> kieuxems, sapxeps;

    private SharedPreferences prefCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        db = FirebaseFirestore.getInstance();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(mGeoApiContext == null){
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_key))
                    .build();
        }

        imgBack = findViewById(R.id.imgBack);
        txtTenDanhMuc = findViewById(R.id.txtTenDanhMuc);

        prefCategory = getSharedPreferences("prefDanhMuc", 0);
        txtTenDanhMuc.setText(prefCategory.getString("tenDanhMuc", ""));

        spinContextKieuXem = findViewById(R.id.spinContextKieuXem);
        spinContextSapXep = findViewById(R.id.spinContextSapXep);

        kieuxems = Arrays.asList(getResources().getStringArray(R.array.array_kieuxem));
        arrayAdapterKieuXem = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, kieuxems);
        spinContextKieuXem.setAdapter(arrayAdapterKieuXem);
        arrayAdapterKieuXem.notifyDataSetChanged();

        sapxeps = Arrays.asList(getResources().getStringArray(R.array.array_sapxep));
        arrayAdapterSapXep = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, sapxeps);
        spinContextSapXep.setAdapter(arrayAdapterSapXep);
        arrayAdapterSapXep.notifyDataSetChanged();

        listDiaDiemTimKiem = findViewById(R.id.recyclerTimKiem);
        listDiaDiemTimKiem.setHasFixedSize(true);
        layoutManagerDanhMuc = new LinearLayoutManager(getApplicationContext());
        listDiaDiemTimKiem.setLayoutManager(layoutManagerDanhMuc);

        danhsach(prefCategory.getString("maDanhMuc", ""));

        spinContextSapXep.setOnItemSelectedListener(this);
        spinContextKieuXem.setOnItemSelectedListener(this);

        imgBack.setOnClickListener(this);
    }

    private void danhsach(String madanhmuc){
        if (mMap != null)
            mMap.clear();

        db.collection("Dia Diem").whereArrayContains("danhmuc", madanhmuc).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            DiaDiem diaDiem = documentSnapshot.toObject(DiaDiem.class);

                            Location locationCurrent = new Location("Location Current");
                            locationCurrent.setLatitude(MainActivity.latitude);
                            locationCurrent.setLongitude(MainActivity.longtitude);

                            Location locationSelected = new Location("Location Selected");
                            locationSelected.setLatitude(diaDiem.getLocation().getLatitude());
                            locationSelected.setLongitude(diaDiem.getLocation().getLongitude());

                            final double distance = locationCurrent.distanceTo(locationSelected) / 1000;

                            if(distance < 5){
                                com.google.android.gms.maps.model.LatLng latLng = new com.google.android.gms.maps.model.LatLng(diaDiem.getLocation().getLatitude(), diaDiem.getLocation().getLongitude());

                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.title(diaDiem.getTendiadiem());
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                                mMap.addMarker(markerOptions);

                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13), 4000, null);

                                diaDiems.add(diaDiem);
                            }
                        }

                        Collections.sort(diaDiems, new SortDiaDiem());

                        adapterDiaDiemTimKiem = new ListDiaDiemTimKiemAdapter(diaDiems, MainActivity.latitude, MainActivity.longtitude);
                        listDiaDiemTimKiem.setAdapter(adapterDiaDiemTimKiem);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.imgBack:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.spinContextKieuXem:
                if(position == 0)
                    listDiaDiemTimKiem.setVisibility(View.VISIBLE);
                else
                    listDiaDiemTimKiem.setVisibility(View.GONE);

                break;
            case R.id.spinContextSapXep:
                if (position == 0){
                    if (adapterDiaDiemTimKiem != null){
                        Collections.sort(diaDiems, new SortDiaDiem());
                        adapterDiaDiemTimKiem.notifyDataSetChanged();
                    }
                }else {
                    if (adapterDiaDiemTimKiem != null){
                        Collections.sort(diaDiems, new SortDiaDiem());
                        Collections.reverse(diaDiems);
                        adapterDiaDiemTimKiem.notifyDataSetChanged();
                    }
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        int index = 0;
        for(PolylineData polylineData: mPolylinesData){
            index++;

            if(polyline.getId().equals(polylineData.getPolyline().getId())){
                polylineData.getPolyline().setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                polylineData.getPolyline().setZIndex(1);

                com.google.android.gms.maps.model.LatLng endLocation = new LatLng(
                        polylineData.getLeg().endLocation.lat,
                        polylineData.getLeg().endLocation.lng
                );

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(endLocation)
                        .title("Trip #" + index)
                        .snippet("Duration: " + polylineData.getLeg().duration)
                );

                marker.showInfoWindow();

                mTripMarkers.add(marker);
            }
            else{
                polylineData.getPolyline().setColor(ContextCompat.getColor(getApplicationContext(), R.color.grayduration));
                polylineData.getPolyline().setZIndex(0);
            }
        }
    }

    private void removeTripMarkers(){
        for (Marker marker : mTripMarkers){
            marker.remove();
        }
    }

    private void resetSelectedMarker(){
        if (mSelectedMarker != null){
            mSelectedMarker.setVisible(true);
            mSelectedMarker = null;
            removeTripMarkers();
        }
    }

    private void calculateDirections(Marker marker){
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );

        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.alternatives(true);
        directions.origin(new com.google.maps.model.LatLng(MainActivity.latitude, MainActivity.longtitude));

        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
            }
        });
    }

    private void addPolylinesToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (mPolylinesData.size() > 0){
                    for (PolylineData polylineData : mPolylinesData)
                        polylineData.getPolyline().remove();

                    mPolylinesData.clear();
                    mPolylinesData = new ArrayList<>();
                }

                double duration = 99999999;

                for(DirectionsRoute route: result.routes){
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<com.google.android.gms.maps.model.LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

                        newDecodedPath.add(new com.google.android.gms.maps.model.LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }

                    Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(getApplicationContext(), R.color.grayduration));
                    polyline.setClickable(true);
                    mPolylinesData.add(new PolylineData(polyline, route.legs[0]));

                    double tempDuration = route.legs[0].duration.inSeconds;
                    if (tempDuration < duration){
                        duration = tempDuration;
                        onPolylineClick(polyline);
                        zoomRoute(polyline.getPoints());
                    }

                    mSelectedMarker.setVisible(false);
                }
            }
        });
    }

    private void zoomRoute(List<com.google.android.gms.maps.model.LatLng> lstLatLngRoute) {
        if (mMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (com.google.android.gms.maps.model.LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 120;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),600,null);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng latLng = new LatLng(MainActivity.latitude, MainActivity.longtitude);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("This is you")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        mMarker = mMap.addMarker(markerOptions);

        //Move Camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16), 5000, null);

        //Init Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                mMap.setMyLocationEnabled(true);
        } else
            mMap.setMyLocationEnabled(true);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {
                if(marker.getTitle().contains("Trip #")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(SearchResultActivity.this);
                    builder.setMessage("Open Google Maps?")
                            .setCancelable(true)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                    String latitude = String.valueOf(marker.getPosition().latitude);
                                    String longitude = String.valueOf(marker.getPosition().longitude);
                                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                    mapIntent.setPackage("com.google.android.apps.maps");

                                    try{
                                        if (mapIntent.resolveActivity(getApplication().getPackageManager()) != null)
                                            startActivity(mapIntent);
                                    }catch (NullPointerException e){
                                        Toast.makeText(getApplication(), "Couldn't open map", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                }else {
                    if(marker.getTitle().equals("This is you"))
                        marker.hideInfoWindow();
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(SearchResultActivity.this);
                        builder.setMessage("Ban co muon xem duong di den: " + marker.getTitle() + " khong ?")
                                .setCancelable(true)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                        resetSelectedMarker();
                                        mSelectedMarker = marker;
                                        calculateDirections(marker);
                                        dialog.dismiss();
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                return false;
            }
        });

        mMap.setOnPolylineClickListener(this);
    }
}
