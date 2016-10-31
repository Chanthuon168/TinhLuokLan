package com.hammersmith.tinhluoklan.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Chan Thuon on 10/5/2016.
 */
public class DeviceToken {
    @SerializedName("social_link")
    private String socialLink;
    @SerializedName("token")
    private String deviceToken;

    public DeviceToken() {
    }

    public DeviceToken(String socialLink, String deviceToken) {
        this.socialLink = socialLink;
        this.deviceToken = deviceToken;
    }

    public String getSocialLink() {
        return socialLink;
    }

    public void setSocialLink(String socialLink) {
        this.socialLink = socialLink;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
