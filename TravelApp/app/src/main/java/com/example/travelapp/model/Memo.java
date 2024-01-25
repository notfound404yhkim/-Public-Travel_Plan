package com.example.travelapp.model;

import java.io.Serializable;

public class Memo implements Serializable {

    public int id;
    public String title;
    public String date;
    public String content;

    public Memo(String title, String date, String content) {
        this.title = title;
        this.date = date;
        this.content = content;
    }

    public Memo(int id,String title, String date, String content) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.content = content;
    }


}
