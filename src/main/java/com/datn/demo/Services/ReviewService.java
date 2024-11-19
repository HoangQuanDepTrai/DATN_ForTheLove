package com.datn.demo.Services;

import com.datn.demo.Entities.ReviewEntity;
import com.datn.demo.Entities.MovieEntity;
import com.datn.demo.Entities.AccountEntity;
import com.datn.demo.Repositories.ReviewRepository;
import com.datn.demo.Repositories.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    // Lấy tất cả các đánh giá
    public List<ReviewEntity> getAllReviews() {
        return reviewRepository.findAll();
    }

    // Lấy tất cả các đánh giá cho một bộ phim dựa trên ID phim
    public List<ReviewEntity> getReviewsByMovie(MovieEntity movie) {
        return reviewRepository.findByMovie(movie);
    }

    // Tìm một đánh giá theo reviewId
    public Optional<ReviewEntity> getReviewById(Integer reviewId) {
        return reviewRepository.findById(reviewId);
    }

    // Tìm một đánh giá theo reviewId và trả về null nếu không tìm thấy
    public ReviewEntity findReviewById(Integer reviewId) {
        return reviewRepository.findById(reviewId).orElse(null);
    }

    // Tạo mới một đánh giá, chỉ khi người dùng có hóa đơn cho bộ phim đó
    public ReviewEntity createReview(ReviewEntity review) {

        Integer accountId = review.getAccount().getAccountId();
        Integer movieId = review.getMovie().getMovieId();

        // Kiểm tra xem người dùng đã mua vé cho bộ phim này chưa
        if (invoiceRepository.existsInvoiceForMovie(accountId, movieId)) {
            return reviewRepository.save(review);
        } else {
            throw new RuntimeException("Không thể thêm bình luận. Bạn chưa mua vé cho bộ phim này.");
        }
    }

    // Lưu hoặc cập nhật đánh giá mới, có thể dùng chung cho việc cập nhật
    public ReviewEntity saveReview(ReviewEntity review) {
        return createReview(review); // Sử dụng logic tương tự như createReview để kiểm tra hóa đơn
    }

    // Cập nhật đánh giá
    public ReviewEntity updateReview(Integer reviewId, ReviewEntity reviewDetails) {
        Optional<ReviewEntity> optionalReview = reviewRepository.findById(reviewId);

        if (optionalReview.isPresent()) {
            ReviewEntity existingReview = optionalReview.get();
            existingReview.setContent(reviewDetails.getContent());
            existingReview.setAccount(reviewDetails.getAccount());
            existingReview.setMovie(reviewDetails.getMovie());
            return saveReview(existingReview); // Sử dụng saveReview để đảm bảo kiểm tra hóa đơn
        } else {
            throw new RuntimeException("Review not found with id " + reviewId);
        }
    }

    // Xóa một đánh giá theo reviewId
    public void deleteReviewById(Integer reviewId) {

        // Kiểm tra xem bình luận có tồn tại trước khi xóa
        if (reviewRepository.existsById(reviewId)) {
            reviewRepository.deleteById(reviewId);
        } else {
            throw new RuntimeException("Review not found with id " + reviewId);
        }
    }
}
