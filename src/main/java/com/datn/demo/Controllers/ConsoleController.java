package com.datn.demo.Controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.datn.demo.DTO.ShowtimeDTO;
import com.datn.demo.DTO.ShowtimeDetailsDTO;
import com.datn.demo.Entities.AccountEntity;
import com.datn.demo.Entities.CinemaInformationEntity;
import com.datn.demo.Entities.MovieEntity;
import com.datn.demo.Entities.ShowtimeEntity;
import com.datn.demo.Services.CinemaInformationService;
import com.datn.demo.Services.MovieService;
import com.datn.demo.Services.RoomService;
import com.datn.demo.Services.ShowtimeService;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class ConsoleController {
	@Autowired
	private MovieService movieService;

	@Autowired
	private ShowtimeService showtimeService;
	@Autowired
	private RoomService roomService;
	@Autowired
	private CinemaInformationService cinemaService;

	@GetMapping("/no-showtimes")
	public String showMoviesWithoutShowtimes(Model model) {
		List<MovieEntity> moviesWithoutShowtimes = movieService.findMoviesWithoutShowtime();
		model.addAttribute("moviesWithoutShowtimes", moviesWithoutShowtimes);
		return "main/user/movies_no_showtimes"; // Tên của file HTML để hiển thị
	}

	@RequestMapping("/add-showtime/{movieId}")
	public String showAddShowtimeForm(@PathVariable("movieId") int movieId, Model model) {
		// Lấy thông tin của phim bằng movieId
		Optional<MovieEntity> movieOpt = movieService.getMovieById(movieId);

		if (movieOpt.isPresent()) {
			model.addAttribute("movie", movieOpt.get());
			model.addAttribute("showtime", new ShowtimeEntity());
			model.addAttribute("rooms", roomService.getAllRooms());
			model.addAttribute("cinemas", cinemaService.getAllCinemas());
			return "main/user/add_showtime_form";
		} else {
			model.addAttribute("error", "Không tìm thấy phim với ID: " + movieId);
			return "main/user/error";
		}
	}


	@PostMapping("/save-showtime")
	public String saveShowtime(@ModelAttribute("showtime") ShowtimeEntity showtime,
							   @RequestParam("movieId") int movieId, RedirectAttributes redirectAttributes) {
		// Lấy phim từ movieId
		Optional<MovieEntity> movieOpt = movieService.getMovieById(movieId);

		if (movieOpt.isPresent()) {
			// Gán phim cho showtime
			showtime.setMovie(movieOpt.get());  // Gán đối tượng MovieEntity vào showtime
			showtimeService.saveShowtime(showtime); // Lưu showtime vào cơ sở dữ liệu
			redirectAttributes.addFlashAttribute("message", "Suất chiếu đã được thêm thành công!");
			return "redirect:/no-showtimes"; // Trang danh sách suất chiếu
		} else {
			redirectAttributes.addFlashAttribute("error", "Không thể lưu suất chiếu vì phim không tồn tại.");
			return "redirect:/no-showtimes"; // Trở lại trang danh sách suất chiếu với thông báo lỗi
		}
	}




	@GetMapping("/schedule")
	public String getShowtimeSchedule(Model model) {
		Map<LocalDate, List<ShowtimeEntity>> showtimes = showtimeService.getShowtimesForNextSevenDays();
		Map<Date, List<ShowtimeEntity>> formattedShowtimes = new LinkedHashMap<>();

		for (Map.Entry<LocalDate, List<ShowtimeEntity>> entry : showtimes.entrySet()) {
			Date date = java.sql.Date.valueOf(entry.getKey());
			formattedShowtimes.put(date, entry.getValue());
		}

		model.addAttribute("showtimes", formattedShowtimes);
		return "main/user/showtime_schedule"; // Đường dẫn đến view để hiển thị lịch chiếu
	}

	@GetMapping("/cinemas/{date}")
	public ResponseEntity<?> getCinemasByShowDate(@PathVariable String date) {
		LocalDate localDate = LocalDate.parse(date);
		List<ShowtimeEntity> showtimes = showtimeService.findShowtimesByDate(localDate);

		// Lọc các rạp chiếu duy nhất (loại bỏ bản sao)
		Set<String> uniqueCinemas = new HashSet<>();
		showtimes.forEach(showtime -> uniqueCinemas.add(showtime.getCinemaInformation().getCinemaName()));

		// Nếu cần thiết, tạo lại danh sách rạp chiếu với các rạp duy nhất
		List<ShowtimeDTO> showtimeDTOs = showtimes.stream()
				.map(showtime -> new ShowtimeDTO(
						showtime.getShowtimeId(),
						showtime.getMovie().getMovieId(),
						showtime.getMovie().getMovieName(),
						showtime.getMovie().getImage(),
						showtime.getStartTime(),
						showtime.getEndTime(),
						showtime.getRoom().getRoomName(),
						showtime.getMovie().getGenre(),
						showtime.getMovie().getAgeRestriction(),
						showtime.getMovie().getDuration(),
						showtime.getCinemaInformation().getCinemaId(),
						showtime.getCinemaInformation().getCinemaName()
				))
				.collect(Collectors.toList());

		return ResponseEntity.ok(showtimeDTOs);
	}


	@GetMapping("/movies/{cinemaId}/{date}")
	public ResponseEntity<List<ShowtimeDTO>> getMoviesByCinemaAndDate(@PathVariable int cinemaId,
																	  @PathVariable String date) {
		LocalDate localDate = LocalDate.parse(date); // Chuyển đổi từ String thành LocalDate
		List<ShowtimeEntity> showtimes = showtimeService.findShowtimesByCinemaAndDate(cinemaId, localDate); // Lấy danh
		// sách ca
		// chiếu cho
		// rạp và
		// ngày

		// Chuyển đổi thành ShowtimeDTO
		List<ShowtimeDTO> showtimeDTOs = showtimes.stream()
				.map(showtime -> new ShowtimeDTO(showtime.getShowtimeId(), showtime.getMovie().getMovieId(),
						showtime.getMovie().getMovieName(), showtime.getMovie().getImage(), showtime.getStartTime(),
						showtime.getEndTime(), showtime.getRoom().getRoomName(), showtime.getMovie().getGenre(),
						showtime.getMovie().getAgeRestriction(), showtime.getMovie().getDuration(),
						showtime.getCinemaInformation().getCinemaId(), showtime.getCinemaInformation().getCinemaName()))
				.collect(Collectors.toList());

		return ResponseEntity.ok(showtimeDTOs);
	}

	@GetMapping("/movies-with-showtimes")
	public String listMoviesWithShowtimes(Model model, HttpSession session) {
		// Lấy danh sách tất cả các showtime
		List<ShowtimeDetailsDTO> showTimes = showtimeService.getAllShowtime();

		// Tạo một tập hợp để lưu các movieId có showtime
		Set<Integer> movieIdsWithShowtimes = new HashSet<>();
		for (ShowtimeDetailsDTO showtime : showTimes) {
			movieIdsWithShowtimes.add(showtime.getMovieId());
		}

		// Lấy tất cả phim
		List<MovieEntity> allMovies = movieService.getAllMovies();

		// Lọc danh sách phim để chỉ giữ lại những phim có showtime
		List<MovieEntity> moviesWithShowtimes = allMovies.stream().filter(movieIdsWithShowtimes::contains)
				.collect(Collectors.toList());

		// Cập nhật model với danh sách phim có ca chiếu
		model.addAttribute("movies", moviesWithShowtimes);
		model.addAttribute("showtimes", new ArrayList<>(showTimes)); // Gửi danh sách showtime

		return "main/user/movie_now_showing"; // Tên file HTML để hiển thị danh sách phim có ca chiếu
	}

	@GetMapping("/movies-without-showtimes")
	public String listMoviesWithoutShowtimes(Model model, HttpSession session) {
		// Lấy danh sách tất cả các showtime
		List<ShowtimeDetailsDTO> showTimes = showtimeService.getAllShowtime();

		// Tạo một tập hợp để lưu các movieId có showtime
		Set<Integer> movieIdsWithShowtimes = new HashSet<>();
		for (ShowtimeDetailsDTO showtime : showTimes) {
			movieIdsWithShowtimes.add(showtime.getMovieId());
		}

		// Lấy tất cả phim
		List<MovieEntity> allMovies = movieService.getAllMovies();

		// Lọc danh sách phim để chỉ giữ lại những phim không có showtime
		List<MovieEntity> moviesWithoutShowtimes = allMovies.stream()
				.filter(movie -> !movieIdsWithShowtimes.contains(movie.getMovieId())).collect(Collectors.toList());

		// Cập nhật model với danh sách phim không có ca chiếu
		model.addAttribute("movies", moviesWithoutShowtimes);
		model.addAttribute("showtimes", new ArrayList<>(showTimes)); // Gửi danh sách showtime

		return "main/user/movie_up_coming"; // Tên file HTML để hiển thị danh sách phim không có ca chiếu
	}

}
