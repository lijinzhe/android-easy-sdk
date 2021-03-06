package com.ueueo.sample.rxjava;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.ueueo.log.Logger;
import com.ueueo.sample.R;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RxJavaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView textView = (TextView) findViewById(R.id.text);

        Logger.init("lijinzhe");

//        Logger.i("pifubao   %s","  hello world!");
//        Logger.tag("lijinzhe").i("lijinzhe   %s","  hello world!");
//        Logger.i("3333333   %s","  hello world!");

//        Logger.tag("lijinzhe").method(0).header("你好啊").footer("佛卡是江东父老看拉萨借读费").object(new User());

//        Logger.tag("lijinzhe").method(0).footer("11112312312312312").footer("213123123123123").object(new User());

//        Map<String,User> userMap = new HashMap<>();
//        userMap.put("user1",new User());
//        userMap.put("user2",new User());
//        Logger.tag("lijinzhe").method(0).header("11112312312312312").headerObject(new User()).object(userMap);

        final Observable<String> o1 = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Logger.i("Observable 1111");

                subscriber.onNext("1111");
                subscriber.onCompleted();
                Logger.i("Observable onCompleted 1111");

                throw new RuntimeException("aaaaaaa");
            }
        });
        final Observable<String> o2 = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Logger.i("Observable 2222");
                subscriber.onNext("2222");
                subscriber.onCompleted();
                Logger.i("Observable onCompleted 2222");
            }
        });
        final Observable<String> o3 = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Logger.i("Observable 3333");
                subscriber.onNext("3333");
                subscriber.onCompleted();
            }
        });

        Observable.just("lijinzhe").subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        Logger.i("doOdoOnSubscribe");
                    }
                })
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Logger.i("doOnNext 11111");
                    }
                })
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        Logger.i("Observable flat map 1111  ");
//                        return o1.subscribeOn(AndroidSchedulers.mainThread());
                        return o1;
                    }
                })
               .onErrorReturn(new Func1<Throwable, String>() {
                   @Override
                   public String call(Throwable throwable) {
                       return "ueueo";
                   }
               })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Logger.i("doOnNext 22222"+s);
                    }
                })
//                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        Logger.i("Observable flat map 2222"+s);
                        return o2.observeOn(Schedulers.io());
                    }
                })
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        Logger.i("Observable flat map 3333");
                        return o3;
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Logger.i("Observable next %s", s);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.i("ERROR "+throwable.getMessage());
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        Logger.i("Observable complete");
                    }
                });

//        Observable.zip(o1, o2, new Func2<String, String, Object>() {
//            @Override
//            public Object call(String s, String s2) {
//                Logger.i("Observable zip next s1:%s  s2:%s",s,s2);
//                return "laslkdlasd";
//            }
//        }).subscribeOn(Schedulers.io())
//        .observeOn(AndroidSchedulers.mainThread())
//        .subscribe(new Action1<Object>() {
//            @Override
//            public void call(Object o) {
//                Logger.i(o.toString());
//            }
//        });

//        Observable.merge(o1,o2)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<String>() {
//            @Override
//            public void call(String s) {
//                Logger.i("onNext %s",s);
//            }
//        });

    }

    public static class User {
        public int id = 1;
        public String name = "李金哲";
        public int age = 33;
    }
}
