package com.example.rxjavasamples.ui.main;

import static com.example.rxjavasamples.MainActivity.TAG;
import static com.example.rxjavasamples.ui.main.Utils.timestampDateShort;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.disposables.Disposable;

public class ObservableViewModel extends ViewModel {

    public static final int DEFAULT_TAKE = 15;
    public static final int DEFAULT_DEBOUNCE = 200;
    public static final int RESUBSCRIBE_TIME = 2000;
    public static final int DEFAULT_SKIP = 1;
    int debounceParam = DEFAULT_DEBOUNCE;
    int takeParam = DEFAULT_TAKE;
    int skipParam = DEFAULT_SKIP;
    MutableLiveData<String> allTicks = new MutableLiveData<>("Not subscribed");
    MutableLiveData<String> logStringData = new MutableLiveData<>("...");
    String logString ="...";
    int lastValue = 0;
    /*переменная - хандлер подписки на Observable, позволяет выполнить отписку
     * при необходимости
     */
    Disposable dispose;
    /*
     *эта переменная (интерфейс) передается в Observable при создании, на ней
     * будет осуществляться вызов onNext(Integer i), onComplete().
     */
    ObservableEmitter<Integer> keyObserver;
    /* создаваемый объект Observable  c изучаемыми свойствами
     */
    Observable<Integer> clickObservable = initObservable();
    /* сцепленный с ним объект Observable, получающий те же события
     */
    Observable<String> chainedObservable;
    @SuppressLint("DefaultLocale")
    Observable<Integer> initObservable() {
        Observable<Integer> observable= Observable.<Integer>create(emitter -> keyObserver = emitter)
                /* Оператор блокирует слишком частые, чаще заданного параметра, onNext события
                 */
            .debounce(debounceParam, TimeUnit.MILLISECONDS)
                /*
                 *Этот оператор пропускает первые n событий onNext.
                 * Порядок операторов играет значение!
                 */
                .skip(skipParam)
                /*
                *Этот оператор сам вызывает onComplete после получения заданного количества
                *onNext, завершая работу.
                */
            .take(takeParam)
                /*
                Этот оператор вызывается при подписке, позволяя сохранить Disposable
                и отслеживать жизненный цикл Flowable/Observable
                */
            .doOnSubscribe(disposable -> allTicks.postValue("Subscribed, waiting"))
                /* Основной оператор, выполняющий полезную обработку поступающих данных
                 *  В данном случае выполняется вывод данных в логкат и через лайфдату в поле
                 *  вывода
                 */
            .doOnNext(i -> {
                Log.i(TAG, "onNext: =" + i);
                lastValue += i;
                allTicks.postValue("Ticks count =" + lastValue);
                /* Завершающий оператор. После вызова onComplete повторная передача
                 * onNext будет игнорироваться, повторные onComplete|onError вызовут
                 * ошибку
                 */
            }).doOnComplete(() -> {
                allTicks.postValue("Final ticks count =" + lastValue + "\nDone! ");
                lastValue = 0;
                resubscribe();
                /* Оператор обработки onError. Факт наличия обработки не ведет автоматически
                 к "проглатыванию" ошибки! Это поведение определяется другими операторами
                 */
            })  .doOnError(e -> {
                allTicks.postValue("Got ERROR =" + e.toString());
                lastValue = 0;
                resubscribe();
                /* Оператор варианта действий по итогам onError. В данном случае
                 *    ошибка дальше не идет, а превращается в  вызов OnComplete,
                 *   есть и другие варианты
                 */
            }).onErrorComplete();
        /* Исходный объект работает с Integer, в данном случае оператором map создается
         * сцепленый Observable<String>, в onDoNext которого будут приходить строки,
         * полученные функцией преобразования.
         */
    chainedObservable = observable
            .map(integer -> String.format("Click# %d %s", lastValue, timestampDateShort()))
            .doOnNext(string->{
                /*
                 * ( используется инкрементируемый в модели lastValue вместо передаваемого integer поскольку
                 *  в данном примере передаваемый из фрагмента integer всегда единица)
                 *  Запись ведется в лог внизу экрана
                 */
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
            chainedObservable.subscribe();}
    }

    void onClick(View button) {
        String tag = (String) button.getTag();
        if (tag.equals("click")) {
            if (keyObserver != null && !keyObserver.isDisposed())
                keyObserver.onNext(1);
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
        handler.postDelayed(() -> {
            if (dispose != null && !dispose.isDisposed())
                dispose.dispose();
            clickObservable =initObservable(); //подписываемся с новыми параметрами
            dispose = clickObservable.subscribe();
            chainedObservable.subscribe();
        }, RESUBSCRIBE_TIME);
        //переподписка осуществляется через 2 секунды после OnError, OnComplete.
    }

}