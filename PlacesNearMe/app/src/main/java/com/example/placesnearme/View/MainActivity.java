package com.example.placesnearme.View;

import android.Manifest;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import com.example.placesnearme.Common;
import com.example.placesnearme.Interface.IGoogleAPIService;
import com.example.placesnearme.Model.Firebase.DiaDiem;
import com.example.placesnearme.Model.Firebase.Review;
import com.example.placesnearme.Model.MyPlaces;
import com.example.placesnearme.Model.PlaceDetail;
import com.example.placesnearme.Model.Results;
import com.example.placesnearme.Model.Reviews;
import com.example.placesnearme.View.Fragment.AddPlaceFragment;
import com.example.placesnearme.View.Fragment.CategoryFragment;
import com.example.placesnearme.View.Fragment.ProfileFragment;
import com.example.placesnearme.View.Fragment.SearchFragment;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Looper;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.placesnearme.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private static final int MY_PERMISSION_CODE = 1000;
    public static BottomNavigationView bottomNavigationView;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    public static Location mLastLocation;
    public static double latitude, longtitude;

    private IGoogleAPIService mService;

    private MyPlaces currentPlaces;
    private PlaceDetail mPlace;

    public static FirebaseFirestore db;

    final FragmentManager fm = getSupportFragmentManager();
    final public static Fragment fragment1 = new CategoryFragment();
    final public static Fragment fragment2 = new SearchFragment();
    final public static Fragment fragment3 = new AddPlaceFragment();
    final public static Fragment fragment4 = new ProfileFragment();
    public static Fragment active = fragment1;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private List<String> arrTemp = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mService = Common.getGoogleAPIService();

        db = FirebaseFirestore.getInstance();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.getMenu().findItem(R.id.action_category).setChecked(true);

        fm.beginTransaction().add(R.id.frameLayout,fragment1, "1").commit();
        fm.beginTransaction().add(R.id.frameLayout, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.frameLayout, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.frameLayout, fragment4, "4").hide(fragment4).commit();

        preferences = getSharedPreferences("prefEdit", 0);
        editor = preferences.edit();

        //Request Runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            checkLocationPermission();

        buildLocationRequest();
        buildLocationCallBack();

        enableGPS(mLocationRequest);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

        Calendar c = Calendar.getInstance();
        final int day = c.get(Calendar.DAY_OF_MONTH);

        final List<String> types = Arrays.asList(getResources().getStringArray(R.array.place_type));

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (Exception e) { }
                finally {
                    if (day != 1){
                        for (int i = 0; i < types.size(); i++){
                            //nearByPlaceAddNew(types.get(i));
                        }
                    }else {
                        for (int i = 0; i < types.size(); i++){
                            //nearByPlaceEdit(types.get(i));
                        }
                    }
                }
            }
        });
        thread.start();

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    // Change Fragment
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_category:
                changeFragment(fragment1);
                return true;
            case R.id.action_search:
                changeFragment(fragment2);
                return true;
            case R.id.action_add_place:
                changeFragment(fragment3);
                return true;
            case R.id.action_user:
                changeFragment(fragment4);
                return true;
        }
        return false;
    }

    public void changeFragment(Fragment fragment){
        fm.beginTransaction().hide(active).show(fragment).commit();
        active = fragment;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        buildLocationCallBack();
                        buildLocationRequest();

                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    }
                } else
                    Toast.makeText(this, getString(R.string.tuchoicapquyen), Toast.LENGTH_SHORT).show();
            }
            break;
        }
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

        SettingsClient settingsClient = LocationServices.getSettingsClient(MainActivity.this);

        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
        task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
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

    private void nearByPlaceAddNew(final String placeType) {
        String url = getUrl(latitude, longtitude, placeType);

        mService.getNearByPlaces(url)
                .enqueue(new Callback<MyPlaces>() {
                    @Override
                    public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {
                        currentPlaces = response.body();

                        if (response.isSuccessful()) {
                            for (int i = 0; i < currentPlaces.getResults().length; i++) {
                                Results googlePlaces = currentPlaces.getResults()[i];

                                mService.getDetaislPlaces(getPlaceDetailUrl(googlePlaces.getPlace_id()))
                                        .enqueue(new Callback<PlaceDetail>() {
                                            @Override
                                            public void onResponse(Call<PlaceDetail> call, Response<PlaceDetail> response) {
                                                mPlace = response.body();

                                                String madiadiem = mPlace.getResult().getPlace_id();
                                                String tendiadiem = mPlace.getResult().getName();
                                                String diachi = mPlace.getResult().getVicinity();

                                                String lati = mPlace.getResult().getGeometry().getLocation().getLat();
                                                String longti = mPlace.getResult().getGeometry().getLocation().getLng();
                                                GeoPoint location = new GeoPoint(Double.parseDouble(lati), Double.parseDouble(longti));

                                                String website = mPlace.getResult().getWebsite();
                                                String dienthoai = mPlace.getResult().getFormatted_phone_number();

                                                List<String> danhmuc = new ArrayList<>();
                                                List<String> hinhAnh = new ArrayList<>();
                                                List<Map<String, Object>> thoiGianHoatDong = new ArrayList<>();

                                                if (mPlace.getResult().getTypes() != null){
                                                    for (int j = 0; j < mPlace.getResult().getTypes().length; j++) {
                                                        danhmuc.add(mPlace.getResult().getTypes()[j]);
                                                    }

                                                    for (int l = 0; l < danhmuc.size(); l++) {
                                                        if (danhmuc.get(l).equals("point_of_interest"))
                                                            danhmuc.remove(l);
                                                        if (danhmuc.get(l).equals("establishment"))
                                                            danhmuc.remove(l);
                                                    }
                                                }

                                                if (mPlace.getResult().getPhotos() != null){
                                                    for (int k = 0; k < mPlace.getResult().getPhotos().length; k++)
                                                        hinhAnh.add(getPhotoOfPlace(mPlace.getResult().getPhotos()[k].getPhoto_reference()));
                                                }

                                                if (mPlace.getResult().getOpening_hours() != null){
                                                    int songay = mPlace.getResult().getOpening_hours().getWeekday_text().length;
                                                    for(int i = 0; i < songay; i++) {
                                                        String text = mPlace.getResult().getOpening_hours().getWeekday_text()[i];
                                                        String textCut = text.substring(text.indexOf(":") + 1).trim();

                                                        if(textCut.equals(getString(R.string.dongcua))){
                                                            addBusinessTiming(textCut, textCut, thoiGianHoatDong);
                                                        }else if (textCut.equals(getString(R.string.mocua24h))){
                                                            addBusinessTiming(textCut, textCut, thoiGianHoatDong);
                                                        }else{
                                                            String open = textCut.substring(0, 5);
                                                            String close = textCut.substring(6, 11);

                                                            addBusinessTiming(open, close, thoiGianHoatDong);
                                                        }
                                                    }
                                                }else {
                                                    Map<String, Object> map = new HashMap<>();

                                                    for (int i = 0; i < 7; i++){
                                                        map.put(Common.mocua, getString(R.string.khongcothoigianhoatdong));
                                                        map.put(Common.dongcua, getString(R.string.khongcothoigianhoatdong));

                                                        thoiGianHoatDong.add(map);
                                                    }
                                                }

                                                DiaDiem diaDiem = new DiaDiem();
                                                diaDiem.setMadiadiem(madiadiem);
                                                diaDiem.setTendiadiem(tendiadiem);
                                                diaDiem.setDiachi(diachi);
                                                diaDiem.setLocation(location);
                                                diaDiem.setWebsite(website);
                                                diaDiem.setDienthoai(dienthoai);
                                                diaDiem.setDanhmuc(danhmuc);
                                                diaDiem.setHinhanh(hinhAnh);
                                                diaDiem.setThoigianhoatdong(thoiGianHoatDong);

                                                themDiaDiem(madiadiem, diaDiem);

                                                List<Reviews> reviews = new ArrayList<>();

                                                if(mPlace.getResult().getReviews() != null){
                                                    for (int n = 0; n < mPlace.getResult().getReviews().length; n++)
                                                        reviews.add(mPlace.getResult().getReviews()[n]);

                                                    for (int q = 0; q < reviews.size(); q++) {
                                                        Review review = new Review();
                                                        review.setManguoireview("");
                                                        review.setTennguoireview(reviews.get(q).getAuthor_name());
                                                        review.setDanhgia(reviews.get(q).getRating());
                                                        review.setNoidungreview(reviews.get(q).getText());
                                                        review.setHinhanhnguoireview(reviews.get(q).getProfile_photo_url());

                                                        themReviews(madiadiem, review);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<PlaceDetail> call, Throwable t) {

                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyPlaces> call, Throwable t) {

                    }
                });
    }

    private void addBusinessTiming(String open, String close, List<Map<String, Object>> thoiGianHoatDong){
        Map<String, Object> map = new HashMap<>();

        map.put(Common.mocua, open);
        map.put(Common.dongcua, close);

        thoiGianHoatDong.add(map);
    }

    private void nearByPlaceEdit(final String placeType) {
        String url = getUrl(latitude, longtitude, placeType);

        mService.getNearByPlaces(url)
                .enqueue(new Callback<MyPlaces>() {
                    @Override
                    public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {
                        currentPlaces = response.body();

                        if (response.isSuccessful()) {
                            for (int i = 0; i < currentPlaces.getResults().length; i++) {
                                Results googlePlaces = currentPlaces.getResults()[i];

                                mService.getDetaislPlaces(getPlaceDetailUrl(googlePlaces.getPlace_id()))
                                        .enqueue(new Callback<PlaceDetail>() {
                                            @Override
                                            public void onResponse(Call<PlaceDetail> call, Response<PlaceDetail> response) {
                                                mPlace = response.body();

                                                final String madiadiem = mPlace.getResult().getPlace_id();
                                                final String tendiadiem = mPlace.getResult().getName();
                                                final String diachi = mPlace.getResult().getVicinity();

                                                String lati = mPlace.getResult().getGeometry().getLocation().getLat();
                                                String longti = mPlace.getResult().getGeometry().getLocation().getLng();
                                                final GeoPoint location = new GeoPoint(Double.parseDouble(lati), Double.parseDouble(longti));

                                                final String website = mPlace.getResult().getWebsite();
                                                final String dienthoai = mPlace.getResult().getFormatted_phone_number();

                                                final List<String> danhmuc = new ArrayList<>();
                                                final List<String> hinhAnh = new ArrayList<>();
                                                final List<Map<String, Object>> thoiGianHoatDong = new ArrayList<>();

                                                if (mPlace.getResult().getTypes() != null){
                                                    for (int j = 0; j < mPlace.getResult().getTypes().length; j++) {
                                                        danhmuc.add(mPlace.getResult().getTypes()[j]);
                                                    }

                                                    for (int l = 0; l < danhmuc.size(); l++) {
                                                        if (danhmuc.get(l).equals("point_of_interest"))
                                                            danhmuc.remove(l);
                                                        if (danhmuc.get(l).equals("establishment"))
                                                            danhmuc.remove(l);
                                                    }
                                                }

                                                final Map<String, Object> danhmucMap = new HashMap<>();
                                                danhmucMap.put(Common.danhmuc, danhmuc);

                                                if (mPlace.getResult().getPhotos() != null){
                                                    for (int k = 0; k < mPlace.getResult().getPhotos().length; k++)
                                                        hinhAnh.add(getPhotoOfPlace(mPlace.getResult().getPhotos()[k].getPhoto_reference()));
                                                }

                                                if (mPlace.getResult().getOpening_hours() != null){
                                                    int songay = mPlace.getResult().getOpening_hours().getWeekday_text().length;
                                                    for(int i = 0; i < songay; i++) {
                                                        String text = mPlace.getResult().getOpening_hours().getWeekday_text()[i];
                                                        String textCut = text.substring(text.indexOf(":") + 1).trim();

                                                        if(textCut.equals(getString(R.string.dongcua))){
                                                            addBusinessTiming(textCut, textCut, thoiGianHoatDong);
                                                        }else if (textCut.equals(getString(R.string.mocua24h))){
                                                            addBusinessTiming(textCut, textCut, thoiGianHoatDong);
                                                        }else{
                                                            String open = textCut.substring(0, 5);
                                                            String close = textCut.substring(6, 11);

                                                            addBusinessTiming(open, close, thoiGianHoatDong);
                                                        }
                                                    }
                                                }else {
                                                    Map<String, Object> map = new HashMap<>();

                                                    for (int i = 0; i < 7; i++){
                                                        map.put(Common.mocua, getString(R.string.khongcothoigianhoatdong));
                                                        map.put(Common.dongcua, getString(R.string.khongcothoigianhoatdong));

                                                        thoiGianHoatDong.add(map);
                                                    }
                                                }

                                                final Map<String, Object> thoigianhoatdongMap = new HashMap<>();
                                                thoigianhoatdongMap.put(Common.thoigianhoatdong, thoiGianHoatDong);

                                                db.collection(Common.DIADIEM).document(madiadiem)
                                                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                                                if(e != null){
                                                                    Toast.makeText(getApplicationContext(), getString(R.string.coloitrongquatrinhlaydulieu)
                                                                            , Toast.LENGTH_SHORT).show();

                                                                    return;
                                                                }

                                                                if (documentSnapshot.exists()){
                                                                    DiaDiem diaDiem = documentSnapshot.toObject(DiaDiem.class);
                                                                    hinhAnh.addAll(diaDiem.getHinhanh());

                                                                    for (int j = 0; j < hinhAnh.size(); j++){
                                                                        if (!arrTemp.contains(hinhAnh.get(j))){
                                                                            arrTemp.add(hinhAnh.get(j));
                                                                        }
                                                                    }

                                                                    hinhAnh.clear();
                                                                    hinhAnh.addAll(arrTemp);

                                                                    Map<String, Object> hinhanhMap = new HashMap<>();
                                                                    hinhanhMap.put(Common.hinhanh, hinhAnh);

                                                                    updateData(madiadiem, tendiadiem, diachi, location, website
                                                                            , dienthoai, danhmucMap, hinhanhMap, thoigianhoatdongMap);
                                                                }else {
                                                                    DiaDiem diaDiem = new DiaDiem();
                                                                    diaDiem.setMadiadiem(madiadiem);
                                                                    diaDiem.setTendiadiem(tendiadiem);
                                                                    diaDiem.setDiachi(diachi);
                                                                    diaDiem.setLocation(location);
                                                                    diaDiem.setWebsite(website);
                                                                    diaDiem.setDienthoai(dienthoai);
                                                                    diaDiem.setDanhmuc(danhmuc);
                                                                    diaDiem.setHinhanh(hinhAnh);
                                                                    diaDiem.setThoigianhoatdong(thoiGianHoatDong);

                                                                    themDiaDiem(madiadiem, diaDiem);
                                                                }
                                                            }
                                                        });

                                                List<Reviews> reviews = new ArrayList<>();

                                                if(mPlace.getResult().getReviews() != null){
                                                    for (int n = 0; n < mPlace.getResult().getReviews().length; n++)
                                                        reviews.add(mPlace.getResult().getReviews()[n]);

                                                    for (int q = 0; q < reviews.size(); q++) {
                                                        Review review = new Review();
                                                        review.setManguoireview("");
                                                        review.setTennguoireview(reviews.get(q).getAuthor_name());
                                                        review.setDanhgia(reviews.get(q).getRating());
                                                        review.setNoidungreview(reviews.get(q).getText());
                                                        review.setHinhanhnguoireview(reviews.get(q).getProfile_photo_url());

                                                        themReviews(madiadiem, review);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<PlaceDetail> call, Throwable t) {

                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyPlaces> call, Throwable t) {

                    }
                });
    }

    private String getUrl(double latitude, double longtitude, String placeType) {
        StringBuilder googlePlacesUrl = new StringBuilder(Common.GET_URL_NEARBY_PLACES);
        googlePlacesUrl.append("location=" + latitude + "," + longtitude);
        googlePlacesUrl.append("&radius=" + 5000);
        googlePlacesUrl.append("&type=" + placeType);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + getResources().getString(R.string.google_maps_key));

        return googlePlacesUrl.toString();
    }

    private String getPlaceDetailUrl(String place_id) {
        StringBuilder url = new StringBuilder(Common.GET_URL_DETAIL_PLACES);
        url.append("place_id=" + place_id + "&language=vi");
        url.append("&key=" + getResources().getString(R.string.google_maps_key));
        return url.toString();
    }

    private String getPhotoOfPlace(String photo_reference) {
        StringBuilder url = new StringBuilder(Common.GET_URL_PHOTO);
        url.append("?maxwidth=1000");
        url.append("&photoreference=" + photo_reference);
        url.append("&key=" + getResources().getString(R.string.google_maps_key));
        return url.toString();
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

    private void themDiaDiem(String madiadiem, final DiaDiem diaDiem) {
        final DocumentReference docRef = db.collection(Common.DIADIEM).document(madiadiem);

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (!documentSnapshot.exists()){
                    docRef.set(diaDiem).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(MainActivity.this, getString(R.string.themthanhcong), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, getString(R.string.themloi), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void themReviews(final String madiadiem, final Review review) {
        final DocumentReference docRef = db.collection(Common.DANHGIA).document(madiadiem);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (!documentSnapshot.exists()){
                    docRef.collection(Common.REVIEWS).document().set(review).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(MainActivity.this, getString(R.string.themthanhcong), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, getString(R.string.themloi), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void updateData(String madiadiem, String tendiadiem, String diachi, GeoPoint location, String website, String dienthoai
            , Map<String, Object> danhmucMap, Map<String, Object> hinhanhMap, Map<String, Object> thoiGianHoatDongMap){
        DocumentReference diadiem = db.collection(Common.DIADIEM).document(madiadiem);
        diadiem.update(Common.danhmuc, FieldValue.delete());
        diadiem.set(danhmucMap, SetOptions.merge());

        diadiem.update(Common.diachi, diachi);
        diadiem.update(Common.dienthoai, dienthoai);

        diadiem.update(Common.hinhanh, FieldValue.delete());
        diadiem.set(hinhanhMap, SetOptions.merge());

        diadiem.update(Common.location, location);
        diadiem.update(Common.madiadiem, madiadiem);
        diadiem.update(Common.tendiadiem, tendiadiem);

        diadiem.update(Common.thoigianhoatdong, FieldValue.delete());
        diadiem.set(thoiGianHoatDongMap, SetOptions.merge());

        diadiem.update(Common.website, website);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);

        editor.putBoolean("edit", false);
        editor.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);

        editor.putBoolean("edit", false);
        editor.commit();
    }
}
