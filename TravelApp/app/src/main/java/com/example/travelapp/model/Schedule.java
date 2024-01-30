package com.example.travelapp.model;

import java.io.Serializable;

public class Schedule  {
    public int id;
    public int userId;
    public String region;
    public String strDate;
    public String endDate;

    public String content;
    public String createdAt;
    public String imgUrl;

    public Schedule(int id, int userId, String region, String strDate, String endDate, String content, String createdAt, String imgUrl) {
        this.id = id;
        this.userId = userId;
        this.region = region;
        this.strDate = strDate;
        this.endDate = endDate;
        this.content = content;
        this.createdAt = createdAt;
        this.imgUrl = imgUrl;
    }

    public Schedule(int id, int userId, String region, String strDate, String endDate, String content, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.region = region;
        this.strDate = strDate;
        this.endDate = endDate;
        this.content = content;
        this.createdAt = createdAt;
    }
}
