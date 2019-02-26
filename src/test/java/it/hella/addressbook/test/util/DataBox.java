package it.hella.addressbook.test.util;

import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataBox {

    public static final List<String> getCsvAddressBook(int numberOfRecords, int maleNumber) {
        return null;
    }

    public static final List<String> getCsvAddressBookMaxAge(int numberOfRecords, int maxAge) {
        return null;
    }

    public static final List<String> getCsvAddressBook(int numberOfRecords, String nameA, String nameB, int dayAgeDiff) {
        return null;
    }

    public static final List<String> getRandomCsvAddressBook(int numberOfRecords, String separator) {
        List<String> records = new ArrayList<>();
        for (int k = 0; k < numberOfRecords; k++){
            records.add(randomRecord(separator));
        }
        return records;
    }

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

}
