package it.hella.addressbook;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AgeDayDiffAggregatorTest {

    AgeDayDiffAggregator ageDayDiffAggregator;

    @Before
    public void before() {
        ageDayDiffAggregator = new AgeDayDiffAggregator("A", "B");
    }

    @Test
    public void testCorrectZeroDifference(){
        List<Address> data = Arrays.asList(
                new Address("A", "surname", "male",
                        LocalDate.of(1931, 1, 1)),
                new Address("B", "surname", "male",
                        LocalDate.of(1931, 1, 1))
        );
        data.forEach(ageDayDiffAggregator::accept);
        assertEquals(Long.valueOf(0), ageDayDiffAggregator.getValue());
    }

    @Test
    public void testCorrectNonZeroDifference(){
        List<Address> data = Arrays.asList(
                new Address("A", "surname", "male",
                        LocalDate.of(1931, 1, 10)),
                new Address("B", "surname", "male",
                        LocalDate.of(1931, 1, 1))
        );
        data.forEach(ageDayDiffAggregator::accept);
        assertEquals(Long.valueOf(9), ageDayDiffAggregator.getValue());
    }

    @Test
    public void testCorrectNonZeroDifferenceOrderIrrelevant(){
        List<Address> data = Arrays.asList(
                new Address("B", "surname", "male",
                        LocalDate.of(1931, 1, 10)),
                new Address("A", "surname", "male",
                        LocalDate.of(1931, 1, 1))
        );
        data.forEach(ageDayDiffAggregator::accept);
        assertEquals(Long.valueOf(9), ageDayDiffAggregator.getValue());
    }

    @Test
    public void testNoABPair(){
        List<Address> data = Arrays.asList(
                new Address("C", "surname", "male",
                        LocalDate.of(1931, 1, 10)),
                new Address("D", "surname", "male",
                        LocalDate.of(1931, 1, 1))
        );
        data.forEach(ageDayDiffAggregator::accept);
        assertEquals(Long.valueOf(-1), ageDayDiffAggregator.getValue());
    }

    @Test
    public void testNoANotBPair(){
        List<Address> data = Arrays.asList(
                new Address("A", "surname", "male",
                        LocalDate.of(1931, 1, 10)),
                new Address("D", "surname", "male",
                        LocalDate.of(1931, 1, 1))
        );
        data.forEach(ageDayDiffAggregator::accept);
        assertEquals(Long.valueOf(-1), ageDayDiffAggregator.getValue());
    }

    @Test
    public void testNotABPair(){
        List<Address> data = Arrays.asList(
                new Address("C", "surname", "male",
                        LocalDate.of(1931, 1, 10)),
                new Address("B", "surname", "male",
                        LocalDate.of(1931, 1, 1))
        );
        data.forEach(ageDayDiffAggregator::accept);
        assertEquals(Long.valueOf(-1), ageDayDiffAggregator.getValue());
    }

    @Test
    public void tesDuplicatedFirstTaken(){
        List<Address> data = Arrays.asList(
                new Address("A", "surname", "male",
                        LocalDate.of(1931, 1, 10)),
                new Address("A", "surname", "male",
                        LocalDate.of(1931, 1, 1)),
                new Address("B", "surname", "male",
                        LocalDate.of(1931, 1, 1)),
                new Address("C", "surname", "male",
                        LocalDate.of(1931, 1, 1)),
                new Address("A", "surname", "male",
                        LocalDate.of(1931, 1, 1)),
                new Address("B", "surname", "male",
                        LocalDate.of(1931, 1, 10))
        );
        data.forEach(ageDayDiffAggregator::accept);
        assertEquals(Long.valueOf(9), ageDayDiffAggregator.getValue());
    }

    @Test
    public void testInvalidRecordsInsensitive(){
        List<Address> data = Arrays.asList(
                new Address("B", "surname", "male",
                        LocalDate.of(1931, 1, 10)),
                Address.EMPTY,
                new Address("A", "surname", "male",
                        LocalDate.of(1931, 1, 1))
        );
        data.forEach(ageDayDiffAggregator::accept);
        assertEquals(Long.valueOf(9), ageDayDiffAggregator.getValue());
    }
}
