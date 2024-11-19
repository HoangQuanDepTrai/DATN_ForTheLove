package com.datn.demo.Services;

import com.datn.demo.Entities.TicketEntity;
import com.datn.demo.Repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public List<TicketEntity> getTicketsByShowtimeId(int showtimeId) {
        return ticketRepository.findTicketsByShowtimeId(showtimeId);
    }
}

