package it.hella.addressbook;

import it.hella.aggregator.Aggregator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * The type Age day diff aggregator.
 */
public class AgeDayDiffAggregator extends Aggregator<Address, Long> {

    /**
     * NAME of the aggregator
     */
    public static final String NAME = "ageday_diff_counter";

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
        super(NAME, -1L);
        this.nameA = nameA;
        this.nameB = nameB;
    }

    @Override
    public void accept(Address address) {
        if (address.equals(Address.INVALID) ||
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AgeDayDiffAggregator that = (AgeDayDiffAggregator) o;
        return Objects.equals(nameA, that.nameA) &&
                Objects.equals(nameB, that.nameB);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), nameA, nameB);
    }
}
