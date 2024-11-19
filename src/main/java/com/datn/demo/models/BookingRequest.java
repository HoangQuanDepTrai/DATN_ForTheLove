package com.datn.demo.models;

import java.util.List;

public class BookingRequest {

    private List<String> selectedSeats; // Danh sách ghế đã chọn
    private List<Product> products; // Danh sách sản phẩm đã mua
    private String showtimeId; // ID của ca chiếu

    // Constructor không tham số (bắt buộc khi sử dụng với Spring)
    public BookingRequest() {
    }

    // Getter và Setter cho selectedSeats
    public List<String> getSelectedSeats() {
        return selectedSeats;
    }

    public void setSelectedSeats(List<String> selectedSeats) {
        this.selectedSeats = selectedSeats;
    }

    // Getter và Setter cho products
    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    // Getter và Setter cho showtimeId
    public String getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(String showtimeId) {
        this.showtimeId = showtimeId;
    }
}
