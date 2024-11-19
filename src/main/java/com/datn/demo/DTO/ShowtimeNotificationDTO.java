package com.datn.demo.DTO;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowtimeNotificationDTO {
	  private String movieName;
	    private LocalDate originalShowDate;
	    private LocalTime originalStartTime;
	    private LocalTime originalEndTime;
	    private LocalDate currentShowDate;
	    private LocalTime currentStartTime;
	    private LocalTime currentEndTime;
}
