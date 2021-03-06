package it.hella.addressbook.test.util;

import it.hella.addressbook.Address;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The type Data box.
 */
public class DataBox {

    /**
     * Gets random csv address book.
     *
     * @param numberOfRecords the number of records
     * @param separator       the separator
     * @return the random csv address book
     */
    public static final List<String> getRandomCsvAddressBook(int numberOfRecords, String separator) {
        List<String> records = new ArrayList<>();
        for (int k = 0; k < numberOfRecords; k++){
            records.add(randomRecord(separator));
        }
        return records;
    }

    /**
     * Random record string.
     *
     * @param separator the separator
     * @return the string
     */
    public static final String randomRecord(String separator) {
        Random r = new Random();
        return new StringBuffer()
                .append(RandomStringUtils.randomAlphabetic(10))
                .append(" ")
                .append(RandomStringUtils.randomAlphabetic(10))
                .append(separator)
                .append((r.nextInt(2) == 0) ? "Male" : "Female")
                .append(separator)
                .append(randomBirthday()).toString();
    }

    /**
     * Random birthday string.
     *
     * @return the string
     */
    public static String randomBirthday() {
        LocalDate date = LocalDate.now().
                minus(Period.ofDays((new Random().nextInt(365 * 70))));
        return new StringBuffer()
                .append( date.getDayOfMonth())
                        .append("/")
                        .append(date.getMonthValue())
                        .append("/")
                        .append(String.valueOf(date.getYear()).substring(2)).toString();
    }

    /**
     * To csv date string.
     *
     * @param localDate the local date
     * @return the string
     */
    public static String toCsvDate(LocalDate localDate) {
        return new StringBuffer()
                .append(localDate.getDayOfMonth())
                .append("/")
                .append(localDate.getMonthValue())
                .append("/")
                .append(String.valueOf(localDate.getYear()).substring(2))
                .toString();
    }

    /**
     * To csv format string.
     *
     * @param address the address
     * @return the string
     */
    public static String toCsvFormat(Address address) {
        return new StringBuffer()
                .append(address.getName())
                .append(" ")
                .append(address.getSurname())
                .append(",")
                .append(address.getGender())
                .append(",")
                .append(toCsvDate(address.getBirthDate()))
                .toString();
    }

}
