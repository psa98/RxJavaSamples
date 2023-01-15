package com.example.rxjavasamples.retrofit;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostName {

        @SerializedName("id")
        @Expose
        public Integer id;

        @SerializedName("title")
        @Expose
        public String title;

    public PostName(Integer id, String title) {
        this.id = id;
        this.title = title;
    }
}
