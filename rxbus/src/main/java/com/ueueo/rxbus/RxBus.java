package com.ueueo.rxbus;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by Lee on 16/7/14.
 */
public class RxBus {
    private static final Subject<Object, Object> bus = new SerializedSubject<>(PublishSubject.create());

    public static void post(Object o) {
        bus.onNext(o);
    }

    public static <T> Observable<T> toObserverable(Class<T> eventType) {
        return bus.ofType(eventType);
    }

}
