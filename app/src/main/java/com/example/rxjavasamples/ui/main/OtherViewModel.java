package com.example.rxjavasamples.ui.main;

import static com.example.rxjavasamples.MainActivity.TAG;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.disposables.Disposable;

public class OtherViewModel extends ViewModel {


    MutableLiveData<String> allTicks = new MutableLiveData<>("Not subscribed");
    int lastValue = 0;
    Disposable dispose;
    ObservableEmitter<Integer> keyObserver;

    Observable<Integer> clickObservable = Observable.<Integer>create(emitter -> {
                keyObserver = emitter;
            })
            .debounce(300, TimeUnit.MILLISECONDS)
            .take(15)
            //.distinct()
            //.groupBy()
            .skip(2)

            .doOnNext(i -> {
                Log.e(TAG, "onNext: =" + i);
                lastValue += i;
                allTicks.postValue("All Ticks count =" + lastValue);
            }).doOnComplete(() -> {
                allTicks.postValue("Final all Ticks count =" + lastValue + "\nDone! ");
                lastValue = 0;
            }).doOnError(e -> {
                allTicks.postValue("All Ticks count ERROR =" + e.getMessage());
            }).onErrorComplete();


    void subscribe() {
        if (dispose == null || dispose.isDisposed())
            dispose = clickObservable.subscribe();

    }

    void onClick(View button) {
        String tag = (String) button.getTag();
        Log.e(TAG, "onClick: =" + tag);
        if (tag.equals("click")) {
            if (keyObserver != null && !keyObserver.isDisposed())
                keyObserver.onNext(1);
        }
        if (tag.equals("error")) {
            if (keyObserver != null && !keyObserver.isDisposed())
                keyObserver.onError(new IllegalStateException());
            dispose.dispose();
            dispose = clickObservable.subscribe(); //переподписка
        }
        if (tag.equals("complete")) {
            if (keyObserver != null && !keyObserver.isDisposed())
                keyObserver.onComplete();
            dispose.dispose();
            dispose = clickObservable.subscribe(); //переподписка
        }
    }

}