package com.datn.demo.DTO;

import com.datn.demo.Entities.AccountEntity;
import com.datn.demo.Entities.ShowtimeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDTO {
    
    private Integer invoiceId; // Mã hóa đơn
    private ShowtimeEntity showTime; // Đối tượng ShowtimeEntity
    private AccountEntity account; // Đối tượng AccountEntity
    private Double totalAmount; // Tổng số tiền

    // Danh sách chi tiết hóa đơn
    private List<InvoiceDetailDTO> invoiceDetails; // Danh sách các chi tiết hóa đơn

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InvoiceDetailDTO {
        private int detailId; // Mã chi tiết hóa đơn
        private int productId; // Mã sản phẩm
        private String productName; // Tên sản phẩm
        private Integer quantity; // Số lượng sản phẩm
        private Double price; // Giá của sản phẩm
        private Double totalPrice; // Tổng giá trị

        // Phương thức để tính toán totalPrice
        public void calculateTotalPrice() {
            this.totalPrice = price * quantity;
        }
    }
}
