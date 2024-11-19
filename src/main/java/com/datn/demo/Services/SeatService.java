package com.datn.demo.Services;

import com.datn.demo.DTO.SeatDTO;
import com.datn.demo.Entities.RoomEntity;
import com.datn.demo.Entities.SeatEntity;
import com.datn.demo.Repositories.RoomRepository;
import com.datn.demo.Repositories.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class SeatService {

    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private RoomRepository roomRepository;

    // Bảng để theo dõi các ghế đã bị khóa
    private Map<Integer, LocalDateTime> lockedSeats = new HashMap<>();

    // Tìm kiếm ghế theo ID
    public Optional<SeatEntity> findById(int seatId) {
        return seatRepository.findById(seatId);
    }

    // Cập nhật trạng thái ghế trong cơ sở dữ liệu
    public SeatDTO updateSeatStatus(SeatDTO seatDTO) {
        RoomEntity roomEntity = roomRepository.findById(seatDTO.getRoomId()).orElseThrow();
        SeatEntity seatEntity = toEntity(seatDTO, roomEntity);
        seatEntity.setStatus(seatDTO.getStatus());
        seatRepository.save(seatEntity);
        return toDTO(seatEntity);
    }

    // Hàm để khóa ghế trong 5 phút
    public boolean lockSeat(int seatId) {
        if (lockedSeats.containsKey(seatId)) {
            // Kiểm tra nếu ghế đã bị khóa và kiểm tra thời gian khóa
            LocalDateTime unlockTime = lockedSeats.get(seatId);
            if (LocalDateTime.now().isBefore(unlockTime)) {
                return false; // Ghế đã bị khóa, không thể chọn
            } else {
                // Nếu hết thời gian khóa, giải phóng ghế
                lockedSeats.remove(seatId);
            }
        }
        // Khóa ghế trong 5 phút
        lockedSeats.put(seatId, LocalDateTime.now().plusMinutes(1));
        return true;
    }

    // Giải phóng ghế đã khóa khi hết thời gian khóa
    // Giải phóng ghế khi hết thời gian khóa
    @Scheduled(fixedRate = 10000)
    public void unlockSeats() {
        LocalDateTime now = LocalDateTime.now();
        // Loại bỏ các ghế đã hết thời gian khóa và cập nhật trạng thái về "available"
        lockedSeats.entrySet().removeIf(entry -> {
            if (entry.getValue().isBefore(now)) {
                int seatId = entry.getKey();
                // Cập nhật trạng thái ghế về "available" khi thời gian khóa đã hết
                Optional<SeatEntity> seatEntityOpt = seatRepository.findById(seatId);
                if (seatEntityOpt.isPresent()) {
                    SeatEntity seatEntity = seatEntityOpt.get();
                    seatEntity.setStatus("available");
                    seatRepository.save(seatEntity);
                    return true; // Ghế đã hết khóa và được giải phóng
                }
            }
            return false;
        });
    }



    // Convert từ SeatEntity sang SeatDTO
    private SeatDTO toDTO(SeatEntity seatEntity) {
        SeatDTO seatDTO = new SeatDTO();
        seatDTO.setSeatId(seatEntity.getSeatId());
        seatDTO.setRoomId(seatEntity.getRoom().getRoomId());
        seatDTO.setSeatName(seatEntity.getSeatName());
        seatDTO.setStatus(seatEntity.getStatus());
        seatDTO.setSeatPrice(seatEntity.getSeatPrice());
        return seatDTO;
    }

    // Convert từ SeatDTO sang SeatEntity
    private SeatEntity toEntity(SeatDTO seatDTO, RoomEntity roomEntity) {
        SeatEntity seatEntity = new SeatEntity();
        seatEntity.setSeatId(seatDTO.getSeatId());
        seatEntity.setRoom(roomEntity);
        seatEntity.setSeatName(seatDTO.getSeatName());
        seatEntity.setStatus(seatDTO.getStatus());
        seatEntity.setSeatPrice(seatDTO.getSeatPrice());
        return seatEntity;
    }
}
