package com.example.placesnearme.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.placesnearme.Adapter.ListItemDanhMucAdapter;
import com.example.placesnearme.Adapter.ListItemDiaDiemAdapter;
import com.example.placesnearme.Common;
import com.example.placesnearme.Interface.IGoogleAPIService;
import com.example.placesnearme.Model.DiaDiem;
import com.example.placesnearme.Model.MyPlaces;
import com.example.placesnearme.Model.Photos;
import com.example.placesnearme.Model.Results;
import com.example.placesnearme.R;
import com.example.placesnearme.Remote.ObjectSerializer;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultListAndMapActivity extends AppCompatActivity {

    private ActionBar actionBar;
    public RecyclerView listItemDiaDiem;
    private ListItemDiaDiemAdapter adapterDiaDiem;

    private FirebaseFirestore db;

    private RecyclerView.LayoutManager layoutManagerDiaDiem;

    private SharedPreferences prefCategorySelected, prefLocation, prefDiaDiem;

    public double latitude, longtitude;
    private List<DiaDiem> diaDiemList = new ArrayList<>();

    public IGoogleAPIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_list_and_map);

        prefCategorySelected = getSharedPreferences("shareDanhMucSelected", 0); // 0 - for private mode
        prefLocation = getSharedPreferences("shareLastLocation", 0); // 0 - for private mode

        mService = Common.getGoogleAPIService();

        actionBar = getSupportActionBar();
        actionBar.setTitle(prefCategorySelected.getString("tendanhmuc", "Error")); //Thiết lập tiêu đề nếu muốn
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayList diadiemList = new ArrayList();
        // Load user List from preferences
        prefDiaDiem = getSharedPreferences("DiaDiem", Context.MODE_PRIVATE);
        try {
            diadiemList = (ArrayList) ObjectSerializer.deserialize(prefDiaDiem.getString("DiaDiemList", ObjectSerializer.serialize(new ArrayList())));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        latitude = Double.parseDouble(prefLocation.getString("latitude", "0"));
        longtitude = Double.parseDouble(prefLocation.getString("longtitude", "0"));

        nearByPlace(prefCategorySelected.getString("madanhmuc", "Error"));

        listItemDiaDiem = findViewById(R.id.listDiaDiem);
        listItemDiaDiem.setHasFixedSize(true);
        layoutManagerDiaDiem = new LinearLayoutManager(this);
        listItemDiaDiem.setLayoutManager(layoutManagerDiaDiem);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
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

        mService.getNearByPlaces(url)
                .enqueue(new Callback<MyPlaces>() {
                    @Override
                    public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {
                        if (response.isSuccessful()) {
                            for (int i = 0; i < response.body().getResults().length; i++) {
                                Results googlePlaces = response.body().getResults()[i];

                                String placeId = googlePlaces.getPlace_id();
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
                                diaDiem.setMadiadiem(placeId);
                                diaDiem.setTendiadiem(placesName);
                                diaDiem.setDiachi(vicinity);
                                diaDiem.setDanhmuc(danhMuc);
                                diaDiem.setHinhAnh(hinhAnh);
                                diaDiem.setLocation(location);

                                diaDiemList.add(diaDiem);

                                SharedPreferences prefs = getSharedPreferences("DiaDiem", Context.MODE_PRIVATE);
                                //save the user list to preference
                                SharedPreferences.Editor editor = prefs.edit();

                                try {
                                    editor.putString("DiaDiemList", ObjectSerializer.serialize(diaDiem));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                editor.commit();
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
}
