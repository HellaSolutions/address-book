package it.hella.addressbook;

import it.hella.addressbook.test.util.DataBox;
import it.hella.aggregator.Pipeline;
import it.hella.reactive.csv.CsvDataSource;
import it.hella.reactive.csv.CsvDataSourceTest;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.test.StepVerifier;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static it.hella.addressbook.test.util.DataBox.toCsvFormat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class AddressBookPipelineTest {

    private static Logger log = LoggerFactory.getLogger(AddressBookPipelineTest.class);

    private static final int RANDOM_SAMPLE_SIZE = 1000;
    private static final int MASSIVE_SAMPLE_SIZE = 1000000;

    private static final ClassLoader classLoader = CsvDataSourceTest.class.getClassLoader();

    private static Path path;

    static CsvDataSource<Address> csvDataSource;

    @BeforeClass
    public static void beforeClass() {
        path = Paths.get(classLoader.getResource("AddressBook.txt").getPath().substring(1));
        csvDataSource = CsvDataSource.
                <Address>builder().
                mapper(new AddressBookMapper()).
                build();
    }

    @Test
    public void testMapper() {

        List<String> sample = DataBox.getRandomCsvAddressBook(RANDOM_SAMPLE_SIZE, ",");
        StepVerifier.create(csvDataSource.stream(sample))
                .recordWith(ArrayList::new)
                .thenConsumeWhile(Objects::nonNull)
                .consumeRecordedWith(l -> assertThat(l.stream().
                        map(a -> toCsvFormat(a)).
                        collect(Collectors.toList()),is(sample)))
                .expectNextCount(RANDOM_SAMPLE_SIZE).
                verifyComplete();

    }

    @Test
    public void testMaleCounter() {

        final List<Integer> values = new ArrayList<>();
        Pipeline<Address> p = Pipeline.<Address>builder().
                csvDataSource(csvDataSource).
                aggregator(new CountMalesAggregator()).build();
        p.aggregate(path, a -> {
            values.add((Integer)a.getValue());
        });
        try {Thread.sleep(100); }catch(Exception e){}
        assertEquals(Integer.valueOf(3), values.get(0));

    }

    @Test
    public void testMaxAgeCounter() {

        final List<Address> values = new ArrayList<>();
        Pipeline<Address> p = Pipeline.<Address>builder().
                csvDataSource(csvDataSource).
                aggregator(new MaxAgeAggregator()).build();
        p.aggregate(path, a -> {
            values.add((Address)a.getValue());
        });
        try {Thread.sleep(100); }catch(Exception e){}
        assertEquals(new AddressBookMapper().apply(new String[]{"Wes Jackson", "Male", "14/08/74"}),
                values.get(0));

    }

    @Test
    public void AgeDayDiffAggregator() {

        final List<Long> values = new ArrayList<>();
        Pipeline<Address> p = Pipeline.<Address>builder().
                csvDataSource(csvDataSource).
                aggregator(new AgeDayDiffAggregator("Bill", "Paul")).build();
        p.aggregate(path, a -> {
            values.add((Long)a.getValue());
        });
        try {Thread.sleep(100); }catch(Exception e){}
        assertEquals(Long.valueOf(2862),
                values.get(0));

    }

    @Test
    public void AllAggregators() {

        final Map<String, Object> values = new HashMap<>();
        Pipeline<Address> p = Pipeline.<Address>builder().
                csvDataSource(csvDataSource).
                aggregator(new AgeDayDiffAggregator("Bill", "Paul")).
                aggregator(new MaxAgeAggregator()).
                aggregator(new CountMalesAggregator()).
                build();
        p.aggregate(path, a -> values.put(a.getName(), a.getValue()));

        try {Thread.sleep(100); }catch(Exception e){}
        assertEquals(Long.valueOf(2862),
                values.get("ageday_diff_counter"));
        assertEquals(Integer.valueOf(3),
                values.get("males_counter"));
        assertEquals(new AddressBookMapper().apply(new String[]{"Wes Jackson", "Male", "14/08/74"}),
                values.get("age_max"));

    }
    @Test
    public void MassiveTestAggregators() {

        List<String> sample = DataBox.getRandomCsvAddressBook(MASSIVE_SAMPLE_SIZE, ",");
        final Map<String, Object> values = new HashMap<>();
        Pipeline<Address> p = Pipeline.<Address>builder().
                csvDataSource(csvDataSource).
                aggregator(new AgeDayDiffAggregator(
                        getName(sample.get(0)),
                        getName(sample.get(MASSIVE_SAMPLE_SIZE - 1)))).
                aggregator(new MaxAgeAggregator()).
                aggregator(new CountMalesAggregator()).
                build();

        StopWatch stopwatch = StopWatch.createStarted();
        p.aggregate(sample, a -> values.put(a.getName(), a.getValue()));

        log.info(String.format("age_max %s", p.getResult("age_max")));
        log.info(String.format("males_counter %s", p.getResult("males_counter")));
        log.info(String.format("ageday_diff_counter %s", p.getResult("ageday_diff_counter")));
        stopwatch.stop();
        log.info(String.format("parsed %d records in %s seconds", MASSIVE_SAMPLE_SIZE, stopwatch.getTime(TimeUnit.SECONDS)));

        assertEquals(3, values.size());

    }

    private String getName(String record){
        return record.split(",")[0].split(" ")[0];
    }

}
