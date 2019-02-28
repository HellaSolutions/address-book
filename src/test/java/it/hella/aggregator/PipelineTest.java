package it.hella.aggregator;

import it.hella.addressbook.test.util.DataBox;
import it.hella.addressbook.test.util.TestAggregator;
import it.hella.reactive.csv.CsvDataSource;
import it.hella.reactive.csv.CsvDataSourceTest;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * The type Pipeline test.
 */
public class PipelineTest {

    private static Logger log = LoggerFactory.getLogger(PipelineTest.class);

    private static final int RANDOM_SAMPLE_SIZE = 1000;
    private static final int NUM_CONCURRENT_AGGREGATORS = 50;

    /**
     * The constant numberSynchronizationTests.
     */
    public static final Integer numberSynchronizationTests = 1;
    /**
     * The constant randomDelayBound.
     */
    public static final Integer randomDelayBound = 1000;

    private static final ClassLoader classLoader = CsvDataSourceTest.class.getClassLoader();

    private static Path path;

    /**
     * The Thrown.
     */
    @Rule
    public ExpectedException thrown = ExpectedException.none();


    /**
     * Before class.
     */
    @BeforeClass
    public static void beforeClass() {
        path = Paths.get(classLoader.getResource("AddressBook.txt").getPath().substring(1));
    }

    /**
     * Test aggregator receives all records.
     */
    @Test
    public void testAggregatorReceivesAllRecords() {

        TestAggregator<String[]> testAggregator = new TestAggregator<>("test_aggregator");
        List<String> records =
                DataBox.getRandomCsvAddressBook(RANDOM_SAMPLE_SIZE, ", ");
        CsvDataSource<String[]> csvDataSource = CsvDataSource.
                <String[]>builder().
                mapper(Function.identity()).
                build();
        Pipeline<String[]> p = Pipeline.<String[]>builder().csvDataSource(csvDataSource).
                aggregator(testAggregator).build();
        p.aggregate(records, r -> {/*running in another thread*/});

        p.await();
        assertThat(testAggregator.getReceived().
                stream().map(r -> r[0] + "," + r[1] + "," + r[2]).collect(Collectors.toList()), is(records));
        assertEquals(Integer.valueOf(RANDOM_SAMPLE_SIZE), testAggregator.getValue());
    }

    /**
     * Test multiple aggregators concurrency and broad casting.
     */
    @Test
    public void testMultipleAggregatorsConcurrencyAndBroadCasting() {

        List<TestAggregator<String[]>> aggregators =
            TestAggregator.getTestAggregators(NUM_CONCURRENT_AGGREGATORS);
        //Stores the concurrent threads the aggregators are working on
        Set<String> threads = new HashSet<>();
        List<String> records =
                DataBox.getRandomCsvAddressBook(RANDOM_SAMPLE_SIZE, ", ");
        CsvDataSource<String[]> csvDataSource = CsvDataSource.
                <String[]>builder().
                mapper(Function.identity()).
                build();
        Pipeline.PipelineBuilder<String[]> builder = Pipeline.
                <String[]>builder().csvDataSource(csvDataSource);
        aggregators.forEach(builder::aggregator);
        Pipeline<String[]> p = builder.build();

        p.aggregate(records, a -> threads.add(((TestAggregator)a).getThreadName()));
        p.await();
        aggregators.forEach(a -> {
                    assertThat(a.getReceived().
                            stream().map(r -> r[0] + "," + r[1] + "," + r[2]).collect(Collectors.toList()), is(records));
                    assertEquals(Integer.valueOf(RANDOM_SAMPLE_SIZE), a.getValue());
                }
        );
        assertTrue(threads.size() > 1);
        log.info(String.format("Runned %s aggregators on %s threads", NUM_CONCURRENT_AGGREGATORS, threads.size()));

    }

    /**
     * Builder asynchronous collect test.
     */
    @Test
    public void builderAsynchronousCollectTest() {

        for (int i = 0; i < numberSynchronizationTests; i++) {
            CsvDataSource<String[]> csvDataSource = CsvDataSource.
                    <String[]>builder().
                    mapper(Function.identity()).
                    build();
            Pipeline<String[]> p = Pipeline.<String[]>builder().csvDataSource(csvDataSource).
                    aggregator(new Aggregator<String[], String>("names", "") {
                        @Override
                        public void accept(String[] o) {
                            this.setValue(this.getValue() + o[0]);
                        }
                    }).
                    aggregator(new Aggregator<String[], String>("sex", "") {
                        @Override
                        public void accept(String[] o) {
                            this.setValue(this.getValue() + o[1]);
                        }
                    }).build();
            p.aggregate(path, a -> log.info((String)a.getValue()));
            p.await();

        }
    }

}
