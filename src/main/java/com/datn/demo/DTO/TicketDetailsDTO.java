package com.datn.demo.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class TicketDetailsDTO {

    private String seatName;
    private String movieName;
    private LocalDate showDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String roomName;
    private String cinemaName;
    private String address;
    private String email;
    private String phoneNumber;

    // Constructor
    public TicketDetailsDTO(String seatName, String movieName, LocalDate showDate, LocalTime startTime, LocalTime endTime,
                            String roomName, String cinemaName, String address, String email, String phoneNumber) {
        this.seatName = seatName;
        this.movieName = movieName;
        this.showDate = showDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomName = roomName;
        this.cinemaName = cinemaName;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}
