package com.datn.demo.DTO;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;
@Data

public class ShowtimeDTO {
	private int showtimeId;
    private int movieId;
	private String movieName;
    private String image; 
	private LocalTime startTime;
    private LocalTime endTime;
	private String roomName;
    private String genre; 
    private String ageRestriction;
    private int duration;
    private int cinemaId;
    private String cinemaName;
	
	public ShowtimeDTO(int showtimeId,int movieId, String movieName,String image, LocalTime startTime,LocalTime endTime, String roomName,String genre,String ageRestriction,int duration,int cinemaId,String cinemaName) {
		this.showtimeId = showtimeId;
		this.movieId = movieId;
		this.movieName = movieName;
		this.image = image;
		this.startTime = startTime;
		this.endTime = endTime;
		this.roomName = roomName;
		this.genre = genre;
		this.ageRestriction = ageRestriction;
		this.duration = duration;
		this.cinemaId = cinemaId; 
		this.cinemaName = cinemaName;
	}
}
