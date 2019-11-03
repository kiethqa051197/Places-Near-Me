package com.example.placesnearme;

import com.example.placesnearme.Interface.IGoogleAPIService;
import com.example.placesnearme.Model.Results;
import com.example.placesnearme.Remote.RetrofitClient;
import com.example.placesnearme.Remote.RetrofitScalarsClient;

public class Common {
    public static Results currentResult;

    private static final String GOOGLE_API_URL = "https://maps.googleapis.com/";

    public static IGoogleAPIService getGoogleAPIService(){
        return RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService.class);
    }

    public static IGoogleAPIService getGoogleAPIServiceScarlars(){
        return RetrofitScalarsClient.getScalarsClient(GOOGLE_API_URL).create(IGoogleAPIService.class);
    }
}
