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

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class
FlowableViewModel extends ViewModel {

    public static final int DEFAULT_TAKE = 6;
    public static final int DEFAULT_DELAY = 1000;
    public static final int RESUBSCRIBE_TIME = 1000;
    int delayParam = DEFAULT_DELAY;
    int takeParam = DEFAULT_TAKE;
    MutableLiveData<String> allTicks = new MutableLiveData<>("Not subscribed");
    MutableLiveData<String> logStringData = new MutableLiveData<>("...");
    String logString ="...";
    int counter = 0;

    /*переменная - хандлер подписки на  Flowable, позволяет выполнить отписку
     * при необходимости
     */
    Disposable dispose;

    /*
     *эта переменная (интерфейс) передается во Flowable при создании, на ней
     * будет осуществляться вызов onNext(Integer i), onComplete().
     */
    FlowableEmitter<Integer> keyObserver;

    /* создаваемый объект Flowable c изучаемыми свойствами
     */
    Flowable<Integer> clickFlowable;
    private Single<List<Integer>> totals;
    private Single<Integer> totalSum;

    Flowable<Integer> initFlowable() {
        counter=0;
        Flowable<Integer> flowable = Flowable
                .<Integer>create(emitter -> keyObserver = emitter,

                /*обработка onNext событий в примере осуществляется с выводом в лог с 2000 мс
                 задержкой, Flowable обеспечивает необходимую буферизацию
                */
                BackpressureStrategy.BUFFER)
                /*
                 в примере отбираются только неповторяющиеся случайные числа поступающие
                 на вход в onNext. При поступлении повторяющегося обработка прекращается
                 и обработка дальше по цепочке операторов не идет, клик не засчитан
                */
                .distinct()
                /*
                 Этот оператор сам вызывает onComplete после получения заданного количества
                 onNext, завершая работу.
                */
                .take(takeParam)
                /*
                * По умолчанию обработка onNext и других операторов осуществляется там же
                *  где был создан Flowable/Observable и вызвана подписка, то есть на Android main
                *  потоке. Если там вызвать долгую блокирущую операцию, это приведет
                *  к ANR сбою. Оператор  observeOn с указанием Schedulers.computation(),
                *  Schedulers.io() потоков (есть и доп параметры - приоритет в тч)
                *  перенесет обработку в нужные фоновые пулы потоков.
                */
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                /*
                Этот оператор вызывается при подписке, позволяя сохранить Disposable
                и отслеживать жизненный цикл Flowable/Observable
                */
                .doOnSubscribe(disposable -> allTicks.postValue("Subscribed, waiting"))
                /* Основной оператор, выполняющий полезную обработку поступающих данных
                 *  В данном случае выполняется вывод данных через лайфдату в поле вывода
                 * на главном потоке
                 */
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(i -> {
                    counter++;
                    allTicks.postValue("Ticks count = "+counter+
                            ", last random value =" + i);
                /* Дополнительный оператор, выполняющий полезную обработку поступающих данных
                 *  В данном случае выполняется вывод данных в лог имитирущий
                 *  медленную их обработку на пуле фоновых потоков
                 */
                }).observeOn(Schedulers.io())
                .doAfterNext(i -> {
                    @SuppressLint("DefaultLocale")
                    String s = String.format("value=%d, %s",
                            i, timestampDateShort());
                    postInLog (s,true);
                })

                /* Завершающий оператор. После вызова onComplete повторная передача
                 * onNext будет игнорироваться, повторные onComplete|onError вызовут
                 * ошибку. Операторы выполняются в последнем указанном потоке
                 */
                .doOnComplete(() -> {
                    allTicks.postValue("Final ticks count =" + counter + "\nDone! ");
                    Log.i(TAG, "Thread: ="+Thread.currentThread());
                    postInLog("Done! ===========",false);
                    resubscribe();
                    /* Оператор обработки onError. Факт наличия обработки не ведет автоматически
                     к "проглатыванию" ошибки! Это поведение определяется другими операторами
                     */
                }).doOnError(e -> {
                    allTicks.postValue("Got ERROR =" + e.toString());
                    resubscribe();

                })
                /* Оператор варианта действий по итогам onError. В данном случае
                    ошибка дальше не идет, а превращается в  вызов OnComplete,
                    есть и другие варианты
                 */
                .onErrorComplete();

        /* Для данного Flowable предполагается создать два сцепленных Single, получающих итоги -
        *  список всех  значений Integer и сумму. Это можно сделать через share()
        *  Одно значение могло бы произведено непосредственно, через точку,
        * totals= flowable.toSortedList()
         *  но второй последовательный Single от того же объекта никогда не получил бы onComplete.
        */
        Flowable<Integer> copyFlowable = flowable.share();
        totals= copyFlowable
                .toSortedList().onErrorReturnItem(new ArrayList<>(0))
                .doOnSuccess(list-> postInLog("Totals list:\n"+list,false));

        totalSum = copyFlowable.reduce((sum, item) -> {
            sum += item;
            return sum;
            /*Maybe/Single объект имеет сокращенный набор операторов, в частности doOnSuccess(value)
            *  вместо doOnNext|onComplete, который у Maybe может быть никогда и не вызван
            */
            }).doOnSuccess(sum-> postInLog("Total sum:\n"+sum,false))
           /* Maybe может быть преобразован  в Single*/
                .toSingle().onErrorReturnItem(0);
        return flowable;
    }

    private void postInLog(String s,Boolean slowProcessing) {
        if (slowProcessing) {
            try {
                Thread.sleep(delayParam);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        logString="\n"+s+logString;
        logStringData.postValue(logString);
    }

    void startFlow(){
        clickFlowable =initFlowable();
        dispose = clickFlowable.subscribe();
        totals.subscribe();
        totalSum.subscribe();
    }

    void onClick (View button, int randomValue) {
        String tag = (String) button.getTag();
        if (tag.equals("click")) {
            if (keyObserver != null && !keyObserver.isCancelled())
                keyObserver.onNext(randomValue);
        }
        if (tag.equals("error")) {
            if (keyObserver != null && !keyObserver.isCancelled())
                keyObserver.onError(new IllegalStateException());

        }
        if (tag.equals("complete")) {
            if (keyObserver != null && !keyObserver.isCancelled())
                keyObserver.onComplete();

        }
    }

    private void resubscribe() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed((Runnable) () -> {

            if (dispose != null && !dispose.isDisposed())
                dispose.dispose();
             //подписываемся с новыми параметрами
            startFlow();
        }, RESUBSCRIBE_TIME);
        //переподписка через 1 секундy
    }

}