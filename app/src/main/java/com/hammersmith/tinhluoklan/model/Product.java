package com.hammersmith.tinhluoklan.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Chan Thuon on 9/9/2016.
 */
public class Product {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("image")
    private String image;
    @SerializedName("price")
    private String price;
    @SerializedName("discount")
    private String discount;
    @SerializedName("car_type")
    private String type;
    @SerializedName("color")
    private String color;
    @SerializedName("owner_name")
    private String ownerName;
    @SerializedName("phone")
    private String phone;
    @SerializedName("phone2")
    private String phone2;
    @SerializedName("email")
    private String email;
    @SerializedName("website")
    private String website;
    @SerializedName("facebook")
    private String facebook;
    @SerializedName("description")
    private String description;
    @SerializedName("cat_name")
    private String catName;
    @SerializedName("pro_id")
    private String proId;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("address")
    private String address;
    @SerializedName("car_year")
    private String carYear;
    @SerializedName("transmition")
    private String transmition;
    @SerializedName("car_using")
    private String carUsing;
    @SerializedName("car_mater")
    private String carMater;
    @SerializedName("licence")
    private String licence;
    @SerializedName("seller_name")
    private String sellerName;

    public Product() {
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getProId() {
        return proId;
    }

    public void setProId(String proId) {
        this.proId = proId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCarYear() {
        return carYear;
    }

    public void setCarYear(String carYear) {
        this.carYear = carYear;
    }

    public String getTransmition() {
        return transmition;
    }

    public void setTransmition(String transmition) {
        this.transmition = transmition;
    }

    public String getCarUsing() {
        return carUsing;
    }

    public void setCarUsing(String carUsing) {
        this.carUsing = carUsing;
    }

    public String getCarMater() {
        return carMater;
    }

    public void setCarMater(String carMater) {
        this.carMater = carMater;
    }

    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }
}
