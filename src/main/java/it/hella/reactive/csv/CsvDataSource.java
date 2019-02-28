package it.hella.reactive.csv;

import lombok.Builder;
import lombok.NonNull;
import reactor.core.publisher.Flux;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.stream.BaseStream;

/**
 * A reactive stream of records extracted from an in-memory or
 * persisted data source.
 *
 * @param <T> the type parameter
 */
@Builder
public class CsvDataSource<T> {

    @NonNull
    private Function<String[], T> mapper;
    @Builder.Default
    private String separator = ",";

    /**
     * Generated a reactive stream from a file.
     *
     * @param path the path
     * @return the flux
     */
    public final Flux<T> stream(Path path){
        return lines(path).map(line -> mapper.apply(line.split(separator)));
    }

    /**
     * Generated a reactive stream from a list.
     *
     * @param records the records
     * @return the flux
     */
    public final Flux<T> stream(Iterable<String> records){
        return lines(records).map(line -> {
            mapper.apply(line.split(separator));
            return mapper.apply(line.split(separator));
        });
    }

    private static final Flux<String> lines(Path path) {
        return Flux.using(() -> Files.lines(path),
                Flux::fromStream,
                BaseStream::close
        );
    }

    private static final Flux<String> lines(Iterable<String> records){
        return Flux.fromIterable(records);
    }
}
