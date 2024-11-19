package com.datn.demo.Repositories;

import com.datn.demo.Entities.AccountEntity;
import com.datn.demo.Entities.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository<TicketEntity, Integer> {

    @Query("SELECT t FROM TicketEntity t WHERE t.showtimeId = :showtimeId")
    List<TicketEntity> findTicketsByShowtimeId(@Param("showtimeId") int showtimeId);

}






