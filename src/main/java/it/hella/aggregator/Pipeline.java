package it.hella.aggregator;

import it.hella.reactive.csv.CsvDataSource;
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

/**
 * The type Pipeline.
 *
 * @param <T> the type parameter
 * @param <V> the type parameter
 */
@Builder
public class Pipeline<T> {

    /**
     * The Aggregators.
     */
    @Singular
    @NonNull
    List<Aggregator<T, ?>> aggregators;

    /**
     * The Csv data source.
     */
    @NonNull
    CsvDataSource<T> csvDataSource;

    private Map<String, DisposableIndex> subscribers;

    @Data
    @AllArgsConstructor
    private static class DisposableIndex {
        private Disposable disposable;
        private Integer aggregatorIndex;
    }

    /**
     * Each Aggregator subscription is accompanied with
     * a onComplete implementation that calls the complete
     * method. The listener retrieves the Aggregator and
     * publishes it. This produces a Flux of completed
     * Aggregator(s).
     *
     * The user provides a consumer for this Flux to intercept
     * the Aggregators results.
     *
     */
    private class AggregatorListener {

        private FluxSink<Aggregator<T, ?>> sink;
        private AtomicInteger activeAggregators;

        /**
         * Set sink.
         *
         * @param sink the sink
         */
        void setSink(FluxSink<Aggregator<T, ?>> sink){
            this.sink = sink;
            activeAggregators = new AtomicInteger(aggregators.size());
        }

        /**
         * Complete.
         *
         * @param name the name
         */
        void complete(String name){
            Integer aggregatorIndex = subscribers.get(name).getAggregatorIndex();
            sink.next(aggregators.get(aggregatorIndex));
            if (activeAggregators.decrementAndGet() == 0){
                sink.complete();
            }
        }
    }
    private final AggregatorListener listener = new AggregatorListener();

    /**
     * Aggregate.
     *
     * @param records  the records as iterable
     * @param consumer the consumer for a Flux of completed Aggregators
     */
    public void aggregate(List<String> records, Consumer<Aggregator<T,?>> consumer) {

        runPipeline(csvDataSource.
                stream(records).publish(), consumer);

    }

    /**
     * Aggregate.
     *
     * @param path     the records as a CSV file
     * @param consumer the consumer for a Flux of completed Aggregators
     */
    public void aggregate(Path path, Consumer<Aggregator<T,?>> consumer) {

        runPipeline(csvDataSource.
                stream(path).publish(), consumer);

    }

    private void runPipeline(ConnectableFlux<T> connectableFlux, Consumer<Aggregator<T, ?>> consumer){
        subscribers = new HashMap<>();
        Scheduler s = Schedulers.elastic();
        Flux<T> broadcastConcurrentPublisher = connectableFlux.publishOn(s);
        int n = 0;
        for(Aggregator<T, ?> c : aggregators){
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

    private void listenerFlux(Consumer<Aggregator<T, ?>> consumer) {
        Flux<Aggregator<T, ?>> flux = Flux.create(listener::setSink, FluxSink.OverflowStrategy.BUFFER);
        flux.subscribe(consumer);
    }

    /**
     * Blocking utility method that retrieves the value computed from a named Aggregator.
     * Used in tests to avoid time-wasting Thread.sleep calls
     *
     * @param name the name
     * @return the result
     */
    public Object getResult(String name) {
        if (subscribers == null){
            throw new IllegalStateException(String.format("No subscribers, you have to call the aggregate method before"));
        }
        if (subscribers.containsKey(name)){
            DisposableIndex disposableIndex = subscribers.get(name);
            while(!disposableIndex.getDisposable().isDisposed()){}
            return aggregators.get(disposableIndex.getAggregatorIndex()).getValue();
        }else{
            throw new IllegalArgumentException(String.format("Unrecognized aggregator id: " + name));
        }
    }


}

