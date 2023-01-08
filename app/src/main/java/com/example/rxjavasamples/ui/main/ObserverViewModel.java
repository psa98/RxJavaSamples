package com.example.rxjavasamples.ui.main;

import static com.example.rxjavasamples.MainActivity.TAG;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.disposables.Disposable;

public class ObserverViewModel extends ViewModel {

    public static final int DEFAULT_TAKE = 15;
    int debounceParam = 200;
    int takeParam = DEFAULT_TAKE;
    int skipParam = 1;
    MutableLiveData<String> allTicks = new MutableLiveData<>("Not subscribed");
    MutableLiveData<String> logStringData = new MutableLiveData<>("...");
    String logString ="...";
    int lastValue = 0;
    Disposable dispose;
    ObservableEmitter<Integer> keyObserver;

    Observable<Integer> clickObservable = initObservable();
    Observable<String> mappedObservable;
    @SuppressLint("DefaultLocale")
    Observable<Integer> initObservable() {
        Observable<Integer> observable= Observable.<Integer>create(emitter -> keyObserver = emitter)
            .debounce(debounceParam, TimeUnit.MILLISECONDS)
            .take(takeParam)
            .skip(skipParam)
            .doOnSubscribe(disposable -> allTicks.postValue("Subscribed, waiting"))
            .doOnNext(i -> {
                Log.e(TAG, "onNext: =" + i);
                lastValue += i;
                allTicks.postValue("All Ticks count =" + lastValue);
            }).doOnComplete(() -> {
                allTicks.postValue("Final all Ticks count =" + lastValue + "\nDone! ");
                lastValue = 0;
                resubscribe();
            })  .doOnError(e -> {
                allTicks.postValue("Got ERROR =" + e.toString());
                lastValue = 0;
                resubscribe();
            }).onErrorComplete();

    mappedObservable = observable
            .map(integer -> String.format("Click# %d %s", lastValue, new Date()))
            .doOnNext(string->{
                    Log.i(TAG, "mapped observable - click registered:"+string);
                    logString="\n"+string+logString;
                    logStringData.postValue(logString);
            }).doOnComplete(()-> {
                    logString = "\n" + "Done! ==================" + "\n" + logString;
                    logStringData.postValue(logString);
            });


    return observable;
    }
    void subscribe() {
        if (dispose == null || dispose.isDisposed()){
            dispose = clickObservable.subscribe();
            mappedObservable.subscribe();}

    }

    void onClick(View button, int clickCounter) {
        String tag = (String) button.getTag();
        if (tag.equals("click")) {
            if (keyObserver != null && !keyObserver.isDisposed())
                keyObserver.onNext(clickCounter);
        }
        if (tag.equals("error")) {
            if (keyObserver != null && !keyObserver.isDisposed())
                keyObserver.onError(new IllegalStateException());

        }
        if (tag.equals("complete")) {
            if (keyObserver != null && !keyObserver.isDisposed())
                keyObserver.onComplete();

        }
    }

    private void resubscribe() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed((Runnable) () -> {
            if (dispose != null && !dispose.isDisposed())
                dispose.dispose();
            clickObservable =initObservable(); //подписываемся с новыми параметрами
            dispose = clickObservable.subscribe();
            mappedObservable.subscribe();
        }, 2000);
        //переподписка через 2 секунды
    }

}