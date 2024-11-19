package com.datn.demo.Controllers;

import com.datn.demo.DTO.SeatDTO;
import com.datn.demo.Services.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class SeatController {

    @Autowired
    private SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @MessageMapping("/seat/select")
    @SendTo("/topic/seatStatus")
    public SeatDTO updateSeatStatus(SeatDTO seatDTO) {
        if ("selected".equals(seatDTO.getStatus())) {
            // Khóa ghế khi được chọn
            if (seatService.lockSeat(seatDTO.getSeatId())) {
                SeatDTO updatedSeat = seatService.updateSeatStatus(seatDTO);
                updatedSeat.setStatus("selected");
                return updatedSeat;
            } else {
                // Nếu ghế đã khóa, trả về thông tin ghế đã bị khóa
                seatDTO.setStatus("locked");
                return seatDTO;
            }
        } else if ("available".equals(seatDTO.getStatus())) {
            // Giải phóng ghế khi bỏ chọn
            seatDTO.setStatus("available");
            seatService.updateSeatStatus(seatDTO); // Cập nhật lại trạng thái ghế
            return seatDTO;
        }
        return seatDTO;
    }





}
