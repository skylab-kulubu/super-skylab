package com.skylab.superapp.entities.DTOs.ticket.request;

import com.skylab.superapp.core.constants.TicketMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuestTicketRequestDto {

    @NotBlank(message = TicketMessages.GUEST_FIRST_NAME_NOT_BLANK)
    private String firstName;

    @NotBlank(message = TicketMessages.GUEST_LAST_NAME_NOT_BLANK)
    private String lastName;

    @NotBlank(message = TicketMessages.GUEST_EMAIL_NOT_BLANK)
    @Email(message = TicketMessages.GUEST_EMAIL_INVALID)
    private String email;

    @NotBlank(message = TicketMessages.GUEST_PHONE_NOT_BLANK)
    private String phoneNumber;

    private LocalDateTime birthDate;
    private Boolean isStudent;
    private String university;
    private String faculty;
    private String department;
    private String grade;

    private String tcIdentityNumber;
    private String carPlateNumber;

    private Map<String, String> customAnswers;
}