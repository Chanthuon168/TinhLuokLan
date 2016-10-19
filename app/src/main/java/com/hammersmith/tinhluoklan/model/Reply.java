package com.hammersmith.tinhluoklan.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Chan Thuon on 9/12/2016.
 */
public class Reply {
    @SerializedName("id")
    private int id;
    @SerializedName("profile")
    private String profile;
    @SerializedName("created_at")
    private String createAt;
    @SerializedName("name")
    private String name;
    @SerializedName("reply")
    private String reply;
    @SerializedName("social_link")
    private String socialLink;
    @SerializedName("com_id")
    private int comId;

    public Reply() {
    }

    public Reply(int comId, String socialLink, String reply, String createAt) {
        this.comId = comId;
        this.socialLink = socialLink;
        this.reply = reply;
        this.createAt = createAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getSocialLink() {
        return socialLink;
    }

    public void setSocialLink(String socialLink) {
        this.socialLink = socialLink;
    }

    public int getComId() {
        return comId;
    }

    public void setComId(int comId) {
        this.comId = comId;
    }
}
