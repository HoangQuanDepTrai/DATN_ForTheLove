package com.datn.demo.Controllers;

import com.datn.demo.DTO.TicketDetailsDTO;
import com.datn.demo.Repositories.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class TicketController {

    @Autowired
    private InvoiceRepository invoiceRepository;

    // Phương thức GET để hiển thị form nhập Invoice ID
    @GetMapping("/ticket")
    public String showTicketForm() {
        return "ticketPrint"; // Trả về trang có form nhập
    }

    //@RequestParam("invoiceId") lấy từ name="invoiceId" của input invoiceId bên /ticket
    @PostMapping("/ticket")
    public String getTicketDetails(@RequestParam("invoiceId") Integer invoiceId, Model model) {
        // Gọi phương thức trong repository để lấy dữ liệu dựa trên invoiceId
        List<TicketDetailsDTO> ticketDetails = invoiceRepository.findSeatMovieShowtimeDetailsByInvoiceId(invoiceId);

        // Đưa dữ liệu vào model để hiển thị trên trang
        model.addAttribute("ticketDetails", ticketDetails);

        // Trả về view "ticket" để hiển thị thông tin
        return "ticketPrint";
    }
}
