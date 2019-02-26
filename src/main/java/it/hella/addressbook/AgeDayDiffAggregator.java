package it.hella.addressbook;

import it.hella.aggregator.Aggregator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * The type Age day diff aggregator.
 */
public class AgeDayDiffAggregator extends Aggregator<Address, Long> {

    private String nameA;
    private String nameB;

    private LocalDate dateOfBirthA = null;
    private LocalDate dateOfBirthB = null;

    /**
     * Calculates the age differences in days between
     * the first unordered pair of records with names
     * nameA and nameB
     *
     * @param nameA the name a
     * @param nameB the name b
     */
    public AgeDayDiffAggregator(String nameA, String nameB){
        super("ageday_diff_counter", -1L);
        this.nameA = nameA;
        this.nameB = nameB;
    }

    @Override
    public void accept(Address address) {
        if (address.equals(Address.EMPTY) ||
                (dateOfBirthA != null && dateOfBirthB != null)){
            return;
        }
        if (dateOfBirthA == null && address.getName().equalsIgnoreCase(nameA)){
            dateOfBirthA = address.getBirthDate();
        } else if (dateOfBirthB == null && address.getName().equalsIgnoreCase(nameB)) {
            dateOfBirthB = address.getBirthDate();
        }
        if (dateOfBirthA != null && dateOfBirthB != null){
            super.setValue(Math.abs(ChronoUnit.DAYS.between(dateOfBirthA, dateOfBirthB)));
        }

    }
}
