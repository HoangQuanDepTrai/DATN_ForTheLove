package com.datn.demo.Controllers;

import com.datn.demo.Entities.*;
import com.datn.demo.Repositories.*;
import com.datn.demo.Services.VNPAYService;
import com.datn.demo.models.SeatInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import java.text.Normalizer;
import java.util.regex.Pattern;

@Controller
public class PaymentController {

    @Autowired
    private SeatRepository seatRepository; // Thêm SeatRepository

    @Autowired
    private VNPAYService vnPayService;

    @Autowired
    private CinemaInformationRepository cinemaInformationRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private TicketRepository ticketRepository; // Thêm TicketRepository
    @Autowired
    private ConsoleController consoleController;

    @GetMapping({"/thanhtoan"})
    public String home() {
        return "main/user/invoice"; // Trả về invoice.html
    }

    @PostMapping("/submitOrder")
    public String submitOrder(HttpServletRequest request, Model model, HttpSession session) {

        // Lấy tổng thanh toán từ session
        Integer totalPaymentInt = (Integer) session.getAttribute("totalPaymentInt");
        // Lấy danh sách ghế đã chọn từ session
        List<SeatInfo> selectedSeats = (List<SeatInfo>) session.getAttribute("selectedSeats");

        if (selectedSeats != null && !selectedSeats.isEmpty()) {
            System.out.println("Danh sách ghế đã chọn:");
            for (SeatInfo seat : selectedSeats) {
                System.out.println("Tên ghế: " + seat.getSeatName());
                System.out.println("Tên ghế: " + seat.getSeatId());

            }
        } else {
            System.out.println("Không có ghế nào được chọn.");
        }


        // Kiểm tra nếu giá trị tồn tại
        if (totalPaymentInt == null) {
            model.addAttribute("Message", "Lỗi");
            model.addAttribute("errorContent", "Không tìm thấy tài khoản thanh toán, vui lòng thử lại sau!!");
            return "main/user/404"; // Trả về trang lỗi nếu không tìm thấy
        }

        AccountEntity acc = (AccountEntity) session.getAttribute("acc");

        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

        // Chuyển tên đầy đủ thành không dấu
        String fullNameWithoutAccent = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
                .matcher(Normalizer.normalize(acc.getFullName(), Normalizer.Form.NFD))
                .replaceAll("");

        String vnpayUrl = vnPayService.createOrder(request, totalPaymentInt, fullNameWithoutAccent, baseUrl);
        return "redirect:" + vnpayUrl;
    }

    @GetMapping("/vnpay-payment-return")
    public String paymentCompleted(HttpServletRequest request, HttpSession session, Model model) {

        // Lấy và xóa totalPaymentInt khỏi session
        Integer totalPaymentInt = (Integer) session.getAttribute("totalPaymentInt");
        session.removeAttribute("totalPaymentInt"); // Xóa giá trị khỏi session

        int paymentStatus = vnPayService.orderReturn(request);

        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPriceString = request.getParameter("vnp_Amount");

        // Chuyển đổi tổng tiền từ String sang BigDecimal
        if (totalPriceString == null) {
            model.addAttribute("Message", "Lỗi");
            model.addAttribute("errorContent", "Không tìm thấy giá trị thanh toán, vui lòng thử lại sau!!");
            return "main/user/404"; // Trả về trang lỗi nếu không tìm thấy
        }

        BigDecimal totalPrice = new BigDecimal(totalPriceString).divide(BigDecimal.valueOf(100));

        model.addAttribute("orderId", orderInfo);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("paymentTime", paymentTime);
        model.addAttribute("transactionId", transactionId);

        /*lấy ghế đã chọn tư sesion*/
        List<SeatInfo> selectedSeats = (List<SeatInfo>) session.getAttribute("selectedSeats");

        if (selectedSeats != null && !selectedSeats.isEmpty()) {
            System.out.println("Danh sách ghế đã chọn:");
            for (SeatInfo seat : selectedSeats) {
                System.out.println("Tên ghế: " + seat.getSeatName());
                System.out.println("Tên ghế: " + seat.getSeatId());

            }
        } else {
            System.out.println("Không có ghế nào được chọn.");
        }

        // Nếu thanh toán thành công thì lưu hóa đơn vào cơ sở dữ liệu
        if (paymentStatus == 1) {
            InvoiceEntity invoice = createInvoice(session, totalPrice, orderInfo, paymentTime);
            if (invoice != null) {
                invoiceRepository.save(invoice);

                // Tạo ticket cho các ghế đã chọn
                createTickets(session, invoice);

                return "ordersuccess";
            } else {


                model.addAttribute("Message", "Lỗi");
                model.addAttribute("errorContent", "Không tìm thấy giá trị thanh toán, vui lòng thử lại sau!!");
                return "main/user/404"; // Trả về trang lỗi nếu không tìm thấy
            }


        } else {
            // Nếu thanh toán thất bại
            return "orderfail";
        }
    }

