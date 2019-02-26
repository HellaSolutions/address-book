package it.hella.addressbook;

import it.hella.aggregator.Aggregator;

import java.time.LocalDate;

public class MaxAgeAggregator extends Aggregator<Address, Address> {

    public MaxAgeAggregator(){
        super("age_max",
                new Address("NO_DATA", "NO_DATA", "NO_DATA", LocalDate.now()));
    }

    @Override
    public void accept(Address address) {
        if (address.equals(Address.EMPTY)){
            return;
        }
        if (address.getBirthDate().isBefore(super.getValue().getBirthDate())){
            super.setValue(address);
        }
    }

}
