package com.hammersmith.tinhluoklan.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Chan Thuon on 9/19/2016.
 */
public class Love {
    @SerializedName("id")
    private int id;
    @SerializedName("count")
    private String count;
    @SerializedName("pro_id")
    private int proId;
    @SerializedName("status")
    private String status;
    @SerializedName("social_link")
    private String socialLink;

    public Love() {
    }

    public Love(int proId, String socialLink) {
        this.proId = proId;
        this.socialLink = socialLink;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProId() {
        return proId;
    }

    public void setProId(int proId) {
        this.proId = proId;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSocialLink() {
        return socialLink;
    }

    public void setSocialLink(String socialLink) {
        this.socialLink = socialLink;
    }
}
