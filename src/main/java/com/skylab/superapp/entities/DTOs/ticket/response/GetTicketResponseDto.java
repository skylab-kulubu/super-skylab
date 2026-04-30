package com.skylab.superapp.entities.DTOs.ticket.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.skylab.superapp.entities.DTOs.Event.EventDto;
import com.skylab.superapp.entities.DTOs.User.UserDto;
import com.skylab.superapp.entities.DTOs.ticketCheckIn.TicketCheckInDto;
import com.skylab.superapp.entities.TicketType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetTicketResponseDto {

    private UUID id;
    private boolean sent;
    private TicketType ticketType;

    private UserDto owner;

    private String guestFirstName;
    private String guestLastName;
    private String guestEmail;
    private String guestPhoneNumber;
    private String guestUniversity;
    private String guestFaculty;
    private String guestDepartment;
    private String guestGrade;

    private EventDto event;
    private List<TicketCheckInDto> checkIns;

    @JsonIgnore
    public String getTicketHolderFullName() {
        if (ticketType == TicketType.REGISTERED && owner != null) {
            return owner.getFirstName() + " " + owner.getLastName();
        }
        return guestFirstName + " " + guestLastName;
    }

    @JsonIgnore
    public String getTicketHolderEmail() {
        if (ticketType == TicketType.REGISTERED && owner != null) {
            return owner.getEmail();
        }
        return guestEmail;
    }
}