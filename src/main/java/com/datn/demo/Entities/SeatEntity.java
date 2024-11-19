package com.datn.demo.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "SEAT")
public class SeatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEAT_ID")
    private int seatId; // Mã ghế

    @ManyToOne
    @JoinColumn(name = "ROOM_ID", referencedColumnName = "ROOM_ID", nullable = false)
    private RoomEntity room; // Tham chiếu tới RoomEntity

    @Column(name = "SEAT_NAME", nullable = false)
    private String seatName; // Tên ghế

    @Column(name = "STATUS", nullable = false)
    private String status; // Trạng thái ghế (ví dụ: đã đặt, còn trống)


    @Column(name = "SEAT_PRICE", precision = 18, scale = 2)
    private Double seatPrice; // Giá ghế

    @Column(name = "SEAT_STATUS", precision = 18, scale = 2)
    private Double seatStatus;
    
    @Override
    public String toString() {
        return "Ghế: " + seatName + " (ID: " + seatId + ", Giá: " + seatPrice + " VNĐ)";
    }
}
