package com.datn.demo.Controllers;

import com.datn.demo.DTO.TicketDetailsDTO;
import com.datn.demo.Entities.AccountEntity;
import com.datn.demo.Repositories.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpSession; // Import HttpSession

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Controller
public class PrintController {

    @Autowired
    private InvoiceRepository invoiceRepository;

    private String hashInvoiceId(Integer invoiceId) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(invoiceId.toString().getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing invoiceId", e);
        }
    }

    @GetMapping("/print/{hashedInvoiceId}")
    public String getTicketDetails(
            @PathVariable("hashedInvoiceId") String hashedInvoiceId,
            Model model,
            @SessionAttribute(value = "acc", required = false) AccountEntity acc) {

        // Kiểm tra xem người dùng đã đăng nhập chưa
        if (acc == null) {
            return "redirect:/login"; // Nếu chưa đăng nhập, chuyển hướng đến trang đăng nhập
        }

        // Kiểm tra nếu người dùng là admin
        if (!acc.getRole().getRoleName().equalsIgnoreCase("admin")) {
            return "redirect:/printError"; // Trả về trang 404 nếu không phải admin
        }

        // Duyệt qua các invoice và tìm `invoiceId` khớp với `hashedInvoiceId`
        Optional<Integer> matchingInvoiceId = invoiceRepository.findAll()
                .stream()
                .filter(invoice -> hashInvoiceId(invoice.getInvoiceId()).equals(hashedInvoiceId))
                .map(invoice -> invoice.getInvoiceId())
                .findFirst();

        if (matchingInvoiceId.isPresent()) {
            List<TicketDetailsDTO> ticketDetails = invoiceRepository.findSeatMovieShowtimeDetailsByInvoiceId(matchingInvoiceId.get());
            model.addAttribute("ticketDetails", ticketDetails);
            return "main/user/ticketPrint"; // Trả về view để hiển thị danh sách vé
        } else {
            model.addAttribute("Message", "Thông báo");
            model.addAttribute("errorContent", "Không tìm thấy hóa đơn, vui lòng thử lại sau!!");
            return "main/user/404"; // Trả về trang lỗi nếu không tìm thấy
        }
    }

    // Controller cho trang lỗi
    @GetMapping("/printError")
    public String printError(Model model) {
        model.addAttribute("Message", "Thông báo");
        model.addAttribute("errorContent", "Vai trò của bạn không đủ để thực hiện tính năng này!!");
        return "main/user/404"; // Trả về trang lỗi
    }
}
