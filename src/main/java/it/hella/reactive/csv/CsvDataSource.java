package it.hella.reactive.csv;

import lombok.Builder;
import lombok.NonNull;
import reactor.core.publisher.Flux;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.stream.BaseStream;

@Builder
public class CsvDataSource<T> {

    @NonNull
    private Function<String[], T> mapper;
    @Builder.Default
    private String separator = ",";

    public final Flux<T> stream(Path path){
        return lines(path).map(line -> mapper.apply(line.split(separator)));
    }

    public final Flux<T> stream(List<String> records){
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

    private static final Flux<String> lines(List<String> records){
        return Flux.fromIterable(records);
    }
}
