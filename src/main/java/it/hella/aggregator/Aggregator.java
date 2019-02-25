package it.hella.aggregator;

import lombok.Data;

import java.util.function.Consumer;

@Data
public abstract class Aggregator<T, V> implements Consumer<T> {

    private String name;

    private V value;

    public Aggregator(String name, V initialValue){
        this.name = name;
        this.value = initialValue;
    }

}