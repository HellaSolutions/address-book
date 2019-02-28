package it.hella.addressbook;

import it.hella.aggregator.Aggregator;

/**
 * The type Count males aggregator.
 */
public class CountMalesAggregator extends Aggregator<Address, Integer> {

    /**
     * NAME of the aggregator
     */
    public static final String NAME = "males_counter";

    /**
     * Instantiates a new Count males aggregator.
     */
    public CountMalesAggregator(){
        super(NAME, 0);
    }

    @Override
    public void accept(Address address) {
        if(address.equals(Address.INVALID)){
            return;
        }
        if (address.getGender().equalsIgnoreCase("male")){
            super.setValue(super.getValue() + 1);
        }
    }

}
