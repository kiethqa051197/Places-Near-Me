package com.example.placesnearme.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.placesnearme.Adapter.ListItemDanhMucChaAdapter;
import com.example.placesnearme.Common;
import com.example.placesnearme.Interface.IGoogleAPIService;
import com.example.placesnearme.Model.DanhMuc;
import com.example.placesnearme.Model.DanhMucCha;
import com.example.placesnearme.Model.DiaDiem;
import com.example.placesnearme.Model.MyPlaces;
import com.example.placesnearme.Model.Photos;
import com.example.placesnearme.Model.PolylineData;
import com.example.placesnearme.Model.Results;
import com.example.placesnearme.Model.Sortbyroll;
import com.example.placesnearme.R;
import com.example.placesnearme.View.Fragment.HomeFragment;
import com.example.placesnearme.View.Fragment.SettingFragment;
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
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, View.OnClickListener, RadioGroup.OnCheckedChangeListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnPolylineClickListener{

    private View navHeader;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle actionbar;
    private NavigationView navigationView;
    private DrawerLayout drawer;

    // New Location
    private FusedLocationProviderClient fusedLocationProviderClient;

    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;

    public static Location mLastLocation;
    private Marker mMarker;

    private double latitude, longtitude;

    private GoogleMap mMap;
    private GeoApiContext mGeoApiContext;

    private static final int MY_PERMISSION_CODE = 1000;

    private ArrayList<PolylineData> mPolylinesData = new ArrayList<>();
    public IGoogleAPIService mService;
    public MyPlaces currentPlaces;

    private ArrayList<Marker> mTripMarkers = new ArrayList<>();
    private Marker mSelectedMarker = null;

    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        //Navigation
        actionbar = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(actionbar);

        actionbar.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navHeader = navigationView.getHeaderView(0);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mService = Common.getGoogleAPIService();

        //Request Runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            checkLocationPermission();

        buildLocationCallBack();
        buildLocationRequest();

        enableGPS(mLocationRequest);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionbar.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();
        Fragment fragment = null;

        if (id == R.id.nav_home) {
            fragment = new HomeFragment();
            displaySelectedFragment(fragment);
        } else if (id == R.id.nav_setting) {
            fragment = new SettingFragment();
            displaySelectedFragment(fragment);
        }

        drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void displaySelectedFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.hide(mapFragment);

        fragmentTransaction.replace(R.id.frameMap, fragment);
        fragmentTransaction.commit();
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

        mMap.setOnMapClickListener(this);
        mMap.setOnPolylineClickListener(this);
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

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    protected void onStop() {
        fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        super.onDestroy();
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
                        .snippet("Your Position")
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

        SettingsClient settingsClient = LocationServices.getSettingsClient(MainActivity.this);

        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
        task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //Toast.makeText(MainActivity.this, "Granted", Toast.LENGTH_SHORT).show();
            }
        });

        task.addOnFailureListener(MainActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(MainActivity.this, 51);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
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

    public void zoomRoute(List<LatLng> lstLatLngRoute) {
        if (mMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 120;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),600,null);
    }

    private void calculateDirections(Marker marker){
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );

        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.alternatives(true);
        directions.origin(new com.google.maps.model.LatLng(
                        mLastLocation.getLatitude(),
                        mLastLocation.getLongitude()
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

    private String getUrl(double latitude, double longtitude, String placeType) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longtitude);
        googlePlacesUrl.append("&radius=" + 5000);
        googlePlacesUrl.append("&type=" + placeType);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + getResources().getString(R.string.google_maps_key));

        return googlePlacesUrl.toString();
    }

    /*
    public void nearByPlace(final String placeType) {
        mMap.clear();
        String url = getUrl(latitude, longtitude, placeType);

        diaDiemList.removeAll(diaDiemList);

        mService.getNearByPlaces(url)
                .enqueue(new Callback<MyPlaces>() {
                    @Override
                    public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {
                        currentPlaces = response.body();

                        if (response.isSuccessful()) {
                            for (int i = 0; i < response.body().getResults().length; i++) {
                                MarkerOptions markerOptions = new MarkerOptions();
                                Results googlePlaces = response.body().getResults()[i];

                                String placesName = googlePlaces.getName();
                                String vicinity = googlePlaces.getVicinity();

                                double lat = Double.parseDouble(googlePlaces.getGeometry().getLocation().getLat());
                                double lng = Double.parseDouble(googlePlaces.getGeometry().getLocation().getLng());

                                String[] types = googlePlaces.getTypes();
                                Photos[] picture = googlePlaces.getPhotos();

                                GeoPoint location = new GeoPoint(lat, lng);

                                List<String> danhMuc = new ArrayList<>();
                                List<String> hinhAnh = new ArrayList<>();

                                for (int j = 0; j < types.length; j++)
                                    danhMuc.add(types[j]);

                                for (int l = 0; l < danhMuc.size(); l++) {
                                    if (danhMuc.get(l).equals("point_of_interest"))
                                        danhMuc.remove(l);
                                    if (danhMuc.get(l).equals("establishment"))
                                        danhMuc.remove(l);
                                }

                                if (picture != null) {
                                    for (int k = 0; k < picture.length; k++) {
                                        hinhAnh.add(picture[k].getPhoto_reference());
                                    }
                                }

                                DiaDiem diaDiem = new DiaDiem();
                                diaDiem.setTendiadiem(placesName);
                                diaDiem.setDiachi(vicinity);
                                diaDiem.setDanhmuc(danhMuc);
                                diaDiem.setHinhAnh(hinhAnh);
                                diaDiem.setLocation(location);

                                diaDiemList.add(diaDiem);

                                Collections.sort(diaDiemList, new Sortbyroll());

                                LatLng latLng = new LatLng(lat, lng);

                                markerOptions.position(latLng);
                                markerOptions.title(placesName);

                                markerOptions.snippet(String.valueOf(i)); //Assign index for marker

                                mMap.addMarker(markerOptions);

                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13), 4000, null);
                            }

                            adapterLocation = new ListItemLocationAdapter(MainActivity.this, diaDiemList, getApplicationContext());
                            listItemDiaDiem.setAdapter(adapterLocation);
                        }
                    }

                    @Override
                    public void onFailure(Call<MyPlaces> call, Throwable t) {
                    }
                });
    }
     */
}
