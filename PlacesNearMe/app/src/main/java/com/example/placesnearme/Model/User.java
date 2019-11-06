package com.example.placesnearme.Model;

import com.google.firebase.firestore.GeoPoint;

public class User {
    String mauser;
    String email;
    String avatar;
    String username;
    GeoPoint location;

    public User() {
    }

    public User(String mauser, String email, String avatar, String username, GeoPoint location) {
        this.mauser = mauser;
        this.email = email;
        this.avatar = avatar;
        this.username = username;
        this.location = location;
    }

    public String getMauser() {
        return mauser;
    }

    public void setMauser(String mauser) {
        this.mauser = mauser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "User{" +
                "mauser='" + mauser + '\'' +
                ", email='" + email + '\'' +
                ", avatar='" + avatar + '\'' +
                ", username='" + username + '\'' +
                ", location=" + location +
                '}';
    }
}
