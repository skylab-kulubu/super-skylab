package com.skylab.superapp.entities.DTOs.ticketCheckIn;

import com.skylab.superapp.entities.DTOs.User.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TicketCheckInDto {

    private UUID id;

    private UUID eventDayId;

    private LocalDateTime createdAt;

    private UserDto scannedBy;


}
