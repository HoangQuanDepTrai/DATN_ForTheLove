package com.datn.demo.AdminController;

import com.datn.demo.DTO.FullMovieShowtimeDTO;
import com.datn.demo.DTO.RoomDTO;
import com.datn.demo.Entities.MovieEntity;
import com.datn.demo.Entities.RoomEntity;
import com.datn.demo.Entities.ShowtimeEntity;
import com.datn.demo.Services.ShowtimeService;
import com.datn.demo.Services.MovieService;
import com.datn.demo.Services.RoomService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/showtime")
public class AdminController {
    @Autowired
    private ShowtimeService showtimeService;

    @Autowired
    private MovieService movieService;

    @Autowired
    private RoomService roomService;

    @GetMapping("/form")
    public String showForm(@RequestParam(required = false) Integer showtimeId, Model model) {
        // Lấy thông tin suất chiếu nếu có showtimeId
        ShowtimeEntity showtime = showtimeId == null ? new ShowtimeEntity() : showtimeService.getShowtimeById(showtimeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy suất chiếu"));

        model.addAttribute("showtime", showtime);

        // Lấy danh sách phim và phòng
        List<MovieEntity> movies = movieService.getAllMovies();
        List<RoomDTO> rooms = roomService.getAllRooms();

        model.addAttribute("movies", movies);
        model.addAttribute("rooms", rooms);

        // Lấy danh sách tất cả suất chiếu để hiển thị trong bảng
        List<FullMovieShowtimeDTO> fullMovieShowtimes = showtimeService.getAllShowtimeDetails();
        model.addAttribute("fullMovieShowtimes", fullMovieShowtimes);

        return "showtime/form";
    }

    @PostMapping("/save")
    public String saveShowtime(@ModelAttribute ShowtimeEntity showtime, Model model) {
        showtimeService.saveShowtime(showtime);
        model.addAttribute("message", "Lưu suất chiếu thành công");
        return "redirect:/showtime/form";
    }

    @PostMapping("/reschedule")
    public String rescheduleShowtime(@RequestParam int showtimeId, @RequestParam LocalDate newShowDate,
                                     @RequestParam LocalTime newStartTime, @RequestParam LocalTime newEndTime,
                                     @RequestParam int newRoomId, @RequestParam String reason,  // Thêm tham số reason
                                     Model model, RedirectAttributes redirectAttributes) {

        // Tìm phòng chiếu mới
        RoomEntity newRoom = roomService.getRoomById(newRoomId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chiếu"));

        // Gọi service để dời lịch chiếu và truyền lý do dời lịch
        showtimeService.rescheduleShowtime(showtimeId, newShowDate, newStartTime, newEndTime, newRoom);

        redirectAttributes.addFlashAttribute("message", "Dời lịch chiếu thành công");
        return "redirect:/showtime/form";
    }



}
