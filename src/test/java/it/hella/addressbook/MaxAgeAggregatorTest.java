package it.hella.addressbook;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * The type Max age aggregator test.
 */
public class MaxAgeAggregatorTest {

    /**
     * The Max age aggregator.
     */
    MaxAgeAggregator maxAgeAggregator;

    /**
     * Before.
     */
    @Before
    public void before() {
        maxAgeAggregator = new MaxAgeAggregator();
    }

    /**
     * Test correct value last.
     */
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

    /**
     * Test correct value first.
     */
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

    /**
     * Test correct value middle.
     */
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

    /**
     * Test invalid records insensitive.
     */
    @Test
    public void testInvalidRecordsInsensitive(){
        List<Address> data = Arrays.asList(
                new Address("A", "surname", "male",
                        LocalDate.of(1931, 1, 1)),
                new Address("B", "surname", "male",
                        LocalDate.of(1900, 1, 1)),
                Address.INVALID,
                new Address("C", "surname", "male",
                        LocalDate.of(1980, 1, 1))
        );
        data.forEach(maxAgeAggregator::accept);
        assertEquals(new Address("B", "surname", "male",
                LocalDate.of(1900, 1, 1)), maxAgeAggregator.getValue());
    }

}
