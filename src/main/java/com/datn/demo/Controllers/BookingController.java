package com.datn.demo.Controllers;

import com.datn.demo.DTO.ShowtimeDetailsDTO;
import com.datn.demo.Entities.*;
import com.datn.demo.Repositories.CinemaInformationRepository;
import com.datn.demo.Services.*;
import com.datn.demo.models.SeatInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SeatService seatService; // Khai báo SeatService

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private CinemaInformationService cinemaInformationService;

    @PostMapping("/confirmBooking")
    public String confirmBooking(
            @RequestParam("showtimeId") int showtimeId,
            @RequestParam("selectedSeats") String selectedSeatsJson,
            @RequestParam("products") String productsJson,
            Model model,
            HttpSession session
            ) {

        // Kiểm tra đăng nhập
        AccountEntity acc = (AccountEntity) session.getAttribute("acc");
        if (acc == null) {
            return "main/user/user-login"; // Chuyển hướng đến trang đăng nhập nếu chưa đăng nhập
        }

        // Phân tích ghế từ JSON
        List<SeatInfo> selectedSeats = parseSeatsFromJson(selectedSeatsJson);
        List<CinemaInformationEntity> cinemas = cinemaInformationService.getAllCinemas();

        // Lưu thông tin ghế đã chọn vào session
        session.setAttribute("selectedSeats", selectedSeats);

        // Khai báo biến tính tổng giá ghế
        double totalSeatPrice = 0;

        for (SeatInfo seat : selectedSeats) {
            SeatEntity seatEntity = seatService.findById(seat.getSeatId()).orElse(null);
            if (seatEntity != null) {
                seat.setSeatPrice(seatEntity.getSeatPrice());
                seat.setSeatStatus(seatEntity.getStatus());
                totalSeatPrice += seatEntity.getSeatPrice();
            }
        }

        // Lấy chi tiết ca chiếu
        ShowtimeDetailsDTO showtimeDetails = bookingService.getShowtimeDetailsById(showtimeId);
        if (showtimeDetails == null) {
            model.addAttribute("error", "Không tìm thấy thông tin ca chiếu.");
            return "error";
        }

        // Lưu showtimeId vào session
        session.setAttribute("showtimeId", showtimeDetails.getShowtimeId());

        // Thêm dữ liệu vào model để hiển thị trên trang hóa đơn
        model.addAttribute("showtimeId", showtimeDetails.getShowtimeId());
        model.addAttribute("movieImageBG", showtimeDetails.getImage_bg());
        model.addAttribute("movieName", showtimeDetails.getMovieName());
        model.addAttribute("roomName", showtimeDetails.getRoomName());
        model.addAttribute("startTime", showtimeDetails.getStartTime());
        model.addAttribute("endTime", showtimeDetails.getEndTime());
        model.addAttribute("selectedSeats", selectedSeats);
        model.addAttribute("showtimeDate", showtimeDetails.getShowDate());

        List<Product> selectedProducts = parseProductsFromJson(productsJson);
        List<ProductEntity> productEntities = new ArrayList<>();
        double totalProductPrice = 0;

        List<InvoiceDetailsEntity> invoiceDetailsList = new ArrayList<>();

        for (Product product : selectedProducts) {
            ProductEntity productEntity = productService.getProductById(product.getProductId()).orElse(null);
            if (productEntity != null) {
                double productPrice = productEntity.getProductPrice() * product.getQuantity();
                totalProductPrice += productPrice;
                productEntities.add(productEntity);

                InvoiceDetailsEntity invoiceDetail = new InvoiceDetailsEntity();
                invoiceDetail.setProduct(productEntity);
                invoiceDetail.setQuantity(product.getQuantity());
                invoiceDetail.setPrice(productEntity.getProductPrice());
                invoiceDetail.calculateTotalPrice();
                invoiceDetailsList.add(invoiceDetail);
            }
        }

        session.setAttribute("invoiceDetails", invoiceDetailsList);

        model.addAttribute("products", productEntities.stream()
                .map(productEntity -> new ProductEntityWithQuantity(productEntity,
                        selectedProducts.stream().filter(p -> p.getProductId() == productEntity.getProductId())
                                .findFirst().orElse(new Product()).getQuantity()))
                .collect(Collectors.toList()));

        model.addAttribute("totalProductPrice", totalProductPrice);
        model.addAttribute("totalSeatPrice", totalSeatPrice);

        double totalPayment = totalProductPrice + totalSeatPrice;
        int totalPaymentInt = (int) totalPayment;
        session.setAttribute("totalPaymentInt", totalPaymentInt);
        model.addAttribute("totalPayment", totalPaymentInt);

        model.addAttribute("cinemas", cinemas);


        // Hiển thị trang hóa đơn cinema
        CinemaInformationEntity cinemaShowtimes = cinemaInformationService.getCinemaByShowtimeId(showtimeDetails.getShowtimeId());
        model.addAttribute("cinemaShowtime", cinemaShowtimes);
        return "main/user/invoice";
    }

    // Phương thức parse JSON cho ghế
    private List<SeatInfo> parseSeatsFromJson(String selectedSeatsJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Phân tích chuỗi JSON thành danh sách ghế
            return objectMapper.readValue(selectedSeatsJson, new TypeReference<List<SeatInfo>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList(); // Trả về danh sách rỗng nếu có lỗi
        }
    }

    // Phương thức parse JSON cho sản phẩm
    private List<Product> parseProductsFromJson(String productsJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Phân tích chuỗi JSON thành danh sách sản phẩm
            return objectMapper.readValue(productsJson, new TypeReference<List<Product>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList(); // Trả về danh sách rỗng nếu có lỗi
        }
    }

    public static class Product {
        private int productId; // ID sản phẩm
        private int quantity; // Số lượng

        // Constructor không tham số
        public Product() {
        }

        public Product(int productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }

    public static class ProductEntityWithQuantity {
        private ProductEntity productEntity;
        private int quantity;

        public ProductEntityWithQuantity(ProductEntity productEntity, int quantity) {
            this.productEntity = productEntity;
            this.quantity = quantity;
        }

        public ProductEntity getProductEntity() {
            return productEntity;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}
