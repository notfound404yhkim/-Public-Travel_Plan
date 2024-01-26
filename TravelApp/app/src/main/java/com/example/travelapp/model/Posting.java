package com.example.travelapp.model;

import java.io.Serializable;

public class Posting implements Serializable {

    public int id;
    public int userId;
    public String imgUrl;
    public String title;
    public String content;
    public String createdAt;
    public int isLike;
    public int likeCnt;

    public Posting(int id, int userId, String imgUrl, String title, String content, String createdAt, int isLike, int likeCnt) {
        this.id = id;
        this.userId = userId;
        this.imgUrl = imgUrl;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.isLike = isLike;
        this.likeCnt = likeCnt;
    }
}