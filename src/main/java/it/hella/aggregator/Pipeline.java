package it.hella.aggregator;

import com.sun.istack.internal.NotNull;
import it.hella.aggregator.reactive.csv.CsvDataSource;
import lombok.Builder;
import lombok.Singular;
import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.nio.file.Path;
import java.util.*;

@Builder
public class Pipeline<T, V> {

    @Singular
    @NotNull
    List<Aggregator<T, ?>> aggregators;

    @NotNull
    CsvDataSource<T> csvDataSource;

    private Map<Integer, Disposable> subscribers;

    public Disposable aggregate(Path path) {

        subscribers = new HashMap<>();
        Scheduler s = Schedulers.newParallel(UUID.randomUUID().toString());
        ConnectableFlux<T> connectableFlux = csvDataSource.
                stream(path).publish();
        Flux<T> broadcastConcurrentPublisher = connectableFlux.publishOn(s);
        int n = 0;
        for(Aggregator<T, ?> c : aggregators){
            subscribers.put(n++, broadcastConcurrentPublisher.subscribe(c));
        }
        return connectableFlux.connect();

    }

    public Optional<?> getResult(Integer n) {
        if (subscribers.containsKey(n)){
            while(!subscribers.get(n).isDisposed()){}
            return Optional.of(aggregators.get(n).getValue());
        }else{
            return Optional.empty();
        }
    }

}
