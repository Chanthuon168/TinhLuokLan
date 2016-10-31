package com.hammersmith.tinhluoklan.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Chan Thuon on 9/9/2016.
 */
public class User {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("email")
    private String email;
    @SerializedName("social_link")
    private String socialLink;
    @SerializedName("social_type")
    private String socialType;
    @SerializedName("photo")
    private String photo;
    @SerializedName("msg")
    private String msg;
    @SerializedName("password")
    private String password;
    @SerializedName("address")
    private String address;
    @SerializedName("phone")
    private String phone;
    @SerializedName("phone2")
    private String phone2;

    public User() {
    }

    public User(String socialLink) {
        this.socialLink = socialLink;
    }

    public User(String name, String email, String photo, String socialLink, String socialType) {
        this.name = name;
        this.email = email;
        this.photo = photo;
        this.socialLink = socialLink;
        this.socialType = socialType;
    }

    public User(String name, String email, String password, String photo) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.photo = photo;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String socialLink, String name, String email, String address, String phone, String phone2, String photo) {
        this.socialLink = socialLink;
        this.name = name;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.phone2 = phone2;
        this.photo = photo;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSocialLink() {
        return socialLink;
    }

    public void setSocialLink(String socialLink) {
        this.socialLink = socialLink;
    }

    public String getSocialType() {
        return socialType;
    }

    public void setSocialType(String socialType) {
        this.socialType = socialType;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }
}
