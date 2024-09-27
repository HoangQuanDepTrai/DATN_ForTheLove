package com.datn.demo.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MOVIE")
public class MovieEntity {
	@Id
	@Column(name = "MOVIE_ID", nullable = false, length = 255)
	private String movieId; // Mã phim

	@Column(name = "MOVIE_NAME", nullable = false, length = 255)
	private String movieName; // Tên phim

	@Column(name = "TRAILER_URL", columnDefinition = "NVARCHAR(MAX)")
	private String trailerUrl; // Đường dẫn trailer

	@Column(name = "CONTENT", columnDefinition = "NVARCHAR(MAX)")
	private String content; // Nội dung phim

	@Column(name = "AGE_RESTRICTION", length = 50)
	private String ageRestriction; // Giới hạn độ tuổi

	@Column(name = "DURATION")
	private int duration; // Thời gian phim (phút)

	@Column(name = "DIRECTOR", length = 255)
	private String director; // Đạo diễn

	@Column(name = "ACTOR", length = 255)
	private String actor; // Diễn viên

	@Column(name = "IMAGE", columnDefinition = "NVARCHAR(MAX)")
	private String image; // Hình ảnh phim

	@Column(name = "GENRE", length = 255)
	private String genre; // Thể loại phim
	
	@Column(name = "IMAGE_BG", length = 255)
	private String image_bg;
}
