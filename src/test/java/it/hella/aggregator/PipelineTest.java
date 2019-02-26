package it.hella.aggregator;

import it.hella.aggregator.reactive.csv.CsvDataSource;
import it.hella.aggregator.reactive.csv.CsvDataSourceTest;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.function.Function;

public class PipelineTest {

    public static final Integer numberSynchronizationTests = 10;
    public static final Integer randomDelayBound = 10;

    private static final ClassLoader classLoader = CsvDataSourceTest.class.getClassLoader();

    private static Path path;

    @BeforeClass
    public static void beforeClass(){
        path = Paths.get(classLoader.getResource("AddressBook.txt").getPath().substring(1));
    }

    @Test
    public void builderAsynchronousCollectTest() {

        for (int i = 0; i < numberSynchronizationTests; i++) {
            CsvDataSource<String[]> csvDataSource = CsvDataSource.
                    <String[]>builder().
                    mapper(Function.identity()).
                    build();
            Pipeline<String[], String> p = Pipeline.<String[], String>builder().csvDataSource(csvDataSource).
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
                            try{ Thread.sleep(new Random().nextInt(randomDelayBound));}catch(Exception e){}
                        }
                    }).build();
            p.aggregate(path, System.out::println);
            try{Thread.sleep(100);}catch(Exception e){}

        }
    }

}
