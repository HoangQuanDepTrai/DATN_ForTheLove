package com.datn.demo.Services;

import com.datn.demo.Entities.CinemaInformationEntity;
import com.datn.demo.Entities.MovieEntity;
import com.datn.demo.Entities.ShowtimeEntity;
import com.datn.demo.Repositories.CinemaInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class CinemaInformationService {

    private final CinemaInformationRepository cinemaInformationRepository;

    @Autowired
    public CinemaInformationService(CinemaInformationRepository cinemaInformationRepository) {
        this.cinemaInformationRepository = cinemaInformationRepository;
    }

    public List<CinemaInformationEntity> getAllCinemas() {
        return cinemaInformationRepository.findAll();
    }
   
    // Phương thức để lấy rạp chiếu với các suất chiếu chỉ thuộc phim được chọn
    public List<CinemaInformationEntity> getCinemasWithShowtimesByMovie(int movieId) {
        List<CinemaInformationEntity> cinemas = cinemaInformationRepository.findAll();

        for (CinemaInformationEntity cinema : cinemas) {
            // Lọc các suất chiếu theo movieId
            List<ShowtimeEntity> filteredShowtimes = cinema.getShowtimes().stream()
                    .filter(showtime -> showtime.getMovie().getMovieId() == movieId)
                    .collect(Collectors.toList());

            // Gán lại danh sách suất chiếu đã được lọc
            cinema.setShowtimes(filteredShowtimes);
        }

        return cinemas;
    }


    // Phương thức để lấy một rạp theo suất chiếu
    public CinemaInformationEntity getCinemaByShowtimeId(Integer showtimeId) {
        // Tìm rạp theo showtimeId
        return cinemaInformationRepository.findCinemaByShowtimeId(showtimeId);
    }


}
