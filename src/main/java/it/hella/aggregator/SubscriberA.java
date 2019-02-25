package it.hella.aggregator;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;

import java.time.LocalDate;

public class SubscriberA extends BaseSubscriber<String> {

    private int maxAge = 0;
    private LocalDate now = LocalDate.now();



}
