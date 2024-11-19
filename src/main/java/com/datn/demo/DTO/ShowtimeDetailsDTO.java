package com.datn.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowtimeDetailsDTO {
    private int showtimeId; // Mã ca chiếu
    private int movieId; // Mã phim
    private String movieName; // Tên phim
    private String genre; // Thể loại phim
    private String ageRestriction; // Giới hạn độ tuổi
    private int duration; // Thời gian phim (phút)
    private String roomName; // Tên phòng chiếu
    private LocalDate showDate; // Ngày chiếu
    private LocalTime startTime; // Giờ bắt đầu
    private LocalTime endTime; // Giờ kết thúc
    private String content; // Nội dung phim
    private String image; // Hình ảnh phim
    private String director; // Đạo diễn
    private String actor; // Diễn viên
    private String image_bg; // Hình ảnh nền
    private String trailerUrl; // Đường dẫn trailer
    private double seatPrice; // Giá ghế
    private LocalDate releaseDate; // Ngày phát hành phim (thêm thuộc tính mới)

    // Constructor bao gồm tất cả các thuộc tính
    public ShowtimeDetailsDTO(int showtimeId, String movieName, String ageRestriction, LocalTime startTime,
                              LocalTime endTime, LocalDate showDate, String roomName, int movieId,
                              String genre, String content, String image, String director, String actor,
                              String image_bg, String trailerUrl,LocalDate releaseDate) {
        this.showtimeId = showtimeId;
        this.movieName = movieName;
        this.ageRestriction = ageRestriction;
        this.startTime = startTime;
        this.endTime = endTime;
        this.showDate = showDate;
        this.roomName = roomName;
        this.movieId = movieId;
        this.genre = genre;
        this.content = content;
        this.image = image;
        this.director = director;
        this.actor = actor;
        this.image_bg = image_bg;
        this.trailerUrl = trailerUrl;
        this.seatPrice = seatPrice; // Gán giá ghế
        this.releaseDate = releaseDate;
    }
}
