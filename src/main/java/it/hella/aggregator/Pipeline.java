package it.hella.aggregator;

import com.sun.istack.internal.NotNull;
import it.hella.aggregator.reactive.csv.CsvDataSource;
import lombok.*;
import reactor.core.Disposable;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Builder
public class Pipeline<T, V> {

    @Singular
    @NotNull
    List<Aggregator<T, V>> aggregators;

    @NotNull
    CsvDataSource<T> csvDataSource;

    private Map<String, DisposableIndex> subscribers;

    @Data
    @AllArgsConstructor
    private static class DisposableIndex {
        private Disposable disposable;
        private Integer aggregatorIndex;
    }

    private class AggregatorListener {

        private FluxSink<V> sink;
        private AtomicInteger activeAggregators;

        void setSink(FluxSink<V> sink){
            this.sink = sink;
            activeAggregators = new AtomicInteger(aggregators.size());
        }

        void complete(String name){
            Integer aggregatorIndex = subscribers.get(name).getAggregatorIndex();
            sink.next(aggregators.get(aggregatorIndex).getValue());
            if (activeAggregators.decrementAndGet() == 0){
                sink.complete();
            }
        }
    }
    private final AggregatorListener listener = new AggregatorListener();

    public void aggregate(Path path, Consumer<V> consumer) {

        subscribers = new HashMap<>();
        Scheduler s = Schedulers.elastic();
        ConnectableFlux<T> connectableFlux = csvDataSource.
                stream(path).publish();
        Flux<T> broadcastConcurrentPublisher = connectableFlux.publishOn(s);
        int n = 0;
        for(Aggregator<T, V> c : aggregators){
            final int m = n;
            DisposableIndex disposableIndex = new DisposableIndex(
                    broadcastConcurrentPublisher.
                        subscribe(c, Throwable::printStackTrace,
                            () -> listener.complete(c.getName())),
                    m);
            subscribers.put(c.getName(), disposableIndex);
            n++;
        }
        listenerFlux(consumer);
        connectableFlux.connect();

    }

    private void listenerFlux(Consumer<V> consumer) {
        Flux<V> flux = Flux.create(listener::setSink, FluxSink.OverflowStrategy.BUFFER);
        flux.subscribe(consumer);
    }

}
