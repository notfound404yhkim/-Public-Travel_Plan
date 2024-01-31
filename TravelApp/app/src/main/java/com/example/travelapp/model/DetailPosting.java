package com.example.travelapp.model;

import java.util.ArrayList;

public class DetailPosting {
    public String result;
    public Posting items;
    public ArrayList<String> tag;
    public ArrayList<Comments> comments;

    public class Comments {
        public int id;
        public String name;
        public String profileImg;
        public String content;
        public String createdAt;
    }
}
