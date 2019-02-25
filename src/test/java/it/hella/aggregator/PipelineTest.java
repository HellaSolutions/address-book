package it.hella.aggregator;

import it.hella.aggregator.reactive.csv.CsvDataSource;
import it.hella.aggregator.reactive.csv.CsvDataSourceTest;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.Assert.*;

public class PipelineTest {

    private static final ClassLoader classLoader = CsvDataSourceTest.class.getClassLoader();

    private static Path path;

    @BeforeClass
    public static void beforeClass(){
        path = Paths.get(classLoader.getResource("AddressBook.txt").getPath().substring(1));
    }

    @Test
    public void builderSynchronizedCollectTest() {

        for (int i = 0; i < 1000; i++) {
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
                            try{ Thread.sleep(100);}catch(Exception e){}
                        }
                    }).build();
            p.aggregate(path);
            assertNotEquals(Optional.empty(), p.getResult(0));
            assertNotEquals(Optional.empty(), p.getResult(1));
            System.out.println(p.getResult(0));
            System.out.println(p.getResult(1));

        }
    }

}
