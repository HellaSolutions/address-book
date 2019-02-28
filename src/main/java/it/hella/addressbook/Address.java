package it.hella.addressbook;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

/**
 * The type Address.
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Address {

    /**
     * The constant INVALID.
     * <p>
     * Used to mark an invalid record
     */
    public static final Address INVALID = new Address(null, null, null, null);

    private String name;
    private String surname;
    private String gender;
    private LocalDate birthDate;
}
