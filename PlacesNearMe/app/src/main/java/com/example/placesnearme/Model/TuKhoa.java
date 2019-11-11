package com.example.placesnearme.Model;

public class TuKhoa {
    String matukhoa;
    String tentukhoa;
    DanhMuc danhMuc;

    public TuKhoa() {
    }

    public TuKhoa(String matukhoa, String tentukhoa, DanhMuc danhMuc) {
        this.matukhoa = matukhoa;
        this.tentukhoa = tentukhoa;
        this.danhMuc = danhMuc;
    }

    public String getMatukhoa() {
        return matukhoa;
    }

    public void setMatukhoa(String matukhoa) {
        this.matukhoa = matukhoa;
    }

    public String getTentukhoa() {
        return tentukhoa;
    }

    public void setTentukhoa(String tentukhoa) {
        this.tentukhoa = tentukhoa;
    }

    public DanhMuc getDanhMuc() {
        return danhMuc;
    }

    public void setDanhMuc(DanhMuc danhMuc) {
        this.danhMuc = danhMuc;
    }
}
