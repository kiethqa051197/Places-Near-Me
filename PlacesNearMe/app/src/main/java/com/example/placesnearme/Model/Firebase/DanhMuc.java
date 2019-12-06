package com.example.placesnearme.Model.Firebase;

import java.util.List;

public class DanhMuc {
    String madanhmuc;
    String tendanhmuc;
    String hinhanh;
    DanhMucCha danhMucCha;
    List<String> tukhoa;

    public DanhMuc() {
    }

    public DanhMuc(String madanhmuc, String tendanhmuc, String hinhanh, DanhMucCha danhMucCha, List<String> tukhoa) {
        this.madanhmuc = madanhmuc;
        this.tendanhmuc = tendanhmuc;
        this.danhMucCha = danhMucCha;
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
        return danhMucCha;
    }

    public void setDanhMucCha(DanhMucCha danhMucCha) {
        this.danhMucCha = danhMucCha;
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