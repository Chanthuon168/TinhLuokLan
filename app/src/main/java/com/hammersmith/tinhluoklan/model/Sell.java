package com.hammersmith.tinhluoklan.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Chan Thuon on 10/27/2016.
 */
public class Sell {
    @SerializedName("image")
    private String image;
    @SerializedName("msg")
    private String msg;
    @SerializedName("model")
    private String model;
    @SerializedName("year")
    private String year;
    @SerializedName("color")
    private String color;
    @SerializedName("transmission")
    private String transmission;
    @SerializedName("condition")
    private String condition;
    @SerializedName("mileage")
    private String mileage;
    @SerializedName("city")
    private String city;
    @SerializedName("power")
    private String power;
    @SerializedName("price")
    private String price;
    @SerializedName("licence")
    private String licence;
    @SerializedName("description")
    private String description;
    @SerializedName("name")
    private String name;
    @SerializedName("address")
    private String address;
    @SerializedName("phone")
    private String phone;
    @SerializedName("phone2")
    private String phone2;
    @SerializedName("social_link")
    private String socialLink;
    @SerializedName("cat_id")
    private int catId;
    @SerializedName("num_image")
    private int numImage;
    @SerializedName("car_id")
    private int carId;
    @SerializedName("img_id")
    private int imgId;
    @SerializedName("created_at")
    private String createdAt;


    public Sell() {
    }

    public Sell(String image) {
        this.image = image;
    }

    public Sell(int catId, String socialLink, String model) {
        this.catId = catId;
        this.socialLink = socialLink;
        this.model = model;
    }

    public Sell(int carId, String image) {
        this.carId = carId;
        this.image = image;
    }

    public Sell(int carId, int numImage, int imgId, String price, String year, String condition, String transmission, String power, String mileage, String color, String city, String licence, String description, String name, String address, String phone, String phone2, String socialLink, String createdAt) {
        this.carId = carId;
        this.numImage = numImage;
        this.imgId = imgId;
        this.price = price;
        this.year = year;
        this.condition = condition;
        this.transmission = transmission;
        this.power = power;
        this.mileage = mileage;
        this.color = color;
        this.city = city;
        this.licence = licence;
        this.description = description;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.phone2 = phone2;
        this.socialLink = socialLink;
        this.createdAt = createdAt;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }
}
