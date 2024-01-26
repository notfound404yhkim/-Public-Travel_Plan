package com.example.travelapp.model;

import java.io.Serializable;

public class History implements Serializable {


    public String region;
    public String strDate;
    public String endDate;


    public History(String region, String strDate, String endDate) {
        this.region = region;
        this.strDate = strDate;
        this.endDate = endDate;
    }
}
