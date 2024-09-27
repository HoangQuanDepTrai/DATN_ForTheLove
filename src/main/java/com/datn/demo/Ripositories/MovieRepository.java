package com.datn.demo.Ripositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.datn.demo.Entities.MovieEntity;

public interface MovieRepository extends JpaRepository<MovieEntity, String> {
	// Bạn có thể thêm các phương thức tìm kiếm tùy chỉnh ở đây
	List<MovieEntity> findByGenreContaining(String genre);
}
