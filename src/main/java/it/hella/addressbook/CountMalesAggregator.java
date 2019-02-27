package it.hella.addressbook;

import it.hella.aggregator.Aggregator;

public class CountMalesAggregator extends Aggregator<Address, Integer> {

    public static final String NAME = "males_counter";

    public CountMalesAggregator(){
        super(NAME, 0);
    }

    @Override
    public void accept(Address address) {
        if(address.equals(Address.EMPTY)){
            return;
        }
        if (address.getGender().equalsIgnoreCase("male")){
            super.setValue(super.getValue() + 1);
        }
    }

}
