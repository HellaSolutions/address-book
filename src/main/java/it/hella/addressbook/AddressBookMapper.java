package it.hella.addressbook;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.function.Function;

/**
 * The type Address book mapper.
 *
 * Maps a CSV record into and Adress object
 */
public class AddressBookMapper implements Function<String[], Address> {

    private static Logger log = LoggerFactory.getLogger(AddressBookMapper.class);

    //The most significant digits of the current year
    static String currentYearTwoMostSignificantDigits;

    static{
        //still valid for about 8000 years (I hope)
        currentYearTwoMostSignificantDigits = String.valueOf(LocalDate.now().getYear()).substring(0, 2);
    }

    @Override
    public Address apply(String[] record) {
        if (record == null || record.length != 3){
            log.warn(String.format("Badly formatted record %s",  Arrays.toString(record)));
            return Address.EMPTY;
        }
        String[] saferDate = record[2].trim().split("/");
        LocalDate dateOfBirth = null;
        if (!validate(saferDate) || (dateOfBirth = opinionatedDate(saferDate)) == null){
            log.warn(String.format("Badly formatted date %s",  record[2]));
            return Address.EMPTY;
        }
        String[] nameSurname = record[0].split(" ");
        if (nameSurname.length != 2){
            log.warn(String.format("Csv name bad format %s", Arrays.toString(nameSurname)));
            return Address.EMPTY;
        }
        return new Address(nameSurname[0].trim(), nameSurname[1].trim(), record[1].trim(), dateOfBirth);
    }

    private boolean validate(String[] dateRepresentation) {
        return dateRepresentation.length == 3 &&
                StringUtils.isNumeric(dateRepresentation[0]) &&
                        StringUtils.isNumeric(dateRepresentation[1]) &&
                                StringUtils.isNumeric(dateRepresentation[2]);
    }

    /*
    * Dates are incomplete on the CSV file. This is the weaker opinionated choice
    * */
    private LocalDate opinionatedDate(String[] saferDate){
        Integer year = Integer.valueOf(currentYearTwoMostSignificantDigits + saferDate[2]);
        try {
            LocalDate dateOfBirth = LocalDate.of(year,
                    Integer.valueOf(saferDate[1]),
                    Integer.valueOf(saferDate[0]));
            return dateOfBirth.isBefore(LocalDate.now()) ? dateOfBirth : dateOfBirth.minusYears(100);
        }catch(DateTimeException e){
            return null;
        }
    }
}
