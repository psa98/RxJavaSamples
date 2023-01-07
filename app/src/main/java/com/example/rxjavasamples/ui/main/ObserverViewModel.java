package com.example.rxjavasamples.ui.main;

import static com.example.rxjavasamples.MainActivity.TAG;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Emitter;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class ObserverViewModel extends ViewModel {

    MutableLiveData<String> allTicks = new MutableLiveData<>("Not subscribed");

    public Observer<Integer> eachTickObserver = new Observer<Integer>() {
        int lastValue = 0;

        @Override
        public void onSubscribe(@NonNull Disposable d) {
            allTicks.postValue("Subscribing...waiting for data");
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onNext(@NonNull Integer i) {
            lastValue = i;
            allTicks.postValue("All Ticks count =" + i);
        }

        @Override
        public void onError(@NonNull Throwable e) {
            allTicks.postValue("All Ticks count ERROR =" + e.getLocalizedMessage());

        }

        @Override
        public void onComplete() {
            allTicks.postValue("Final all Ticks count =" + lastValue + "\nDone! ");

        }
    };


    public Emitter<Boolean> emitter = new Emitter<Boolean>() {
        @Override
        public void onNext(@NonNull Boolean value) {

        }

        @Override
        public void onError(@NonNull Throwable error) {

        }

        @Override
        public void onComplete() {

        }
    };

    void onClick(View button) {
        Log.e(TAG, "onClick: =" + button.toString());
    }


}