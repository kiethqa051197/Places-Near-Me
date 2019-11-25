package com.example.placesnearme.Model.Firebase;

public class Review {
    private String manguoireview;
    private String tennguoireview;
    private String hinhanhnguoireview;
    private String danhgia;
    private String noidungreview;

    public Review() {
    }

    public Review(String manguoireview, String tennguoireview, String hinhanhnguoireview, String danhgia, String noidungreview) {
        this.manguoireview = manguoireview;
        this.tennguoireview = tennguoireview;
        this.hinhanhnguoireview = hinhanhnguoireview;
        this.danhgia = danhgia;
        this.noidungreview = noidungreview;
    }

    public String getManguoireview() {
        return manguoireview;
    }

    public void setManguoireview(String manguoireview) {
        this.manguoireview = manguoireview;
    }

    public String getTennguoireview() {
        return tennguoireview;
    }

    public void setTennguoireview(String tennguoireview) {
        this.tennguoireview = tennguoireview;
    }

    public String getHinhanhnguoireview() {
        return hinhanhnguoireview;
    }

    public void setHinhanhnguoireview(String hinhanhnguoireview) {
        this.hinhanhnguoireview = hinhanhnguoireview;
    }

    public String getDanhgia() {
        return danhgia;
    }

    public void setDanhgia(String danhgia) {
        this.danhgia = danhgia;
    }

    public String getNoidungreview() {
        return noidungreview;
    }

    public void setNoidungreview(String noidungreview) {
        this.noidungreview = noidungreview;
    }
}
