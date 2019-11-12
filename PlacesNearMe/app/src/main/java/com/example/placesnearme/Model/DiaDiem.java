package com.example.placesnearme.Model;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.List;

public class DiaDiem implements Serializable {
    List<String> danhmuc;
    String diachi;
    List<String> hinhAnh;
    GeoPoint location;
    String madiadiem;
    String tendiadiem;

    public DiaDiem() { }

    public DiaDiem(List<String> danhmuc, String diachi, List<String> hinhAnh, GeoPoint location,
                   String madiadiem, String tendiadiem) {
        this.danhmuc = danhmuc;
        this.diachi = diachi;
        this.hinhAnh = hinhAnh;
        this.location = location;
        this.madiadiem = madiadiem;
        this.tendiadiem = tendiadiem;
    }

    public List<String> getDanhmuc() {
        return danhmuc;
    }

    public void setDanhmuc(List<String> danhmuc) {
        this.danhmuc = danhmuc;
    }

    public List<String> getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(List<String> hinhAnh) {
        this.hinhAnh = hinhAnh;
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
}

