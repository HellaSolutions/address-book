package it.hella.aggregator.reactive.csv;

import org.junit.BeforeClass;
import org.junit.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Function;

public class CsvDataSourceTest {

    private static final ClassLoader classLoader = CsvDataSourceTest.class.getClassLoader();

    private static Path path;

    @BeforeClass
    public static void beforeClass(){
        path = Paths.get(classLoader.getResource("AddressBook.txt").getPath().substring(1));
    }

    @Test
    public void testFileDataSource() {

        CsvDataSource.<String[]>builder().
                mapper(Function.identity()).
                build().
                stream(path).
                subscribe(l -> System.out.println(Arrays.toString(l)));
    }

    @Test
    public void testConcurrent() {

        Scheduler s = Schedulers.newParallel("parallel-scheduler");
        ConnectableFlux<String[]> connectableFlux = CsvDataSource.<String[]>builder().
                mapper(Function.identity()).
                build().
                stream(path).publish();
        Flux<String[]> broadcastConcurrentPublisher = connectableFlux.publishOn(s);
        broadcastConcurrentPublisher.subscribe(line -> System.out.println(Thread.currentThread().getId() + " A " + Arrays.toString(line)),e -> {}, () -> {});
        broadcastConcurrentPublisher.subscribe(line -> System.out.println(Thread.currentThread().getId() + " B " + line), e -> {}, () -> {});
        broadcastConcurrentPublisher.subscribe(line -> System.out.println(Thread.currentThread().getId() + " C " + line), e -> {}, () -> {});
        broadcastConcurrentPublisher.subscribe(line -> System.out.println(Thread.currentThread().getId() + " D " + line), e -> {}, () -> {});
        connectableFlux.connect();
    }

}
