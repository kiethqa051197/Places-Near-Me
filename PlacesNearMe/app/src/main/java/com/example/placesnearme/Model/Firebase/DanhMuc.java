package com.example.placesnearme.Model.Firebase;

import java.util.List;

public class DanhMuc {
    private String madanhmuc;
    private String tendanhmuc;
    private String hinhanh;
    private DanhMucCha danhmuccha;
    private List<String> tukhoa;

    public DanhMuc() { }

    public DanhMuc(String madanhmuc, String tendanhmuc, String hinhanh, DanhMucCha danhmuccha, List<String> tukhoa) {
        this.madanhmuc = madanhmuc;
        this.tendanhmuc = tendanhmuc;
        this.danhmuccha = danhmuccha;
        this.hinhanh = hinhanh;
        this.tukhoa = tukhoa;
    }

    public String getMadanhmuc() {
        return madanhmuc;
    }

    public void setMadanhmuc(String madanhmuc) {
        this.madanhmuc = madanhmuc;
    }

    public String getTendanhmuc() {
        return tendanhmuc;
    }

    public void setTendanhmuc(String tendanhmuc) {
        this.tendanhmuc = tendanhmuc;
    }

    public DanhMucCha getDanhMucCha() {
        return danhmuccha;
    }

    public void setDanhMucCha(DanhMucCha danhmuccha) {
        this.danhmuccha = danhmuccha;
    }

    public String getHinhanh() {
        return hinhanh;
    }

    public void setHinhanh(String hinhanh) {
        this.hinhanh = hinhanh;
    }

    public List<String> getTukhoa() {
        return tukhoa;
    }

    public void setTukhoa(List<String> tukhoa) {
        this.tukhoa = tukhoa;
    }
}