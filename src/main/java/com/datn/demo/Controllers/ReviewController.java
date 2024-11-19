package com.datn.demo.Controllers;

import com.datn.demo.Entities.MovieEntity;
import com.datn.demo.Entities.ReviewEntity;
import com.datn.demo.Services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // Lấy tất cả các đánh giá
    @GetMapping
    public List<ReviewEntity> getAllReviews() {
        return reviewService.getAllReviews();
    }

    // Lấy tất cả các đánh giá cho một bộ phim dựa trên ID phim
    @GetMapping("/movie")
    public ResponseEntity<List<ReviewEntity>> getReviewsByMovie(@RequestBody MovieEntity movie) {
        List<ReviewEntity> reviews = reviewService.getReviewsByMovie(movie);
        return ResponseEntity.ok(reviews);
    }

    // Tìm một đánh giá theo reviewId
    @GetMapping("/{id}")
    public ResponseEntity<ReviewEntity> getReviewById(@PathVariable Integer id) {
        Optional<ReviewEntity> review = reviewService.getReviewById(id);
        return review.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Lưu hoặc cập nhật đánh giá mới
    @PostMapping("/save")
    public ReviewEntity saveReview(@RequestBody ReviewEntity review) {
        review.setReviewDate(LocalDateTime.now()); // Gán ngày hiện tại
        return reviewService.saveReview(review);
    }

    // Cập nhật một đánh giá
    @PutMapping("/{id}")
    public ResponseEntity<ReviewEntity> updateReview(@PathVariable Integer id, @RequestBody ReviewEntity reviewDetails) {
        try {
            reviewDetails.setReviewDate(LocalDateTime.now()); // Gán ngày hiện tại
            ReviewEntity updatedReview = reviewService.updateReview(id, reviewDetails);
            return ResponseEntity.ok(updatedReview);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Xóa một đánh giá theo reviewId
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Integer id) {

        Optional<ReviewEntity> review = reviewService.getReviewById(id);
        if (review.isPresent()) {
            reviewService.deleteReviewById(id);
            return ResponseEntity.noContent().build(); // Trả về 204 No Content khi xóa thành công
        } else {
            return ResponseEntity.notFound().build(); // Trả về 404 Not Found nếu bình luận không tồn tại
        }
    }


    // Tạo một bình luận mới
    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody ReviewEntity review) {
        try {
            review.setReviewDate(LocalDateTime.now()); // Gán ngày hiện tại
            ReviewEntity newReview = reviewService.saveReview(review);
            return ResponseEntity.ok(newReview);
        } catch (RuntimeException e) {

            // Trả về lỗi 400 với thông báo lỗi
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}
