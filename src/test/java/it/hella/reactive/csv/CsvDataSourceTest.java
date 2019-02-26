package it.hella.reactive.csv;

import it.hella.addressbook.test.util.DataBox;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CsvDataSourceTest {

    private static Logger log = LoggerFactory.getLogger(CsvDataSourceTest.class);

    private static final int RANDOM_SAMPLE_SIZE = 1000;

    private static final ClassLoader classLoader = CsvDataSourceTest.class.getClassLoader();

    private static Path path;

    @BeforeClass
    public static void beforeClass(){
        path = Paths.get(classLoader.getResource("AddressBook.txt").getPath().substring(1));
    }

    @Test
    public void testEmptyDataSource() {

        Path path = Paths.get(classLoader.getResource("EmptyAddressBook.txt").getPath().substring(1));
        Flux<String[]> flux = CsvDataSource.<String[]>builder().
                mapper(Function.identity()).
                build().
                stream(path);
        StepVerifier.create(
                flux).expectNextCount(0).
                verifyComplete();

    }

    @Test
    public void testSampleDataSource() {

        Flux<String[]> flux = CsvDataSource.<String[]>builder().
                mapper(Function.identity()).
                build().
                stream(path);
        StepVerifier.create(
                flux.doOnNext(s -> log.info(Arrays.toString(s))))
                .assertNext(s -> is(new String[]{"Bill McKnight", " Male", " 16/03/77"}))
                .assertNext(s -> is(new String[]{"Paul Robinson", " Male", " 15/01/85"}))
                .assertNext(s -> is(new String[]{"Gemma Lane", " Female", " 20/11/91"}))
                .assertNext(s -> is(new String[]{"Sarah Stone", " Female", " 20/09/80"}))
                .assertNext(s -> is(new String[]{"Wes Jackson", " Male", " 14/08/74"}))
                .verifyComplete();

    }

    @Test
    public void testMappedDataSource() {

        Flux<String[]> flux = CsvDataSource.<String[]>builder().
                mapper(s -> {
                    return Arrays.asList(s).
                            stream().
                            map(f -> f.trim()).
                            collect(Collectors.toList()).
                            toArray(new String[s.length]);
                }).
                build().
                stream(path);
        StepVerifier.create(
                flux.doOnNext(s -> log.info(Arrays.toString(s))))
                .assertNext(s -> is(new String[]{"Bill McKnight", "Male", "16/03/77"}))
                .assertNext(s -> is(new String[]{"Paul Robinson", "Male", "15/01/85"}))
                .assertNext(s -> is(new String[]{"Gemma Lane", "Female", "20/11/91"}))
                .assertNext(s -> is(new String[]{"Sarah Stone", "Female", "20/09/80"}))
                .assertNext(s -> is(new String[]{"Wes Jackson", "Male", "14/08/74"}))
                .verifyComplete();

    }

    @Test
    public void testRandomDataSource(){
        List<String> records =
                DataBox.getRandomCsvAddressBook(RANDOM_SAMPLE_SIZE, ", ");
        Flux<String[]> flux = CsvDataSource.<String[]>builder().
                mapper(Function.identity()).
                build().
                stream(records);
        StepVerifier.create(flux)
                .recordWith(ArrayList::new)
                .thenConsumeWhile(Objects::nonNull)
                .consumeRecordedWith(l ->
                    assertThat(l.stream().map(
                            r -> r[0] + "," + r[1] + "," + r[2]).
                            collect(Collectors.toList()), is(records)))
                .expectNextCount(RANDOM_SAMPLE_SIZE).
                verifyComplete();
    }

    @Test
    public void testRandomDataSourceSeparator(){
        List<String> records =
                DataBox.getRandomCsvAddressBook(RANDOM_SAMPLE_SIZE, "-");
        Flux<String[]> flux = CsvDataSource.<String[]>builder().
                mapper(Function.identity()).
                separator("-").
                build().
                stream(records);
        StepVerifier.create(flux)
                .recordWith(ArrayList::new)
                .thenConsumeWhile(Objects::nonNull)
                .consumeRecordedWith(l ->
                        assertThat(l.stream().map(
                                r -> r[0] + "-" + r[1] + "-" + r[2]).
                                collect(Collectors.toList()), is(records)))
                .expectNextCount(RANDOM_SAMPLE_SIZE).
                verifyComplete();
    }

}
