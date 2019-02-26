package it.hella.addressbook;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CountMalesAggregatorTest {

    CountMalesAggregator countMalesAggregator;

    @Before
    public void before() {
        countMalesAggregator = new CountMalesAggregator();
    }

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

    @Test
    public void testInvalidRecordsInsensitive(){
        List<Address> data = Arrays.asList(
                new Address("B", "surname", "female",
                        LocalDate.of(1931, 1, 1)),
                Address.EMPTY,
                new Address("A", "surname", "MalE",
                        LocalDate.of(1931, 1, 1))
        );
        data.forEach(countMalesAggregator::accept);
        assertEquals(Integer.valueOf(1), countMalesAggregator.getValue());
    }

}
