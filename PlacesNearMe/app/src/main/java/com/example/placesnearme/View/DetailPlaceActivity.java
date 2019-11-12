package com.example.placesnearme.View;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.placesnearme.Adapter.ListItemDetailCategoryAdapter;
import com.example.placesnearme.Adapter.ListItemImagePlacesAdapter;
import com.example.placesnearme.Adapter.ListItemReviewsRatingAdapter;
import com.example.placesnearme.Common;
import com.example.placesnearme.Interface.IGoogleAPIService;
import com.example.placesnearme.Model.PlaceDetail;
import com.example.placesnearme.Model.Reviews;
import com.example.placesnearme.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailPlaceActivity extends AppCompatActivity implements View.OnClickListener{
    private ActionBar actionBar;
    private CircleImageView imgAvataPlace, imgPhone, imgChrome;
    private TextView txtPlacesName, txtPlacePhone, txtWebsite,
            txtOpenNow, txtCountRating, txtDetailLocation, txtMontoFriday, txtSattoSun, txtSeeAllDay,
            txtMonday, txtTuesday, txtWednesday, txtThursday, txtFriday, txtSaturday, txtSunday, txtMoreMedia;

    private NestedScrollView nestedScrollView;

    private LinearLayout linearViewOnMap, linearSeeAllDay, linearShowAllDay,
            linearImage, linearReviews, linearRating;

    private IGoogleAPIService mService;
    private PlaceDetail mPlace;

    private boolean nodataPhone = false;
    private boolean nodataWebsite = false;

    private String monday, tuesday, wednesday,thursday, friday, saturday, sunday, website, phone;

    private SharedPreferences prefPlace;

    private RecyclerView listItemImage, listItemCategory, listItemReviewsRating;
    private RecyclerView.LayoutManager layoutManagerImage, layoutManagerCategory, layoutManagerReviewsRating;

    private ListItemImagePlacesAdapter adapterImage;
    private ListItemDetailCategoryAdapter adapterCategory;
    private ListItemReviewsRatingAdapter adapterReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_places);

        mService = Common.getGoogleAPIService();

        prefPlace = getSharedPreferences("sharePlaces", 0);

        actionBar = getSupportActionBar();
        actionBar.setTitle(prefPlace.getString("distance", ""));
        actionBar.setLogo(R.drawable.ic_near_me_black_24dp);    //Icon muốn hiện thị
        actionBar.setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nestedScrollView = findViewById(R.id.nestedScrollView);

        imgAvataPlace = findViewById(R.id.imgAvataPlace);
        imgPhone = findViewById(R.id.imgPhone);
        imgChrome = findViewById(R.id.imgChrome);

        txtPlacesName = findViewById(R.id.txtPlacesName);
        txtPlacePhone = findViewById(R.id.txtPlacePhone);
        txtCountRating = findViewById(R.id.txtCountRating);
        txtDetailLocation = findViewById(R.id.txtDetailLocation);
        txtOpenNow = findViewById(R.id.txtOpenNow);
        txtWebsite = findViewById(R.id.txtWebsite);
        txtMontoFriday = findViewById(R.id.txtMontoFriday);
        txtSattoSun = findViewById(R.id.txtSattoSun);

        txtMonday = findViewById(R.id.txtMonday);
        txtTuesday = findViewById(R.id.txtTuesday);
        txtWednesday = findViewById(R.id.txtWednesday);
        txtThursday = findViewById(R.id.txtThursday);
        txtFriday = findViewById(R.id.txtFriday);
        txtSaturday = findViewById(R.id.txtSaturday);
        txtSunday = findViewById(R.id.txtSunday);

        txtSeeAllDay = findViewById(R.id.txtSeeAllDay);

        txtMoreMedia = findViewById(R.id.txtMoreMedia);

        linearViewOnMap = findViewById(R.id.linearViewOnMap);
        linearSeeAllDay = findViewById(R.id.linearSeeAllDay);
        linearShowAllDay = findViewById(R.id.linearShowAllDay);
        linearImage = findViewById(R.id.linearImage);
        linearReviews = findViewById(R.id.linearReviews);
        linearRating = findViewById(R.id.linearRating);

        listItemImage = findViewById(R.id.recyclerImage);
        listItemImage.setHasFixedSize(true);
        layoutManagerImage = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        listItemImage.setLayoutManager(layoutManagerImage);

        listItemCategory = findViewById(R.id.recyclerCategoryDetais);
        listItemCategory.setHasFixedSize(true);
        layoutManagerCategory = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        listItemCategory.setLayoutManager(layoutManagerCategory);

        listItemReviewsRating = findViewById(R.id.recyclerReview);
        listItemReviewsRating.setHasFixedSize(true);
        layoutManagerReviewsRating = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        listItemReviewsRating.setLayoutManager(layoutManagerReviewsRating);

        //User Service to fectch Address and Name
        mService.getDetaislPlaces(getPlaceDetailUrl(prefPlace.getString("madiadiem", "")))
                .enqueue(new Callback<PlaceDetail>() {
                    @Override
                    public void onResponse(Call<PlaceDetail> call, Response<PlaceDetail> response) {
                        mPlace = response.body();

                        if (mPlace.getResult().getOpening_hours() != null){
                            if (mPlace.getResult().getOpening_hours().getOpen_now().equals("true"))
                                txtOpenNow.setText("Open now");
                            else
                                txtOpenNow.setText("Closed");
                        } else
                            txtOpenNow.setText("No data to display");

                        txtDetailLocation.setText(mPlace.getResult().getFormatted_address());
                        txtPlacesName.setText(mPlace.getResult().getName());

                        if (mPlace.getResult().getFormatted_phone_number() != null){
                            phone = mPlace.getResult().getFormatted_phone_number();
                            txtPlacePhone.setText("Phone: " + phone);
                            nodataPhone = false;
                        } else {
                            txtPlacePhone.setText("Phone: No data to display");
                            nodataPhone = true;
                        }

                        if (mPlace.getResult().getWebsite() != null){
                            website = mPlace.getResult().getWebsite();
                            txtWebsite.setText("Website: " + website);
                            nodataWebsite = false;
                        } else {
                            txtWebsite.setText("Website: No data to display");
                            nodataWebsite = true;
                        }

                        List<String> categoryList = new ArrayList<>();

                        for (int i = 0; i < mPlace.getResult().getTypes().length; i++) {
                            categoryList.add(mPlace.getResult().getTypes()[i]);
                        }

                        for (int l = 0; l < categoryList.size(); l++) {
                            if (categoryList.get(l).equals("point_of_interest"))
                                categoryList.remove(l);
                            if (categoryList.get(l).equals("establishment"))
                                categoryList.remove(l);
                        }

                        List<String> imagelist = new ArrayList<>();
                        List<String> imageListAfter = new ArrayList<>();
                        List<Reviews> reviewsList = new ArrayList<>();

                        if (mPlace.getResult().getPhotos() != null){
                            for (int i = 0; i < mPlace.getResult().getPhotos().length; i++) {
                                imagelist.add(mPlace.getResult().getPhotos()[i].getPhoto_reference());
                            }
                        }else
                            linearImage.setVisibility(View.GONE);

                        int size = imagelist.size();

                        if (size > 3){
                            Picasso.get()
                                    .load(getPhotoOfPlace(imagelist.get(0), 1000))
                                    .placeholder(R.drawable.ic_image)
                                    .error(R.drawable.ic_error)
                                    .into(imgAvataPlace);

                            txtMoreMedia.setText(size + "\n Media");
                            txtMoreMedia.setVisibility(View.VISIBLE);

                            for (int k = 0; k < 3; k++){
                                imageListAfter.add(imagelist.get(k));
                            }
                        }else
                            txtMoreMedia.setVisibility(View.GONE);

                        txtCountRating.setText(mPlace.getResult().getUser_ratings_total() + " rating");

                        if (mPlace.getResult().getOpening_hours() != null){
                            if (mPlace.getResult().getOpening_hours().getWeekday_text() != null){
                                if (!mPlace.getResult().getOpening_hours().getWeekday_text()[0].equals("Open 24 hours"))
                                    monday = mPlace.getResult().getOpening_hours().getWeekday_text()[0].substring(8);

                                tuesday = mPlace.getResult().getOpening_hours().getWeekday_text()[1].substring(9);
                                wednesday = mPlace.getResult().getOpening_hours().getWeekday_text()[2].substring(10);
                                thursday = mPlace.getResult().getOpening_hours().getWeekday_text()[3].substring(9);
                                friday = mPlace.getResult().getOpening_hours().getWeekday_text()[4].substring(8);

                                if (!mPlace.getResult().getOpening_hours().getWeekday_text()[5].equals("Closed"))
                                    saturday = mPlace.getResult().getOpening_hours().getWeekday_text()[5].substring(10);
                                else
                                    saturday = "Closed";

                                if (!mPlace.getResult().getOpening_hours().getWeekday_text()[6].equals("Closed"))
                                    sunday = mPlace.getResult().getOpening_hours().getWeekday_text()[6].substring(8);
                                else
                                    sunday = "Closed";


                                txtMontoFriday.setText(monday);

                                if (saturday.equals(sunday))
                                    txtSattoSun.setText(saturday);
                                else
                                    txtSattoSun.setText(saturday + ", " + sunday);
                            }
                        }
                        else {
                            txtSattoSun.setText("No Data");
                            txtMontoFriday.setText("No Data");
                            linearSeeAllDay.setEnabled(false);
                        }

                        if (mPlace.getResult().getReviews() != null){
                            for (int i = 0; i < mPlace.getResult().getReviews().length; i++){
                                Reviews reviews = new Reviews();
                                reviews.setAuthor_name(mPlace.getResult().getReviews()[i].getAuthor_name());
                                reviews.setAuthor_url(mPlace.getResult().getReviews()[i].getAuthor_url());
                                reviews.setLanguage(mPlace.getResult().getReviews()[i].getLanguage());
                                reviews.setProfile_photo_url(mPlace.getResult().getReviews()[i].getProfile_photo_url());
                                reviews.setRating(mPlace.getResult().getReviews()[i].getRating());
                                reviews.setText(mPlace.getResult().getReviews()[i].getText());
                                reviews.setTime(mPlace.getResult().getReviews()[i].getTime());
                                reviews.setRelative_time_description(mPlace.getResult().getReviews()[i].getRelative_time_description());

                                reviewsList.add(reviews);
                            }
                        }else
                            linearReviews.setVisibility(View.GONE);

                        for (int i = 0; i < reviewsList.size(); i++){
                            Log.d("ktra", reviewsList.get(i).getAuthor_name());
                            Log.d("ktra", reviewsList.get(i).getTime());
                            Log.d("ktra", reviewsList.get(i).getRelative_time_description());
                        }

                        adapterImage = new ListItemImagePlacesAdapter(DetailPlaceActivity.this, imageListAfter);
                        listItemImage.setAdapter(adapterImage);

                        adapterCategory = new ListItemDetailCategoryAdapter(DetailPlaceActivity.this, categoryList);
                        listItemCategory.setAdapter(adapterCategory);

                        adapterReviews = new ListItemReviewsRatingAdapter(DetailPlaceActivity.this, reviewsList);
                        listItemReviewsRating.setAdapter(adapterReviews);
                    }

                    @Override
                    public void onFailure(Call<PlaceDetail> call, Throwable t) {

                    }
                });

        linearViewOnMap.setOnClickListener(this);
        linearSeeAllDay.setOnClickListener(this);
        linearShowAllDay.setOnClickListener(this);
        linearRating.setOnClickListener(this);
        imgChrome.setOnClickListener(this);
        imgPhone.setOnClickListener(this);
    }

    private String getPlaceDetailUrl(String place_id) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        url.append("place_id=" + place_id);
        url.append("&key=" + getResources().getString(R.string.google_maps_key));
        return url.toString();
    }

    public String getPhotoOfPlace(String photo_reference, int maxWidth) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo");
        url.append("?maxwidth=" + maxWidth);
        url.append("&photoreference=" + photo_reference);
        url.append("&key=" + getResources().getString(R.string.google_maps_key));
        return url.toString();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id){
            case R.id.linearViewOnMap:
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mPlace.getResult().getUrl()));
                startActivity(mapIntent);
                break;
            case R.id.linearSeeAllDay:
                if (linearShowAllDay.getVisibility() == View.VISIBLE){
                    linearShowAllDay.setVisibility(View.GONE);
                    txtSeeAllDay.setText("See all day");
                }else {
                    txtMonday.setText(monday);
                    txtTuesday.setText(tuesday);
                    txtWednesday.setText(wednesday);
                    txtThursday.setText(thursday);
                    txtFriday.setText(friday);
                    txtSaturday.setText(saturday);
                    txtSunday.setText(sunday);

                    linearShowAllDay.setVisibility(View.VISIBLE);
                    txtSeeAllDay.setText("Collapse");
                }
                break;
            case R.id.imgPhone:
                if (!nodataPhone){
                    Uri u = Uri.parse("tel:" + phone);
                    Intent i = new Intent(Intent.ACTION_DIAL, u);

                    try {
                        startActivity(i);
                    }
                    catch (SecurityException s) { }
                }else {
                    Toast.makeText(this, "No data for display", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.imgChrome:
                if (!nodataWebsite){
                    Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(website));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setPackage("com.android.chrome");
                    try {
                        this.startActivity(intent);
                    } catch (ActivityNotFoundException ex) {
                        intent.setPackage(null);
                        this.startActivity(intent);
                    }
                }else {
                    Toast.makeText(this, "No data for display", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.linearRating:
                nestedScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        nestedScrollView.fullScroll(NestedScrollView.FOCUS_DOWN);
                    }
                });
                break;
        }
    }
}
