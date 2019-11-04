package com.example.placesnearme.Model;

public class DanhMuc {
    String madanhmuc;
    String tendanhmuc;
    String madanhmuccha;
    DanhMucCha danhMucCha;

    public DanhMuc() {
    }

    public DanhMuc(String madanhmuc, String tendanhmuc, String madanhmuccha, DanhMucCha danhMucCha) {
        this.madanhmuc = madanhmuc;
        this.tendanhmuc = tendanhmuc;
        this.madanhmuccha = madanhmuccha;
        this.danhMucCha = danhMucCha;
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

    public DanhMucCha getDanhMucCha() {
        return danhMucCha;
    }

    public void setDanhMucCha(DanhMucCha danhMucCha) {
        this.danhMucCha = danhMucCha;
    }

    @Override
    public String toString() {
        return "DanhMuc{" +
                "madanhmuc='" + madanhmuc + '\'' +
                ", tendanhmuc='" + tendanhmuc + '\'' +
                ", madanhmuccha='" + madanhmuccha + '\'' +
                ", danhMucCha=" + danhMucCha +
                '}';
    }
}
