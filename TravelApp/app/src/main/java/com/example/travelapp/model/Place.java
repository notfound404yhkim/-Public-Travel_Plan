package com.example.travelapp.model;

import java.io.Serializable;

public class Place implements Serializable {

    public int id;
    public int userId;
    public String region;
    public String PlaceName;

    public String content;
    public String imgUrl;
    public String strDate;
    public String endDate;

    public Place(int id, String imgUrl) {
        this.id = id;
        this.imgUrl = imgUrl;
    }
}
