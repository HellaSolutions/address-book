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

    public List<T> getReceived() {
        return received;
    }

    public String getThreadName() {
        return threadName;
    }

}
