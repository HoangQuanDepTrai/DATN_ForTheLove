package com.datn.demo.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datn.demo.Entities.MovieEntity;
import com.datn.demo.Ripositories.MovieRepository;

@Service
public class MovieService {
	@Autowired
	private MovieRepository movieRepository;

	// Lấy tất cả phim
	public List<MovieEntity> getAllMovies() {
		return movieRepository.findAll();
	}

	// Tìm phim theo thể loại
	public List<MovieEntity> findMoviesByGenre(String genre) {
		return movieRepository.findByGenreContaining(genre);
	}

	// Lưu phim mới
	public MovieEntity saveMovie(MovieEntity movie) {
		return movieRepository.save(movie);
	}

	// Xóa phim
	public void deleteMovie(String movieId) {
		movieRepository.deleteById(movieId);
	}

	public Optional<MovieEntity> getMovieById(String movieId) {
		// Sử dụng phương thức findById của repository để tìm movie theo ID
		return movieRepository.findById(movieId);
	}
}
