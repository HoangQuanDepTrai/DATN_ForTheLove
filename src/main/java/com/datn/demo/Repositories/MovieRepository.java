
package com.datn.demo.Repositories;

import com.datn.demo.Entities.MovieEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, Integer> {

    List<MovieEntity> findByMovieNameContainingIgnoreCaseOrGenreContainingIgnoreCase(String movieName, String content);

	Page<MovieEntity> findAll(Pageable pageable);

    @Query(value = "SELECT m FROM MovieEntity m WHERE m.movieId NOT IN (SELECT st.movie.movieId FROM ShowtimeEntity st) AND m.releaseDate <= CURRENT_DATE")
    List<MovieEntity> findMoviesWithoutShowtime();
	
}
