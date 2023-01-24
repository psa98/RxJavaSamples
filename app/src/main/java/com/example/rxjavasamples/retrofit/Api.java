package com.example.rxjavasamples.retrofit;


import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

public interface Api {


    /* Отличия при использовании Рх с Ретрофит - Observable/Single/Flowable  вместо Call
    */
    @GET("posts/")
    Observable<List<PostName>> getAllPostsAsObservable();

    @GET("postsZZZ/")
    Observable<List<PostName>> getApiError();


}
