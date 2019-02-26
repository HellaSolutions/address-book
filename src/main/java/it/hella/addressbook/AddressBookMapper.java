package it.hella.addressbook;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.function.Function;

public class AddressBookMapper implements Function<String[], Address> {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");
    @Override
    public Address apply(String[] record) {
        if (record == null || record.length != 3){
            throw new IllegalArgumentException(String.format("Csv bad format %s", Arrays.toString(record)));
        }
        LocalDate dateOfBirth = LocalDate.parse(record[2].trim(), dateFormatter);
        String[] nameSurname = record[0].split(" ");
        if (nameSurname.length != 2){
            throw new IllegalArgumentException(String.format("Csv name bad format %s", Arrays.toString(nameSurname)));
        }
        return new Address(nameSurname[0].trim(), nameSurname[1].trim(), record[1].trim(), dateOfBirth);
    }
}
