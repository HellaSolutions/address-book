package it.hella.addressbook;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

/**
 * The type Address book mapper test.
 */
public class AddressBookMapperTest {

    private static Logger log = LoggerFactory.getLogger(AddressBookMapperTest.class);

    /**
     * The Mapper.
     */
    AddressBookMapper mapper;

    /**
     * Before.
     */
    @Before
    public void before() {
        mapper = new AddressBookMapper();
    }

    /**
     * Test past date of birth mapping.
     */
    @Test
    public void testPastDateOfBirthMapping() {

        Address address = mapper.apply(new String[]{"name surname", "male", "01/01/31"});
        Address expected = new Address("name", "surname", "male",
                LocalDate.of(1931, 1, 1));
        assertEquals(expected, address);

    }

    /**
     * Test reasonable date of birth mapping.
     */
    @Test
    public void testReasonableDateOfBirthMapping() {

        Address address = mapper.apply(new String[]{"name surname", "female", "01/01/01"});
        Address expected = new Address("name", "surname", "female",
                LocalDate.of(2001, 1, 1));
        assertEquals(expected, address);

    }

    /**
     * Test invalid name surname.
     */
    @Test
    public void testInvalidNameSurname() {

        Address address = mapper.apply(new String[]{"namesurname", "male", "01/01/31"});
        assertEquals(Address.INVALID, address);

    }

    /**
     * Test invalid record format.
     */
    @Test
    public void testInvalidRecordFormat() {

        Address address = mapper.apply(new String[]{"name surname", "41/12/31"});
        assertEquals(Address.INVALID, address);

    }

    /**
     * Test null record.
     */
    @Test
    public void testNullRecord() {

        Address address = mapper.apply(null);
        assertEquals(Address.INVALID, address);

    }

    /**
     * Test invalid date format.
     */
    @Test
    public void testInvalidDateFormat() {

        Address address = mapper.apply(new String[]{"name surname", "female", "dummy"});
        assertEquals(Address.INVALID, address);

    }

    /**
     * Test invalid date not numeric day.
     */
    @Test
    public void testInvalidDateNotNumericDay() {

        Address address = mapper.apply(new String[]{"namesurname", "male", "oo/01/31"});
        assertEquals(Address.INVALID, address);

    }

    /**
     * Test invalid date not numeric year.
     */
    @Test
    public void testInvalidDateNotNumericYear() {

        Address address = mapper.apply(new String[]{"namesurname", "male", "01/01/cc"});
        assertEquals(Address.INVALID, address);

    }

    /**
     * Test invalid date not numeric month.
     */
    @Test
    public void testInvalidDateNotNumericMonth() {

        Address address = mapper.apply(new String[]{"namesurname", "male", "01/uu/31"});
        assertEquals(Address.INVALID, address);

    }

    /**
     * Test invalid date numbers.
     */
    @Test
    public void testInvalidDateNumbers() {

        Address address = mapper.apply(new String[]{"namesurname", "male", "41/12/31"});
        assertEquals(Address.INVALID, address);

    }
}
