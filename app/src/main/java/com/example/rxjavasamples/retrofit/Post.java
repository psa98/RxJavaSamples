package com.example.rxjavasamples.retrofit;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Post {

        @SerializedName("id")
        @Expose
        public Integer id;

        @SerializedName("title")
        @Expose
        public String title;

        @SerializedName("body")
        @Expose
        public String body;

        @SerializedName("userId")
        @Expose
        public Integer userId;


    public Post(Integer id, String title, String body, Integer userId) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.userId = userId;
    }
}
