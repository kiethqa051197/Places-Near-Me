package com.example.placesnearme.Model;

public class DanhMucCha {
    String madanhmuc;
    String tendanhmuc;

    public DanhMucCha() {
    }

    public DanhMucCha(String madanhmuc, String tendanhmuc) {
        this.madanhmuc = madanhmuc;
        this.tendanhmuc = tendanhmuc;
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

    @Override
    public String toString() {
        return "DanhMucCha{" +
                "madanhmuc='" + madanhmuc + '\'' +
                ", tendanhmuc='" + tendanhmuc + '\'' +
                '}';
    }
}
