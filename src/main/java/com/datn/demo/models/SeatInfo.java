package com.datn.demo.models;

public class SeatInfo {
    
    private int seatId; // ID ghế
    private String seatName; // Tên ghế
    private double seatPrice; // Giá ghế
    private String seatStatus; // Trạng thái ghế

    public SeatInfo() {
    }

    public SeatInfo(int seatId, String seatName, double seatPrice, String seatStatus) {
        this.seatId = seatId;
        this.seatName = seatName;
        this.seatPrice = seatPrice;
        this.seatStatus = seatStatus;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public String getSeatName() {
        return seatName;
    }

    public void setSeatName(String seatName) {
        this.seatName = seatName;
    }

    public double getSeatPrice() {
        return seatPrice;
    }

    public void setSeatPrice(double seatPrice) {
        this.seatPrice = seatPrice;
    }

    public String getSeatStatus() {
        return seatStatus;
    }

    public void setSeatStatus(String seatStatus) {
        this.seatStatus = seatStatus;
    }
}