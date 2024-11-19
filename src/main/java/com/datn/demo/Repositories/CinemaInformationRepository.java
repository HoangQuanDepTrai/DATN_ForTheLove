package com.datn.demo.Repositories;


import com.datn.demo.Entities.CinemaInformationEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CinemaInformationRepository extends JpaRepository<CinemaInformationEntity, Integer> {

    // Additional query methods can be added here if needed

    @Query("SELECT c FROM CinemaInformationEntity c JOIN c.showtimes s WHERE s.showtimeId = :showtimeId")
    CinemaInformationEntity findCinemaByShowtimeId(@Param("showtimeId") Integer showtimeId);
    

}