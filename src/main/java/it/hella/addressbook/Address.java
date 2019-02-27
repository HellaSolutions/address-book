package it.hella.addressbook;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Address {

    public static final Address EMPTY = new Address(null, null, null, null);

    private String name;
    private String surname;
    private String gender;
    private LocalDate birthDate;
}
