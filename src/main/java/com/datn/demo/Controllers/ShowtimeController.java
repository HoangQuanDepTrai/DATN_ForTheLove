package com.datn.demo.Controllers;

import com.datn.demo.DTO.ShowtimeDetailsDTO;
import com.datn.demo.Entities.*;
import com.datn.demo.Services.MovieService;
import com.datn.demo.Services.ProductService;
import com.datn.demo.Services.ShowtimeService;
import com.datn.demo.Services.TicketService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class ShowtimeController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private ShowtimeService showtimeService;

    @Autowired
    private ProductService productService;

    @Autowired
    private TicketService ticketService;


    @GetMapping("/booking/{showtimeId}")
    public String bookMovieTicket(@PathVariable("showtimeId") int showtimeId, Model model, HttpSession session, RedirectAttributes redirectAttributes) {

        // Lấy thông tin ca chiếu theo ID
        if (session.getAttribute("acc") == null) {
            redirectAttributes.addFlashAttribute("phaidangnhap", "Bạn phải đăng nhập để sử dụng chức năng đặt vé!");

            // Chuyển hướng đến trang đăng nhập với tham số ngôn ngữ
            return "redirect:/login"; // Nếu chưa đăng nhập, chuyển hướng đến trang đăng nhập
        }

        Optional<ShowtimeEntity> showtimeOpt = showtimeService.getShowtimeById(showtimeId);

        if (showtimeOpt.isPresent()) {

            // Lấy thông tin phim từ ca chiếu
            ShowtimeEntity showtimeEntity = showtimeOpt.get();
            MovieEntity movie = showtimeEntity.getMovie();
            model.addAttribute("movies", movie);

            // Định dạng ngày showDate
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String formattedDate = showtimeEntity.getShowDate().format(formatter);

            // Chuyển đổi ShowtimeEntity sang ShowtimeDetailsDTO
            ShowtimeDetailsDTO showtimeDetails = new ShowtimeDetailsDTO(
                    showtimeEntity.getShowtimeId(),
                    movie.getMovieName(),
                    movie.getAgeRestriction(),
                    showtimeEntity.getStartTime(),
                    showtimeEntity.getEndTime(),
                    showtimeEntity.getShowDate(),
                    showtimeEntity.getRoom().getRoomName(),
                    movie.getMovieId(),
                    movie.getGenre(),
                    movie.getContent(),
                    movie.getImage(),
                    movie.getDirector(),
                    movie.getActor(),
                    movie.getImage_bg(),
                    movie.getTrailerUrl(),
                    movie.getReleaseDate()
            );

            // Thêm Sản Phẩm vào model
            List<ProductEntity> products = productService.getAllProducts();
            model.addAttribute("products", products);

            // Lấy thông tin phòng chiếu
            RoomEntity room = showtimeEntity.getRoom();
            model.addAttribute("room", room);

            // Lấy danh sách ghế từ phòng chiếu
            List<SeatEntity> seats = room.getSeats();

            // Lấy danh sách vé theo showtimeId để kiểm tra trạng thái ghế
            List<TicketEntity> tickets = ticketService.getTicketsByShowtimeId(showtimeId);
            model.addAttribute("seats", seats);
            model.addAttribute("tickets", tickets);

            // Lấy danh sách seatName đã đặt
            List<String> bookedSeatNames = tickets.stream()
                    .map(ticket -> ticket.getSeatId().toString()) // Chuyển seatId thành chuỗi, nếu seatName là seatId
                    .collect(Collectors.toList());
            model.addAttribute("bookedSeatNames", bookedSeatNames);

            // Thêm ShowtimeDetailsDTO vào model
            model.addAttribute("showtimes", showtimeDetails);

            // Trả về trang đặt vé
            return "main/user/booking";
        } else {

            // Nếu không tìm thấy ca chiếu, trả về trang chủ hoặc trang lỗi
            return "main/index";
        }
    }
}
