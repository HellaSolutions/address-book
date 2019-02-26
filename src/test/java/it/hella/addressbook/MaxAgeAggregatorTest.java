package it.hella.addressbook;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MaxAgeAggregatorTest {

    MaxAgeAggregator maxAgeAggregator;

    @Before
    public void before() {
        maxAgeAggregator = new MaxAgeAggregator();
    }

    @Test
    public void testCorrectValueLast(){
        List<Address> data = Arrays.asList(
                new Address("A", "surname", "male",
                        LocalDate.of(1980, 1, 1)),
                new Address("B", "surname", "male",
                        LocalDate.of(1931, 1, 1))
        );
        data.forEach(maxAgeAggregator::accept);
        assertEquals(new Address("B", "surname", "male",
                LocalDate.of(1931, 1, 1)), maxAgeAggregator.getValue());
    }

    @Test
    public void testCorrectValueFirst(){
        List<Address> data = Arrays.asList(
                new Address("A", "surname", "male",
                        LocalDate.of(1931, 1, 1)),
                new Address("B", "surname", "male",
                        LocalDate.of(1980, 1, 1))
        );
        data.forEach(maxAgeAggregator::accept);
        assertEquals(new Address("A", "surname", "male",
                LocalDate.of(1931, 1, 1)), maxAgeAggregator.getValue());
    }

    @Test
    public void testCorrectValueMiddle(){
        List<Address> data = Arrays.asList(
                new Address("A", "surname", "male",
                        LocalDate.of(1931, 1, 1)),
                new Address("B", "surname", "male",
                        LocalDate.of(1900, 1, 1)),
                new Address("C", "surname", "male",
                        LocalDate.of(1980, 1, 1))
        );
        data.forEach(maxAgeAggregator::accept);
        assertEquals(new Address("B", "surname", "male",
                LocalDate.of(1900, 1, 1)), maxAgeAggregator.getValue());
    }

    @Test
    public void testInvalidRecordsInsensitive(){
        List<Address> data = Arrays.asList(
                new Address("A", "surname", "male",
                        LocalDate.of(1931, 1, 1)),
                new Address("B", "surname", "male",
                        LocalDate.of(1900, 1, 1)),
                Address.EMPTY,
                new Address("C", "surname", "male",
                        LocalDate.of(1980, 1, 1))
        );
        data.forEach(maxAgeAggregator::accept);
        assertEquals(new Address("B", "surname", "male",
                LocalDate.of(1900, 1, 1)), maxAgeAggregator.getValue());
    }

}
