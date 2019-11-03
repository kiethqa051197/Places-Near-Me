package com.example.placesnearme.Model;

public class DanhMuc {
    String madanhmuc;
    String tendanhmuc;
    String madanhmuccha;

    public DanhMuc() {
    }

    public DanhMuc(String madanhmuc, String tendanhmuc, String madanhmuccha) {
        this.madanhmuc = madanhmuc;
        this.tendanhmuc = tendanhmuc;
        this.madanhmuccha = madanhmuccha;
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

    public String getMadanhmuccha() {
        return madanhmuccha;
    }

    public void setMadanhmuccha(String madanhmuccha) {
        this.madanhmuccha = madanhmuccha;
    }

    @Override
    public String toString() {
        return "DanhMuc{" +
                "madanhmuc='" + madanhmuc + '\'' +
                ", tendanhmuc='" + tendanhmuc + '\'' +
                ", madanhmuccha='" + madanhmuccha + '\'' +
                '}';
    }
}
