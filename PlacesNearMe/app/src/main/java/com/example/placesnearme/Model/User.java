package com.example.placesnearme.Model;

public class User {
    String mauser;
    String email;
    String avatar;
    String username;

    public User() {
    }

    public User(String mauser, String email, String avatar, String username) {
        this.mauser = mauser;
        this.email = email;
        this.avatar = avatar;
        this.username = username;
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
}
