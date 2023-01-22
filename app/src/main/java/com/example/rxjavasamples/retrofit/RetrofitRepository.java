package com.example.rxjavasamples.retrofit;


import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;


public class RetrofitRepository {



    /* В получаемом от ретрофита объекте УЖЕ может быть проведена серьезная обработка
     * сырых полученных данных - к примеру в нашем случае полученный с сервера
     * список объектов преобразуется к списку Strings c фильтрацией.
     * Созданный объект для вызова запроса хранится лениво, не активируется до subscribe()
     *  и может быть модифицирован дополнительными операторами или сцеплен с другими.
     *
     * Для простых разовых запросов в сеть правильнее использовать Single, применение
     * Observable/Flowable позволяет создание сложных цепочек запросов, к примеру когда
     * результат полученного списка id генерит цепочку обращений к серверу для каждого id
     *
     */
    public static Observable<List<String>> getPostTitles(int from,int until) {
        return RetrofitClient.getInstance()
                .getApi()
                .getAllPostsAsObservable()
                .map(postNames -> {
                    ArrayList<String> list = new ArrayList<>();
                    int counter = 0;
                    for (PostName  item :postNames) {
                        if (counter>=from&&counter<=until)
                            list.add("\n\n"+item.title);
                        counter++;
                        }
                    return list;
                });

    }

    public static Observable<List<PostName>> getPostTitlesWithError() {
        return RetrofitClient.getInstance()
                .getApi()
                .getAllPostsWithError();

    }


}
