package com.datn.demo.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "CINEMA_INFORMATION")
public class CinemaInformationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CINEMA_ID")
    private Integer cinemaId;

    @Column(name = "CINEMA_NAME", nullable = false, length = 255)
    private String cinemaName;

    @Column(name = "ADDRESS", nullable = false, length = 255)
    private String address;

    @Column(name = "PHONE_NUMBER", length = 20)
    private String phoneNumber;

    @Column(name = "EMAIL", length = 255)
    private String email;

    @OneToMany(mappedBy = "cinemaInformation", fetch = FetchType.LAZY)
    private List<ShowtimeEntity> showtimes;

    @Transient // Không lưu trữ vào cơ sở dữ liệu
    private List<MovieEntity> movies; // Danh sách phim đã được nhóm theo suất chiếu
}
