package it.hella.addressbook;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * The type Count males aggregator test.
 */
public class CountMalesAggregatorTest {

    /**
     * The Count males aggregator.
     */
    CountMalesAggregator countMalesAggregator;

    /**
     * Before.
     */
    @Before
    public void before() {
        countMalesAggregator = new CountMalesAggregator();
    }

    /**
     * Test only males.
     */
    @Test
    public void testOnlyMales(){
        List<Address> data = Arrays.asList(
                new Address("A", "surname", "male",
                        LocalDate.of(1931, 1, 1)),
                new Address("B", "surname", "male",
                        LocalDate.of(1931, 1, 1))
        );
        data.forEach(countMalesAggregator::accept);
        assertEquals(Integer.valueOf(2), countMalesAggregator.getValue());
    }

    /**
     * Test only females.
     */
    @Test
    public void testOnlyFemales(){
        List<Address> data = Arrays.asList(
                new Address("A", "surname", "female",
                        LocalDate.of(1931, 1, 1)),
                new Address("B", "surname", "female",
                        LocalDate.of(1931, 1, 1))
        );
        data.forEach(countMalesAggregator::accept);
        assertEquals(Integer.valueOf(0), countMalesAggregator.getValue());
    }

    /**
     * Test case insensitive.
     */
    @Test
    public void testCaseInsensitive(){
        List<Address> data = Arrays.asList(
                new Address("A", "surname", "MalE",
                        LocalDate.of(1931, 1, 1)),
                new Address("B", "surname", "female",
                        LocalDate.of(1931, 1, 1))
        );
        data.forEach(countMalesAggregator::accept);
        assertEquals(Integer.valueOf(1), countMalesAggregator.getValue());
    }

    /**
     * Test invalid records insensitive.
     */
    @Test
    public void testInvalidRecordsInsensitive(){
        List<Address> data = Arrays.asList(
                new Address("B", "surname", "female",
                        LocalDate.of(1931, 1, 1)),
                Address.INVALID,
                new Address("A", "surname", "MalE",
                        LocalDate.of(1931, 1, 1))
        );
        data.forEach(countMalesAggregator::accept);
        assertEquals(Integer.valueOf(1), countMalesAggregator.getValue());
    }

}
