package com.datn.demo.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SeatDTO {

    private int seatId;      // Mã ghế
    private int roomId;      // Mã phòng (để đơn giản, chỉ lưu roomId thay vì đối tượng RoomEntity)
    private String seatName; // Tên ghế
    private String status;   // Trạng thái ghế (đã đặt, còn trống)
    private Double seatPrice; // Giá ghế
    private String seatStatus;

    @Override
    public String toString() {
        return "Ghế: " + seatName + " (ID: " + seatId + ", Giá: " + seatPrice + " VNĐ)";
    }
}
