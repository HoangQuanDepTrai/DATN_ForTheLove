package com.datn.demo.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "INVOICE_DETAILS")
public class InvoiceDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DETAIL_ID")
    private int detailId;

    @ManyToOne
    @JoinColumn(name = "INVOICE_ID", referencedColumnName = "INVOICE_ID", nullable = false)
    private InvoiceEntity invoice;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID", referencedColumnName = "PRODUCT_ID", nullable = false)
    private ProductEntity product;

    @Column(name = "QUANTITY", nullable = false)
    private Integer quantity;

    @Column(name = "PRICE", nullable = false)
    private Double price;

    @Column(name = "TOTAL_PRICE")
    private Double totalPrice;

    // Phương thức để tính toán totalPrice với kiểm tra null
    public void calculateTotalPrice() {
        if (price != null && quantity != null) {
            this.totalPrice = price * quantity;
        } else {
            this.totalPrice = 0.0; // Giá trị mặc định nếu một trong hai giá trị là null
        }
    }
}
