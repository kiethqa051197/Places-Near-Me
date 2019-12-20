package com.example.placesnearme.Model.Firebase;

import com.google.firebase.firestore.GeoPoint;

import java.util.List;
import java.util.Map;

public class DiaDiem {
    private List<String> danhmuc;
    private List<String> hinhanh;
    private List<Map<String, Object>> thoigianhoatdong;
    private String diachi;
    private GeoPoint location;
    private String madiadiem;
    private String tendiadiem;
    private String website;
    private String dienthoai;

    public DiaDiem() { }

    public DiaDiem(List<String> danhmuc, List<String> hinhanh, List<Map<String, Object>> thoigianhoatdong, String diachi,
                   GeoPoint location, String madiadiem, String tendiadiem, String website, String dienthoai) {
        this.danhmuc = danhmuc;
        this.hinhanh = hinhanh;
        this.thoigianhoatdong = thoigianhoatdong;
        this.diachi = diachi;
        this.location = location;
        this.madiadiem = madiadiem;
        this.tendiadiem = tendiadiem;
        this.website = website;
        this.dienthoai = dienthoai;
    }

    public List<String> getDanhmuc() {
        return danhmuc;
    }

    public void setDanhmuc(List<String> danhmuc) {
        this.danhmuc = danhmuc;
    }

    public List<String> getHinhanh() {
        return hinhanh;
    }

    public void setHinhanh(List<String> hinhanh) {
        this.hinhanh = hinhanh;
    }

    public List<Map<String, Object>> getThoigianhoatdong() {
        return thoigianhoatdong;
    }

    public void setThoigianhoatdong(List<Map<String, Object>> thoigianhoatdong) {
        this.thoigianhoatdong = thoigianhoatdong;
    }

    public String getDiachi() {
        return diachi;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getMadiadiem() {
        return madiadiem;
    }

    public void setMadiadiem(String madiadiem) {
        this.madiadiem = madiadiem;
    }

    public String getTendiadiem() {
        return tendiadiem;
    }

    public void setTendiadiem(String tendiadiem) {
        this.tendiadiem = tendiadiem;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getDienthoai() {
        return dienthoai;
    }

    public void setDienthoai(String dienthoai) {
        this.dienthoai = dienthoai;
    }
}