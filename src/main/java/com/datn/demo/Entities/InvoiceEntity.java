package com.datn.demo.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "INVOICE")
public class InvoiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INVOICE_ID")
    private Integer invoiceId;

    @ManyToOne
    @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID")
    private AccountEntity account; // Liên kết với tài khoản

    @ManyToOne
    @JoinColumn(name = "SHOWTIME_ID", referencedColumnName = "SHOWTIME_ID")
    private ShowtimeEntity showtime; // Liên kết với buổi chiếu


    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL) // Liên kết với danh sách chi tiết hóa đơn
    private List<InvoiceDetailsEntity> invoiceDetails;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<TicketEntity> tickets; // Mối quan hệ với danh sách vé

    @Column(name = "BOOKING_DATE")
    private LocalDate bookingDate;

    @Column(name = "BOOKING_TIME")
    private LocalTime bookingTime;

    @Column(name = "TOTAL_AMOUNT")
    private BigDecimal totalAmount;

    @Column(name = "STATUS")
    private String status; // Thêm cột trạng thái (thành công/thất bại)

    public BigDecimal calculateTotalAmount() {
        if (invoiceDetails == null || invoiceDetails.isEmpty()) {
            return BigDecimal.ZERO; // Nếu không có chi tiết hóa đơn, trả về 0
        }
        return invoiceDetails.stream().map(detail -> BigDecimal.valueOf(detail.getTotalPrice())).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
