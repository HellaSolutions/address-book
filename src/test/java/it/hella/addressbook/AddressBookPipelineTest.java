package it.hella.addressbook;

import it.hella.addressbook.test.util.DataBox;
import it.hella.aggregator.Pipeline;
import it.hella.aggregator.reactive.csv.CsvDataSource;
import it.hella.aggregator.reactive.csv.CsvDataSourceTest;
import org.junit.BeforeClass;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static it.hella.addressbook.test.util.DataBox.toCsvFormat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class AddressBookPipelineTest {

    private static final int RANDOM_SAMPLE_SIZE = 1000;

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
        Pipeline<Address, Integer> p = Pipeline.<Address, Integer>builder().
                csvDataSource(csvDataSource).
                aggregator(new CountMalesAggregator()).build();
        p.aggregate(path, a -> {
           values.add(a.getValue());
        });
        try {Thread.sleep(100); }catch(Exception e){}
        assertEquals(Integer.valueOf(3), values.get(0));

    }

    @Test
    public void testMaxAgeCounter() {

        final List<Address> values = new ArrayList<>();
        Pipeline<Address, Address> p = Pipeline.<Address, Address>builder().
                csvDataSource(csvDataSource).
                aggregator(new MaxAgeAggregator()).build();
        p.aggregate(path, a -> {
            values.add(a.getValue());
        });
        try {Thread.sleep(100); }catch(Exception e){}
        assertEquals(new AddressBookMapper().apply(new String[]{"Wes Jackson", "Male", "14/08/74"}),
                values.get(0));

    }

    @Test
    public void AgeDayDiffAggregator() {

        final List<Long> values = new ArrayList<>();
        Pipeline<Address, Long> p = Pipeline.<Address, Long>builder().
                csvDataSource(csvDataSource).
                aggregator(new AgeDayDiffAggregator("Bill", "Paul")).build();
        p.aggregate(path, a -> {
            values.add(a.getValue());
        });
        try {Thread.sleep(100); }catch(Exception e){}
        assertEquals(Long.valueOf(2862),
                values.get(0));

    }

}
