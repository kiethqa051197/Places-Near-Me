package com.example.placesnearme.Model.Firebase;

public class DanhMucCha {
    private String madanhmuc;
    private String tendanhmuc;
    private String hinhanh;

    public DanhMucCha() { }

    public DanhMucCha(String madanhmuc, String tendanhmuc, String hinhanh) {
        this.madanhmuc = madanhmuc;
        this.tendanhmuc = tendanhmuc;
        this.hinhanh = hinhanh;
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

    public String getHinhanh() {
        return hinhanh;
    }

    public void setHinhanh(String hinhanh) {
        this.hinhanh = hinhanh;
    }

    @androidx.annotation.NonNull
    @Override
    public String toString() {
        return tendanhmuc;
    }
}
