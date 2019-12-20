package com.example.placesnearme;

import com.example.placesnearme.Interface.IGoogleAPIService;
import com.example.placesnearme.Remote.RetrofitClient;

public class Common {
    private static final String GOOGLE_API_URL = "https://maps.googleapis.com/";
    public static final String GET_URL_NEARBY_PLACES = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    public static final String GET_URL_DETAIL_PLACES = "https://maps.googleapis.com/maps/api/place/details/json?";
    public static final String GET_URL_PHOTO = "https://maps.googleapis.com/maps/api/place/photo";

    //Collection
    public static final String DANHGIA = "Danh Gia";
    public static final String REVIEWS = "Reviews";
    public static final String DANHMUC  = "Danh Muc";
    public static final String DANHMUCCHA = "Danh Muc Cha";
    public static final String DIADIEM = "Dia Diem";
    public static final String USER = "User";

    // Storage
    public static final String IMAGE = "Images";
    public static final String AVATAR = "Avatar";

    //Field of Collection

    // Danh Muc - Danh Muc Cha
    public static final String hinhanh = "hinhanh";
    public static final String madanhmuc = "madanhmuc";
    public static final String tendanhmuc = "tendanhmuc";
    public static final String tukhoa = "tukhoa";

    // Dia Diem
    public static final String danhmuc = "danhmuc";
    public static final String diachi = "diachi";
    public static final String dienthoai = "dienthoai";
    public static final String location = "location";
    public static final String madiadiem = "madiadiem";
    public static final String tendiadiem = "tendiadiem";
    public static final String thoigianhoatdong = "thoigianhoatdong";
    public static final String website = "website";

    // User
    public static final String avatar = "avatar";
    public static final String email = "email";
    public static final String mauser = "mauser";
    public static final String username = "username";

    // Choose Image
    public static final String setStype = "image/*";
    public static final String titleChooseImage = "Chọn ảnh";
    public static final String content = "content";

    // Other
    public static final String dongcua = "dongcua";
    public static final String mocua = "mocua";
    public static final String edit = "edit";
    public static final String filename ="filename";

    public static final String PREF_DANHMUC = "prefDanhMuc";
    public static final String PREF_DANHMUCCHA = "prefDanhMucCha";
    public static final String PREF_DIADIEM = "prefMaDiaDiem";
    public static final String PREF_USER = "prefUser";
    public static final String PREF_EDIT = "prefEdit";
    public static final String PREF_FILE = "prefFile";

    public static IGoogleAPIService getGoogleAPIService(){
        return RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService.class);
    }
}
