package com.example.placesnearme.Model;

public class DanhMuc {
    String madanhmuc;
    String tendanhmuc;
    String hinhanh;
    DanhMucCha danhMucCha;

    public DanhMuc() {
    }

    public DanhMuc(String madanhmuc, String tendanhmuc, String hinhanh, DanhMucCha danhMucCha) {
        this.madanhmuc = madanhmuc;
        this.tendanhmuc = tendanhmuc;
        this.danhMucCha = danhMucCha;
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

    @Override
    public String toString() {
        return "DanhMuc{" +
                "madanhmuc='" + madanhmuc + '\'' +
                ", tendanhmuc='" + tendanhmuc + '\'' +
                ", hinhanh='" + hinhanh + '\'' +
                ", danhMucCha=" + danhMucCha +
                '}';
    }
}
