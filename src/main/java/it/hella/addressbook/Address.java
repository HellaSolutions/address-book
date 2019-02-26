package it.hella.addressbook;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Address {
    private String name;
    private String surname;
    private String gender;
    private LocalDate birthDate;
}
