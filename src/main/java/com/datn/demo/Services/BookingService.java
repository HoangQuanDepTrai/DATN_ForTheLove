package com.datn.demo.Services;

import com.datn.demo.DTO.ShowtimeDetailsDTO;
import com.datn.demo.Entities.ShowtimeEntity;
import com.datn.demo.Repositories.ShowtimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {
    @Autowired
    private ShowtimeRepository showtimeRepository;

    // Lấy thông tin ca chiếu theo ID
    public ShowtimeDetailsDTO getShowtimeDetailsById(int showtimeId) {

        ShowtimeEntity showtimeEntity = showtimeRepository.findByShowtimeId(showtimeId);
        if (showtimeEntity != null) {
            // Chuyển đổi từ ShowtimeEntity sang ShowtimeDetailsDTO
            return new ShowtimeDetailsDTO(
                    showtimeEntity.getShowtimeId(),
                    showtimeEntity.getMovie().getMovieName(),
                    showtimeEntity.getMovie().getAgeRestriction(),
                    showtimeEntity.getStartTime(),
                    showtimeEntity.getEndTime(),
                    showtimeEntity.getShowDate(),
                    showtimeEntity.getRoom().getRoomName(),
                    showtimeEntity.getMovie().getMovieId(),
                    showtimeEntity.getMovie().getGenre(),
                    showtimeEntity.getMovie().getContent(),
                    showtimeEntity.getMovie().getImage(),
                    showtimeEntity.getMovie().getDirector(),
                    showtimeEntity.getMovie().getActor(),
                    showtimeEntity.getMovie().getImage_bg(),
                    showtimeEntity.getMovie().getTrailerUrl(),
                    showtimeEntity.getMovie().getReleaseDate()
            );
        }
        return null; // Trả về null nếu không tìm thấy
    }

    // Phương thức lấy tất cả thông tin ca chiếu
    public List<ShowtimeDetailsDTO> getAllShowtimeDetails() {
        return showtimeRepository.findAllShowtimeDetails();
    }
}
