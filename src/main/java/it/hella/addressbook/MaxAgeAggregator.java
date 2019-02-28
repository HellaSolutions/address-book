package it.hella.addressbook;

import it.hella.aggregator.Aggregator;

import java.time.LocalDate;

/**
 * The type Max age aggregator.
 */
public class MaxAgeAggregator extends Aggregator<Address, Address> {

    /**
     * The constant NAME.
     */
    public static final String NAME = "age_max";
    private static final String NO_DATA = "NO_DATA";

    /**
     * Instantiates a new Max age aggregator.
     */
    public MaxAgeAggregator(){
        super(NAME,
                new Address(NO_DATA, NO_DATA, NO_DATA, LocalDate.now()));
    }

    @Override
    public void accept(Address address) {
        if (address.equals(Address.INVALID)){
            return;
        }
        if (address.getBirthDate().isBefore(super.getValue().getBirthDate())){
            super.setValue(address);
        }
    }

}
