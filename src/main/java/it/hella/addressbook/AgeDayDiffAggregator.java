package it.hella.addressbook;

import it.hella.aggregator.Aggregator;

import java.time.LocalDate;
import java.time.Period;

public class AgeDayDiffAggregator extends Aggregator<Address, Integer> {

    private String nameA;
    private String nameB;

    private LocalDate dateOfBirthA = null;
    private LocalDate dateOfBirthB = null;

    public AgeDayDiffAggregator(String nameA, String nameB){
        super("ageday_diff_counter", -1);
        this.nameA = nameA;
        this.nameB = nameB;
    }

    @Override
    public void accept(Address address) {
        if (dateOfBirthA != null && dateOfBirthB != null){
            return;
        }
        if (address.getName().equalsIgnoreCase(nameA)){
            dateOfBirthA = address.getBirthDate();
        } else if (address.getName().equalsIgnoreCase(nameB)) {
            dateOfBirthB = address.getBirthDate();
        }
        if (dateOfBirthA != null && dateOfBirthB != null){
            super.setValue(Math.abs(Period.between(dateOfBirthA, dateOfBirthB).getDays()));
        }

    }
}
