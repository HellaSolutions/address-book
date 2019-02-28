package it.hella.addressbook.test.util;

import it.hella.aggregator.Aggregator;

import java.util.ArrayList;
import java.util.List;


/**
 * Aggregator for testing classes.
 *
 * @param <T> the type parameter
 */
public class TestAggregator<T> extends Aggregator<T, Integer> {

    /**
     *  Records the received records for later assertions.
     */
    private List<T> received = new ArrayList<>();

    /**
     *  Records the name of the Thread the aggregator is running in.
     */
    private String threadName;

    /**
     * Instantiates a new Test aggregator.
     *
     * @param name aggregator name
     */
    public TestAggregator(String name){
        super(name, 0);
    }

    @Override
    public void accept(T t) {
        this.threadName = Thread.currentThread().getName();
        received.add(t);
        super.setValue(received.size());
    }

    /**
     * Gets received.
     *
     * @return the received
     */
    public List<T> getReceived() {
        return received;
    }

    /**
     * Gets thread name.
     *
     * @return the thread name
     */
    public String getThreadName() {
        return threadName;
    }

    /**
     * Get test aggregators list.
     *
     * @param numberAggregators the number aggregators
     * @return the list
     */
    public static final List<TestAggregator<String[]>> getTestAggregators(int numberAggregators){
        List<TestAggregator<String[]>> aggregators = new ArrayList<>();
        for (int c = 0; c < numberAggregators; c++) {
            aggregators.add(new TestAggregator<>("test_aggregator_" + c));
        }
        return aggregators;
    }

}