    private InvoiceEntity createInvoice(HttpSession session, BigDecimal totalPrice, String orderInfo, String paymentTime) {
        InvoiceEntity invoice = new InvoiceEntity();

        // Lấy tài khoản từ session
        AccountEntity account = (AccountEntity) session.getAttribute("acc");
        if (account != null) {

            // Sử dụng ID của tài khoản từ session
            invoice.setAccount(account);
        } else {
            return null; // Trả về null nếu không tìm thấy tài khoản trong session
        }


        // Lấy buổi chiếu từ session
        Integer showtimeId = (Integer) session.getAttribute("showtimeId");
        if (showtimeId != null) {
            ShowtimeEntity showtime = showtimeRepository.findByShowtimeId(showtimeId);
            if (showtime != null) {
                invoice.setShowtime(showtime);
            } else {
                return null; // Trả về null nếu không tìm thấy buổi chiếu
            }
        } else {
            return null; // Trả về null nếu không có ID buổi chiếu trong session
        }

        invoice.setBookingDate(LocalDate.now());

        // Chuyển đổi paymentTime thành LocalTime
        if (paymentTime != null) {
            try {
                LocalTime time = LocalTime.parse(paymentTime, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                invoice.setBookingTime(time);
            } catch (DateTimeParseException e) {
                // Xử lý ngoại lệ nếu định dạng không hợp lệ
            }
        }

        invoice.setStatus("Đã thanh toán");

        // Lấy danh sách chi tiết hóa đơn từ session
        List<InvoiceDetailsEntity> invoiceDetails = (List<InvoiceDetailsEntity>) session.getAttribute("invoiceDetails");
        invoice.setInvoiceDetails(invoiceDetails);
        if (invoiceDetails != null) {

            // Thiết lập invoice cho từng chi tiết hóa đơn
            for (InvoiceDetailsEntity detail : invoiceDetails) {
                detail.setInvoice(invoice); // Thiết lập hóa đơn cho chi tiết
                detail.calculateTotalPrice(); // Tính toán tổng giá nếu cần
            }
            invoice.setInvoiceDetails(invoiceDetails); // Thiết lập danh sách chi tiết vào hóa đơn
        }
        invoice.setTotalAmount(totalPrice);

        // Thiết lập các trường khác của hóa đơn và trả về
        invoice.setTotalAmount(totalPrice);
        return invoice;
    }


    private void createTickets(HttpSession session, InvoiceEntity invoice) {

        // Lấy danh sách ghế đã chọn từ session
        List<SeatInfo> selectedSeats = (List<SeatInfo>) session.getAttribute("selectedSeats");

        System.out.println(selectedSeats);

        // Kiểm tra xem có ghế nào đã chọn không
        if (selectedSeats != null && !selectedSeats.isEmpty()) {
            for (SeatInfo seatInfo : selectedSeats) {
                TicketEntity ticket = new TicketEntity();

                // Kiểm tra sự tồn tại của ghế
                if (seatRepository.existsById(seatInfo.getSeatId())) { // Kiểm tra sự tồn tại của ghế
                    ticket.setInvoiceId(invoice.getInvoiceId()); // Lưu ID hóa đơn
                    ticket.setSeatId(seatInfo.getSeatId()); // Lưu ID ghế

                    // Lấy buổi chiếu từ session
                    Integer showtimeId = (Integer) session.getAttribute("showtimeId");
                    ticket.setShowtimeId(showtimeId); // Thiết lập showtimeId

                    // Lưu ticket vào cơ sở dữ liệu
                    ticketRepository.save(ticket);
                } else {
                    System.out.println("Seat ID " + seatInfo.getSeatId() + " does not exist.");
                }
            }
        } else {

            // Xử lý trường hợp không có ghế nào được chọn
            System.out.println("No selected seats to create tickets.");
        }
    }
}
