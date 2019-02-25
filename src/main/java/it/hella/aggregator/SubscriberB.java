package it.hella.aggregator;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.time.LocalDate;

public class SubscriberB implements Subscriber<String> {

    @Override
    public void onSubscribe(Subscription s) {
        //nothing to do
    }

    @Override
    public void onNext(String s) {
        System.out.println(Thread.currentThread().getId());
    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void onComplete() {

    }
}
