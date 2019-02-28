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
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * The type Pipeline.
 *
 * @param <T> the type parameter
 */
@Builder
public class Pipeline<T> {

    /**
     * The Aggregators.
     */
    @Singular
    @NonNull
    private List<Aggregator<T, ?>> aggregators;

    /**
     * The Csv data source.
     */
    @NonNull
    private CsvDataSource<T> csvDataSource;

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
         * Called when an aggregator is completed.
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

        /**
         * Is completed boolean.
         *
         * @return the boolean
         */
        boolean isCompleted() {
            return activeAggregators.get() == 0;
        }

    }
    private final AggregatorListener listener = new AggregatorListener();

    /**
     * Aggregate an in-memory data set.
     *
     * @param records  the records as iterable
     * @param consumer the consumer for a Flux of completed Aggregators
     */
    public void aggregate(Iterable<String> records, Consumer<Aggregator<T,?>> consumer) {

        runPipeline(csvDataSource.
                stream(records).publish(), consumer);

    }

    /**
     * Aggregate a data set from disk.
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
        ConnectableFlux<Aggregator<T, ?>> fluxListener = fluxListener(consumer);
        fluxListener.connect();
        connectableFlux.connect();

    }

    private ConnectableFlux<Aggregator<T, ?>> fluxListener(Consumer<Aggregator<T, ?>> consumer) {
        ConnectableFlux<Aggregator<T, ?>> connectableFlux =
                Flux.create(listener::setSink, FluxSink.OverflowStrategy.BUFFER)
                .publish();
        connectableFlux.subscribe(consumer);
        return connectableFlux;
    }

    public Callable<Boolean> complete(){
        return () -> listener.isCompleted();
    }

}
