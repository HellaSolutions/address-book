package it.hella.aggregator;

import lombok.Data;

import java.util.function.Consumer;

/**
 * Abstract class for aggregators.
 *
 * @param <T> The record type
 * @param <V> the type of the aggregation result
 */
@Data
public abstract class Aggregator<T, V> implements Consumer<T> {

    private String name;
    private V value;

    /**
     * Instantiates a new Aggregator.
     *
     * @param name         the name of the aggregator. Should be unique
     * @param initialValue the initial value
     */
    public Aggregator(String name, V initialValue){
        this.name = name;
        this.value = initialValue;
    }

}