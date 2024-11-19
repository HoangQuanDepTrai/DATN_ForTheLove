package com.datn.demo.Entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TICKET")
public class TicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TICKET_ID")
    private int ticketId;

    @Column(name = "INVOICE_ID")
    private Integer invoiceId; // Thêm trường này

    @Column(name = "SEAT_ID")
    private Integer seatId;

    @ManyToOne
    @JoinColumn(name = "SHOWTIME_ID", referencedColumnName = "SHOWTIME_ID", insertable = false, updatable = false)
    private ShowtimeEntity showtime;

    // Thêm trường showtimeId để thực hiện truy vấn
    @Column(name = "SHOWTIME_ID", insertable = true, updatable = true)
    private Integer showtimeId;

    @ManyToOne
    @JoinColumn(name = "SEAT_ID", referencedColumnName = "SEAT_ID", insertable = false, updatable = false)
    private SeatEntity seat;

    @ManyToOne
    @JoinColumn(name = "INVOICE_ID", referencedColumnName = "INVOICE_ID", insertable = false, updatable = false)
    private InvoiceEntity invoice;
}
