package com.example.placesnearme.Interface;

import com.example.placesnearme.Model.MyPlaces;
import com.example.placesnearme.Model.PlaceDetail;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IGoogleAPIService {
    @GET
    Call<MyPlaces> getNearByPlaces(@Url String url);

    @GET
    Call<PlaceDetail> getDetaislPlaces(@Url String url);
//
//    @GET("maps/api/directions/json")
//    Call<String> getDirections(@Query("origin") String origin, @Query("destination") String destination, @Query("key") String key);
}