package com.example.placesnearme.Model.Firebase;

import com.google.firebase.firestore.GeoPoint;

import java.util.List;

public class DiaDiem {
    List<String> danhmuc;
    List<String> hinhanh;
    List<String> thoigianhoatdong;
    String diachi;
    GeoPoint location;
    String madiadiem;
    String tendiadiem;
    String website;
    String dienthoai;

    public DiaDiem() { }

    public DiaDiem(List<String> danhmuc, List<String> hinhanh, List<String> thoigianhoatdong, String diachi,
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

    public List<String> getThoigianhoatdong() {
        return thoigianhoatdong;
    }

    public void setThoigianhoatdong(List<String> thoigianhoatdong) {
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