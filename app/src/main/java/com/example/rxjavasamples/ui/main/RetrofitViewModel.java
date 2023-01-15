package com.example.rxjavasamples.ui.main;

import static kotlin.random.Random.Default;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.rxjavasamples.retrofit.PostName;
import com.example.rxjavasamples.retrofit.RetrofitRepository;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class RetrofitViewModel extends ViewModel {

    MutableLiveData<String> liveDataWithPostText =
            new MutableLiveData<>("Load random post");


    public void reloadPost() {

        int from = Default.nextInt(1,40);
        int count = Default.nextInt(1,50);


        Observable <List<String>> posts = RetrofitRepository.getPostTitles(from,from+count);
        /* В получаемом от ретрофита объекте УЖЕ может быть проведена серьезная обработка
        * сырых полученных данных - к примеру в нашем случае полученный с сервера
        * список объектов преобразуется к списку Strings c фильтрацией.
        * Созданный объект для вызова запроса хранится лениво, не активируется до subscribe()
        *  и может быть модифицирован дополнительными операторами или сцеплен с другими.
        */

        posts.doOnNext(result-> liveDataWithPostText.postValue(result.toString()))
                .doOnError(error-> liveDataWithPostText.postValue(error.toString()))
                .onErrorComplete()
                .subscribe();
        /*  обработка подготовленного вызова может быть сцеплена с выполнением конкретных
        *   операторов, доступных в месте вызова
        */


    }


    public void forceErrorRequest() {
        Observable <List<PostName>> posts = RetrofitRepository.getPostTitlesWithError();

        posts.doOnNext(result-> liveDataWithPostText.postValue(result.toString()))
                .doOnError(error-> liveDataWithPostText.postValue(error.toString()))
                .onErrorComplete()
                .subscribe();
    }



}