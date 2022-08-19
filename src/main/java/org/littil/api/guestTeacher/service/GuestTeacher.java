package org.littil.api.guestTeacher.service;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.time.DayOfWeek;
import java.util.EnumSet;
import java.util.UUID;

@Data
public class GuestTeacher {
    private UUID id;

    @NotEmpty(message = "{GuestTeacher.firstName.required}")
    private String firstName;

    @NotEmpty(message = "{GuestTeacher.surname.required}")
    private String surname;

    @NotEmpty(message = "{GuestTeacher.email.required}")
    @Email(message = "{GuestTeacher.email.invalid}")
    private String email;

    @NotEmpty(message = "{GuestTeacher.address.required}")
    private String address;

    @NotEmpty(message = "{GuestTeacher.postalCode.required}")
    private String postalCode;

    @NotEmpty(message = "{GuestTeacher.locale.required}")
    private String locale;

    private String preferences;
    private EnumSet<DayOfWeek> availability;
}
