package com.example.placesnearme.View.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placesnearme.Adapter.ListDiaDiemTimKiemAdapter;
import com.example.placesnearme.Model.Firebase.DiaDiem;
import com.example.placesnearme.Model.PolylineData;
import com.example.placesnearme.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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
import java.util.List;

public class SearchFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnPolylineClickListener {

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;

    private EditText edSearch;
    private ImageView imgSearch;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;

    private static Location mLastLocation;
    private GeoApiContext mGeoApiContext;

    private double latitude, longtitude;

    private static final int MY_PERMISSION_CODE = 1000;

    private FirebaseFirestore db;

    private Marker mMarker;


    private RecyclerView listDiaDiemTimKiem;
    private ListDiaDiemTimKiemAdapter adapterDiaDiemTimKiem;
    private RecyclerView.LayoutManager layoutManagerDanhMuc;
    private Marker mSelectedMarker = null;

    private List<DiaDiem> diaDiems = new ArrayList<>();
    private ArrayList<Marker> mTripMarkers = new ArrayList<>();
    private ArrayList<PolylineData> mPolylinesData = new ArrayList<>();

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

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {
                if(marker.getTitle().contains("Trip #")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                                        if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null)
                                            startActivity(mapIntent);
                                    }catch (NullPointerException e){
                                        Toast.makeText(getActivity(), "Couldn't open map", Toast.LENGTH_SHORT).show();
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

                            Location locationCurrent = new Location("Location Current");
                            locationCurrent.setLatitude(latitude);
                            locationCurrent.setLongitude(longtitude);

                            Location locationSelected = new Location("Location Selected");
                            locationSelected.setLatitude(diaDiem.getLocation().getLatitude());
                            locationSelected.setLongitude(diaDiem.getLocation().getLongitude());

                            final double distance = locationCurrent.distanceTo(locationSelected) / 1000;

                            if (distance <= 5){
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

                if (mMarker != null)
                    mMarker.remove();

                latitude = mLastLocation.getLatitude();
                longtitude = mLastLocation.getLongitude();

                LatLng latLng = new LatLng(latitude, longtitude);
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title("This is you")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                mMarker = mMap.addMarker(markerOptions);

                //Move Camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16), 5000, null);

                if(mGeoApiContext == null){
                    mGeoApiContext = new GeoApiContext.Builder()
                            .apiKey(getString(R.string.google_maps_key))
                            .build();
                }
            }
        };
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

    @Override
    public void onPolylineClick(Polyline polyline) {
        int index = 0;
        for(PolylineData polylineData: mPolylinesData){
            index++;

            if(polyline.getId().equals(polylineData.getPolyline().getId())){
                polylineData.getPolyline().setColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
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
                polylineData.getPolyline().setColor(ContextCompat.getColor(getContext(), R.color.grayduration));
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
        directions.origin(new com.google.maps.model.LatLng(
                        latitude,
                        longtitude
                )
        );

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
                    polyline.setColor(ContextCompat.getColor(getContext(), R.color.grayduration));
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
}