package it.hella.addressbook;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class AddressBookMapperTest {

    private static Logger log = LoggerFactory.getLogger(AddressBookMapperTest.class);

    AddressBookMapper mapper;

    @Before
    public void before() {
        mapper = new AddressBookMapper();
    }

    @Test
    public void testPastDateOfBirthMapping() {

        Address address = mapper.apply(new String[]{"name surname", "male", "01/01/31"});
        Address expected = new Address("name", "surname", "male",
                LocalDate.of(1931, 1, 1));
        assertEquals(expected, address);

    }

    @Test
    public void testReasonableDateOfBirthMapping() {

        Address address = mapper.apply(new String[]{"name surname", "female", "01/01/01"});
        Address expected = new Address("name", "surname", "female",
                LocalDate.of(2001, 1, 1));
        assertEquals(expected, address);

    }

    @Test
    public void testInvalidNameSurname() {

        Address address = mapper.apply(new String[]{"namesurname", "male", "01/01/31"});
        assertEquals(Address.EMPTY, address);

    }

    @Test
    public void testInvalidRecordFormat() {

        Address address = mapper.apply(new String[]{"name surname", "41/12/31"});
        assertEquals(Address.EMPTY, address);

    }

    @Test
    public void testNullRecord() {

        Address address = mapper.apply(null);
        assertEquals(Address.EMPTY, address);

    }

    @Test
    public void testInvalidDateFormat() {

        Address address = mapper.apply(new String[]{"name surname", "female", "dummy"});
        assertEquals(Address.EMPTY, address);

    }

    @Test
    public void testInvalidDateNotNumericDay() {

        Address address = mapper.apply(new String[]{"namesurname", "male", "oo/01/31"});
        assertEquals(Address.EMPTY, address);

    }

    @Test
    public void testInvalidDateNotNumericYear() {

        Address address = mapper.apply(new String[]{"namesurname", "male", "01/01/cc"});
        assertEquals(Address.EMPTY, address);

    }

    @Test
    public void testInvalidDateNotNumericMonth() {

        Address address = mapper.apply(new String[]{"namesurname", "male", "01/uu/31"});
        assertEquals(Address.EMPTY, address);

    }

    @Test
    public void testInvalidDateNumbers() {

        Address address = mapper.apply(new String[]{"namesurname", "male", "41/12/31"});
        assertEquals(Address.EMPTY, address);

    }
}
