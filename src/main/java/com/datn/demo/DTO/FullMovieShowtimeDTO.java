package com.datn.demo.DTO;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FullMovieShowtimeDTO {

    // Thông tin từ bảng MOVIE
    private int movieId;
    private String movieName;
    private String trailerUrl;
    private String content;
    private String ageRestriction;
    private int duration;
    private String director;
    private String actor;
    private String image;
    private String genre;
    private LocalDate releaseDate;

    // Thông tin từ bảng SHOWTIME
    private int showtimeId;
    private LocalDate showDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate originalShowDate;

    // Thông tin từ bảng ROOM
    private int roomId;
    private String roomName;

    // Thông tin từ bảng CINEMA_INFORMATION
    private Integer cinemaId;
    private String cinemaName;
    private String address;
    private String phoneNumber;
    private String email;
}

