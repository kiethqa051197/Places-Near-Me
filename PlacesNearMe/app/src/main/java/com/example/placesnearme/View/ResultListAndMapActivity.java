package com.example.placesnearme.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.placesnearme.Adapter.ListItemDiaDiemAdapter;
import com.example.placesnearme.Common;
import com.example.placesnearme.Interface.IGoogleAPIService;
import com.example.placesnearme.Model.DanhMuc;
import com.example.placesnearme.Model.DanhMucCha;
import com.example.placesnearme.Model.DiaDiem;
import com.example.placesnearme.Model.MyPlaces;
import com.example.placesnearme.Model.PlaceDetail;
import com.example.placesnearme.Model.PolylineData;
import com.example.placesnearme.Model.Results;
import com.example.placesnearme.Remote.SortAscending;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultListAndMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener{

    private static final int MY_PERMISSION_CODE = 1000;

    private ActionBar actionBar;
    private RelativeLayout rl_cardDetails;
    private ImageView imgCategory;
    private TextView txtCountReview, txtPlacesName, txtKm, txtDetailLocation;
    private RatingBar ratingBar;

    private RecyclerView listItemDiaDiem;
    private RecyclerView.LayoutManager layoutManagerDiaDiem;
    private ListItemDiaDiemAdapter adapterDiaDiem;

    private ArrayList<Marker> mTripMarkers = new ArrayList<>();
    private ArrayList<PolylineData> mPolylinesData = new ArrayList<>();
    private List<DiaDiem> diaDiemList = new ArrayList<>();

    public FirebaseFirestore db;

    public SharedPreferences prefCategorySelected, prefLocation, prefPlaces;
    private SharedPreferences.Editor editor, editor2;

    public double latitude, longtitude;

    public static Location mLastLocation;
    private Marker mMarker;

    public IGoogleAPIService mService;
    public SupportMapFragment mapFragment;
    private GoogleMap mMap;
    public MyPlaces currentPlaces;
    private Marker mSelectedMarker = null;
    private GeoApiContext mGeoApiContext;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;

    public PlaceDetail mPlace;

    private boolean filter = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_list_and_map);

        db = FirebaseFirestore.getInstance();

        rl_cardDetails = findViewById(R.id.rl_cardDetails);
        imgCategory = findViewById(R.id.imgCategory);
        txtCountReview = findViewById(R.id.txtCountReview);
        txtPlacesName = findViewById(R.id.txtPlacesName);
        txtKm = findViewById(R.id.txtKm);
        txtDetailLocation = findViewById(R.id.txtDetailLocation);
        ratingBar = findViewById(R.id.ratingBar);

        rl_cardDetails.setVisibility(View.GONE);

        //Request Runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            checkLocationPermission();

        buildLocationCallBack();
        buildLocationRequest();

        enableGPS(mLocationRequest);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

        prefCategorySelected = getSharedPreferences("shareDanhMucSelected", 0);
        prefLocation = getSharedPreferences("shareLastLocation", 0);

        mService = Common.getGoogleAPIService();

        actionBar = getSupportActionBar();
        actionBar.setTitle(prefCategorySelected.getString("tendanhmuc", ""));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        latitude = Double.parseDouble(prefLocation.getString("latitude", "0"));
        longtitude = Double.parseDouble(prefLocation.getString("longtitude", "0"));

        nearByPlace(prefCategorySelected.getString("madanhmuc", ""));

        listItemDiaDiem = findViewById(R.id.listDiaDiem);
        listItemDiaDiem.setHasFixedSize(true);
        layoutManagerDiaDiem = new LinearLayoutManager(this);
        listItemDiaDiem.setLayoutManager(layoutManagerDiaDiem);
    }

    private String getUrl(double latitude, double longtitude, String placeType) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longtitude);
        googlePlacesUrl.append("&radius=" + 5000);
        googlePlacesUrl.append("&type=" + placeType);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + getResources().getString(R.string.google_maps_key));

        return googlePlacesUrl.toString();
    }

    public void nearByPlace(final String placeType) {
        String url = getUrl(latitude, longtitude, placeType);

        diaDiemList.removeAll(diaDiemList);

        mService.getNearByPlaces(url)
                .enqueue(new Callback<MyPlaces>() {
                    @Override
                    public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {
                        currentPlaces = response.body();

                        if (response.isSuccessful()) {
                            for (int i = 0; i < response.body().getResults().length; i++) {
                                Results googlePlaces = response.body().getResults()[i];

                                String placeId = googlePlaces.getPlace_id();
                                String placesName = googlePlaces.getName();
                                String vicinity = googlePlaces.getVicinity();

                                double lat = Double.parseDouble(googlePlaces.getGeometry().getLocation().getLat());
                                double lng = Double.parseDouble(googlePlaces.getGeometry().getLocation().getLng());

                                String[] types = googlePlaces.getTypes();

                                GeoPoint location = new GeoPoint(lat, lng);

                                List<String> danhMuc = new ArrayList<>();
                                final List<String> hinhAnh = new ArrayList<>();

                                for (int j = 0; j < types.length; j++)
                                    danhMuc.add(types[j]);

                                for (int l = 0; l < danhMuc.size(); l++) {
                                    if (danhMuc.get(l).equals("point_of_interest"))
                                        danhMuc.remove(l);
                                    if (danhMuc.get(l).equals("establishment"))
                                        danhMuc.remove(l);
                                }

                                DiaDiem diaDiem = new DiaDiem();
                                diaDiem.setMadiadiem(placeId);
                                diaDiem.setTendiadiem(placesName);
                                diaDiem.setDiachi(vicinity);
                                diaDiem.setDanhmuc(danhMuc);
                                diaDiem.setHinhAnh(hinhAnh);
                                diaDiem.setLocation(location);

                                diaDiemList.add(diaDiem);

                                LatLng latLng = new LatLng(lat, lng);

                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.title(placesName);

                                mMap.addMarker(markerOptions);

                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13), 4000, null);
                            }

                            adapterDiaDiem = new ListItemDiaDiemAdapter(ResultListAndMapActivity.this, diaDiemList);
                            listItemDiaDiem.setAdapter(adapterDiaDiem);
                        }
                    }

                    @Override
                    public void onFailure(Call<MyPlaces> call, Throwable t) {
                    }
                });
    }

    public String getPlaceDetailUrl(String place_id) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        url.append("place_id=" + place_id);
        url.append("&key=" + getResources().getString(R.string.google_maps_key));
        return url.toString();
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

                prefLocation = getApplicationContext().getSharedPreferences("shareLastLocation", 0);
                editor = prefLocation.edit();

                editor.putString("latitude", String.valueOf(mLastLocation.getLatitude()));
                editor.putString("longtitude", String.valueOf(mLastLocation.getLongitude()));
                editor.commit();

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

    private void enableGPS(LocationRequest locationRequest) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(ResultListAndMapActivity.this);

        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
        task.addOnSuccessListener(ResultListAndMapActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //Toast.makeText(MainActivity.this, "Granted", Toast.LENGTH_SHORT).show();
            }
        });

        task.addOnFailureListener(ResultListAndMapActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(ResultListAndMapActivity.this, 51);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.headermenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.header_menu_filter:
                Collections.sort(diaDiemList, new SortAscending());
                adapterDiaDiem.notifyDataSetChanged();
                break;
            case R.id.header_menu_list:
                listItemDiaDiem.setVisibility(View.VISIBLE);
                rl_cardDetails.setVisibility(View.GONE);
                break;
            case R.id.header_menu_map:
                listItemDiaDiem.setVisibility(View.GONE);
                break;
            default:break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Init Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                mMap.setMyLocationEnabled(true);
        } else
            mMap.setMyLocationEnabled(true);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {
                if(marker.getTitle().contains("Trip #")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ResultListAndMapActivity.this);
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
                                        if (mapIntent.resolveActivity(ResultListAndMapActivity.this.getPackageManager()) != null)
                                            startActivity(mapIntent);
                                    }catch (NullPointerException e){
                                        Toast.makeText(ResultListAndMapActivity.this, "Couldn't open map", Toast.LENGTH_SHORT).show();
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(ResultListAndMapActivity.this);
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
                final List<DanhMuc> danhMucs = new ArrayList<>();

                for (int i = 0; i < diaDiemList.size(); i++){
                    if (diaDiemList.get(i).getLocation().getLatitude() == marker.getPosition().latitude
                            && diaDiemList.get(i).getLocation().getLongitude() == marker.getPosition().longitude){

                        txtPlacesName.setText(diaDiemList.get(i).getTendiadiem());
                        txtDetailLocation.setText(diaDiemList.get(i).getDiachi());

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
                                        imgCategory.setImageBitmap(bitmap);
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });

                        mService.getDetaislPlaces(getPlaceDetailUrl(diaDiemList.get(i).getMadiadiem()))
                                .enqueue(new Callback<PlaceDetail>() {
                                    @Override
                                    public void onResponse(Call<PlaceDetail> call, Response<PlaceDetail> response) {
                                        mPlace = response.body();

                                        txtCountReview.setText(mPlace.getResult().getUser_ratings_total() + " people rating");

                                        if (mPlace.getResult().getRating() != null){
                                            ratingBar.setStepSize(0.01f);
                                            ratingBar.setRating(Float.parseFloat(mPlace.getResult().getRating()));
                                            ratingBar.invalidate();
                                        }else {
                                            txtCountReview.setText("0 people rating");
                                            ratingBar.setRating(0);
                                            ratingBar.invalidate();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<PlaceDetail> call, Throwable t) {

                                    }
                                });

                        Location locationCurrent = new Location("Location Current");
                        locationCurrent.setLatitude(latitude);
                        locationCurrent.setLongitude(longtitude);

                        Location locationSelected = new Location("Location Selected");
                        locationSelected.setLatitude(marker.getPosition().latitude);
                        locationSelected.setLongitude(marker.getPosition().longitude);

                        final double distance = locationCurrent.distanceTo(locationSelected) / 1000;

                        txtKm.setText(String.format("%.1f km", distance));

                        final String madiadiem = diaDiemList.get(i).getMadiadiem();

                        rl_cardDetails.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                prefPlaces = getSharedPreferences("sharePlaces", 0);
                                editor2 = prefPlaces.edit();

                                editor2.putString("madiadiem", madiadiem);
                                editor2.putString("distance", String.format("%.1f km", distance));
                                editor2.commit();

                                Intent intent = new Intent(ResultListAndMapActivity.this, DetailPlaceActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                }

                rl_cardDetails.setVisibility(View.VISIBLE);

                return false;
            }
        });

        mMap.setOnPolylineClickListener(this);
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        int index = 0;
        for(PolylineData polylineData: mPolylinesData){
            index++;

            if(polyline.getId().equals(polylineData.getPolyline().getId())){
                polylineData.getPolyline().setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                polylineData.getPolyline().setZIndex(1);

                LatLng endLocation = new LatLng(
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

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

                        newDecodedPath.add(new LatLng(
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

    public void zoomRoute(List<LatLng> lstLatLngRoute) {
        if (mMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 120;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),600,null);
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, MY_PERMISSION_CODE);
            else
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, MY_PERMISSION_CODE);
            return false;
        } else
            return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);

                        buildLocationCallBack();
                        buildLocationRequest();

                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    }
                } else
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }
}
