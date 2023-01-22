package com.example.rxjavasamples.retrofit;


import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface Api {


    @GET("posts/")
    Call<List<PostName>> getAllPosts();

    /* Отличия при использовании Рх с Ретрофит - Observable/Single/Flowable  вместо Call
    */
    @GET("posts/")
    Observable<List<PostName>> getAllPostsAsObservable();

    @GET("postsZZZ/")
    Observable<List<PostName>> getApiError();


    @GET("/posts/{id}")
    Call<Post> getPostById(@Path("id") int id);


    @GET("/posts/{id}")
    Single<Post> getSinglePostById(@Path("id") int id);



}
