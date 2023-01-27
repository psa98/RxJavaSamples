package com.example.rxjavasamples.ui.main;

import static com.example.rxjavasamples.MainActivity.TAG;
import static java.lang.System.currentTimeMillis;
import static kotlin.random.Random.Default;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.rxjavasamples.retrofit.PostName;
import com.example.rxjavasamples.retrofit.RetrofitRepository;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

public class RetrofitViewModel extends ViewModel {

    final MutableLiveData<String> liveDataWithPostText =
            new MutableLiveData<>("Load random post");

    final MutableLiveData<Long> liveDataClockView =
            new MutableLiveData<>(currentTimeMillis());

    /* создаваемый объект Observable с таймером - на нем будет производиться
    * автоматический вызов emit() с заданным интервалом
    */

    Observable<Long> clockView = Observable.interval(1000, TimeUnit.MILLISECONDS)
            .doOnNext(value ->liveDataClockView.postValue(currentTimeMillis()))
            .doOnDispose(()->Log.e(TAG,"Disposed!" ))
            .onErrorComplete();


    /* Хандлер подписки для него
     */
    Disposable clockSubscription = clockView.subscribe();

    /* Хандлер подписки для ретрофит запроса
     */
    Disposable currentSubscription;

    public void reloadPost() {
        int from = Default.nextInt(1,40);
        int count = Default.nextInt(1,50);
        Observable <List<String>> posts = RetrofitRepository.getPostTitlesRange(from,from+count);
        /* В получаемом от ретрофита объекте УЖЕ может быть проведена серьезная обработка
        * сырых полученных данных - к примеру в нашем случае полученный с сервера
        * список объектов преобразуется к списку Strings c фильтрацией (см. getPostTitlesRange()).
        * Созданный объект для вызова запроса хранится лениво, не активируется до subscribe()
        *  и может быть модифицирован дополнительными операторами или сцеплен с другими.
        */
        unsubscribeIfNeeded(currentSubscription);
        currentSubscription = posts.doOnNext(result-> liveDataWithPostText.postValue(result.toString()))
                .doOnError(error-> liveDataWithPostText.postValue(error.toString()))
                .doOnDispose(()->Log.i(TAG,"Disposed!" ))
                .onErrorComplete()
                .subscribe();
        /*  обработка подготовленного вызова может быть сцеплена с выполнением конкретных
        *   операторов, доступных в месте и в момент вызова
        *  К примеру, можно сцепить несколько запросов и (или) использовать результат одного
        *  в другом - операторы concat, concatMap, combine, zip
        */

    }


    public void forceErrorRequest() {
        Observable <List<PostName>> posts = RetrofitRepository.getPostTitlesWithError();
        unsubscribeIfNeeded(currentSubscription);
        currentSubscription = posts.doOnNext(result-> liveDataWithPostText.postValue(result.toString()))
                .doOnError(error-> liveDataWithPostText.postValue(error.toString()))
                .doOnDispose(()->Log.i(TAG,"Disposed!" ))
                .onErrorComplete()
                .subscribe();
    }

    private void unsubscribeIfNeeded(Disposable subscription) {
        if (subscription != null && !subscription.isDisposed())
            subscription.dispose();
    }


    /* Вариант автоматической отписки при очистке вьюмодели (при ликвидации фрагмента)
    */
    @Override
    protected void onCleared() {
        super.onCleared();
        Log.i(TAG, "onCleared: =");
        unsubscribeIfNeeded(currentSubscription);
        unsubscribeIfNeeded(clockSubscription);
    }

}