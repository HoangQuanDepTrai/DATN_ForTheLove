package com.datn.demo.Controllers;

import com.datn.demo.Entities.MovieEntity;
import com.datn.demo.Repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "*")
public class MovieControllerReact {

    @Autowired
    private MovieRepository movieRepository;

    /**
     * API để thêm một bộ phim mới.
     * @param movie: Dữ liệu phim từ frontend gửi lên.
     * @return: Thông tin bộ phim đã được lưu.
     */
    @PostMapping
    public ResponseEntity<MovieEntity> addMovie(@RequestBody MovieEntity movie) {
        MovieEntity savedMovie = movieRepository.save(movie);
        return ResponseEntity.ok(savedMovie);
    }

    /**
     * API để lấy danh sách tất cả các bộ phim.
     * @return: Danh sách các bộ phim trong database.
     */
    @GetMapping
    public ResponseEntity<Iterable<MovieEntity>> getAllMovies() {
        Iterable<MovieEntity> movies = movieRepository.findAll();
        return ResponseEntity.ok(movies);
    }
}
