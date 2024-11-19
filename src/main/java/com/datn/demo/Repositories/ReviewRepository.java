package com.datn.demo.Repositories;

import com.datn.demo.Entities.MovieEntity;
import com.datn.demo.Entities.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Integer> {

    // Truy vấn danh sách đánh giá dựa trên MovieEntity
    List<ReviewEntity> findByMovie(MovieEntity movie);
}
