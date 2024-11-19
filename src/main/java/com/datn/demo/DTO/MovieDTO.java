package com.datn.demo.DTO;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class MovieDTO {

    private int movieId; // Mã phim
    private String movieName; // Tên phim
    private String trailerUrl; // Đường dẫn trailer
    private String content; // Nội dung phim
    private String ageRestriction; // Giới hạn độ tuổi
    private int duration; // Thời gian phim (phút)
    private String director; // Đạo diễn
    private String actor; // Diễn viên
    private String image; // Hình ảnh phim
    private String genre; // Thể loại phim
    private String imageBg; // Hình nền phim

    // Thêm các thuộc tính của ca chiếu
    private LocalDate showDate; // Ngày chiếu
    private LocalTime startTime; // Giờ bắt đầu
    private LocalTime endTime; // Giờ kết thúc
    private String roomName; // Tên phòng chiếu

    public MovieDTO(int movieId, String movieName, String trailerUrl, String content, String ageRestriction,
                    int duration, String director, String actor, String image, String genre, String imageBg, LocalDate showDate,
                    LocalTime startTime, LocalTime endTime, String roomName) {
        this.movieId = movieId;
        this.movieName = movieName;
        this.trailerUrl = trailerUrl;
        this.content = content;
        this.ageRestriction = ageRestriction;
        this.duration = duration;
        this.director = director;
        this.actor = actor;
        this.image = image;
        this.genre = genre;
        this.imageBg = imageBg;

        // Gán các thuộc tính của ca chiếu
        this.showDate = showDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomName = roomName;
    }
}
