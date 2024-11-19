package com.datn.demo.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "REVIEW")
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REVIEW_ID")
    private Integer reviewId;

    @Column(name = "REVIEW_CONTENT", nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "ACCOUNT_ID", nullable = false)
    private AccountEntity account;

    @ManyToOne
    @JoinColumn(name = "MOVIE_ID", nullable = false)
    private MovieEntity movie;

    // Thêm cột REVIEW_DATE
    @Column(name = "REVIEW_DATE")
    private LocalDateTime reviewDate;
}
