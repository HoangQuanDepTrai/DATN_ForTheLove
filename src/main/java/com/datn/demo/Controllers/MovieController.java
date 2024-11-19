package com.datn.demo.Controllers;

import com.datn.demo.DTO.ShowtimeDetailsDTO;
import com.datn.demo.Entities.*;
import com.datn.demo.Services.CinemaInformationService;
import com.datn.demo.Services.MovieService;
import com.datn.demo.Services.ReviewService;
import com.datn.demo.Services.ShowtimeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/index")
public class MovieController {
	@Autowired
	private MovieService movieService;

	@Autowired
	private ShowtimeService showtimeService;

	@Autowired
	private ReviewService reviewService;

	@Autowired
	private CinemaInformationService cinemaInformationService;

	@GetMapping("/search")
	public String searchMovieByNameOrGenre(@RequestParam("keyword") String keyword, Model model) {
		try {
			// Tìm kiếm phim dựa trên tên hoặc thể loại
			List<MovieEntity> movies = movieService.findMoviesByNameOrGenre(keyword);

			// Truyền từ khóa tìm kiếm và danh sách phim vào model để hiển thị
			model.addAttribute("searchKeyword", keyword);
			model.addAttribute("movies", movies);

			if (!movies.isEmpty()) {
				model.addAttribute("resultCount", movies.size());
				return "main/user/findmovie";
			} else {
				model.addAttribute("message", "Rất tiếc! Không tìm thấy phim nào phù hợp.");
				return "main/user/no_movie_found";
			}
		} catch (Exception e) {
			model.addAttribute("message", "Đã xảy ra lỗi!");
			model.addAttribute("movies", Collections.emptyList());
			return "main/user/findmovie";
		}
	}

	@GetMapping("/api/searchMovies")
	@ResponseBody
	public List<MovieEntity> searchMoviesByNameOrGenre(@RequestParam("query") String query) {
		return movieService.findMoviesByNameOrGenre(query);
	}

	// Hiển thị danh sách phim
	@GetMapping
	public String listMovies(Model model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, HttpSession session) {
		// Giới hạn số lượng phim trên mỗi trang là 10
		// Lấy danh sách tất cả các showtime
		List<ShowtimeDetailsDTO> showTimes = showtimeService.getAllShowtime();

		// Gộp dữ liệu showtimes theo movieId
		Map<Integer, ShowtimeDetailsDTO> uniqueShowtimesMap = new HashMap<>();

		for (ShowtimeDetailsDTO showtime : showTimes) {
			if (!uniqueShowtimesMap.containsKey(showtime.getMovieId())) {
				uniqueShowtimesMap.put(showtime.getMovieId(), showtime);
			} else {
				// Nếu movieId đã tồn tại, có thể cập nhật thông tin khác nếu cần
				ShowtimeDetailsDTO existingShowtime = uniqueShowtimesMap.get(showtime.getMovieId());
				// Nếu có thêm thông tin khác muốn gộp, có thể thêm ở đây
			}
		}

// Chuyển đổi Map thành danh sách
		List<ShowtimeDetailsDTO> uniqueShowtimes = new ArrayList<>(uniqueShowtimesMap.values());

		// Tạo một tập hợp để lưu các movieId có showtime
		Set<Integer> movieIdsWithShowtimes = showTimes.stream().map(ShowtimeDetailsDTO::getMovieId)
				.collect(Collectors.toSet());

		// Lấy tất cả phim
		List<MovieEntity> allMovies = movieService.getAllMovies();

		// Lọc danh sách phim để chỉ giữ lại những phim không có showtime
		List<MovieEntity> moviesWithoutShowtimes = allMovies.stream()
				.filter(movie -> !movieIdsWithShowtimes.contains(movie.getMovieId())).collect(Collectors.toList());

		// Tính toán tổng số trang
		int totalMovies = moviesWithoutShowtimes.size();
		int totalPages = (int) Math.ceil((double) totalMovies / size);

		// Lấy danh sách phim đã phân trang
		int startItem = page * size;
		List<MovieEntity> paginatedMovies = moviesWithoutShowtimes.stream().skip(startItem).limit(size)
				.collect(Collectors.toList());

		// Cập nhật model với danh sách phim đã phân trang
		model.addAttribute("movies", paginatedMovies);
		/*
		 * model.addAttribute("showtimes", new ArrayList<>(showTimes)); // Gửi danh sách
		 * showtime
		 */
		model.addAttribute("showtimes", uniqueShowtimes);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", totalPages);

		return "main/index"; // Tên file HTML để hiển thị danh sách phim
	}
	@GetMapping("/api/movies")
	@ResponseBody
	public Map<String, Object> getMoviesWithPagination(
	    @RequestParam(defaultValue = "0") int page, 
	    @RequestParam(defaultValue = "10") int size) {

	    // Lấy tất cả showtimes
	    List<ShowtimeDetailsDTO> showTimes = showtimeService.getAllShowtime();

	    // Gộp dữ liệu showtimes theo movieId (chỉ giữ lại phim có một ca chiếu duy nhất)
	    Set<Integer> movieIdsWithShowtimes = showTimes.stream()
	            .map(ShowtimeDetailsDTO::getMovieId)
	            .collect(Collectors.toSet());

	    // Lấy tất cả phim
	    List<MovieEntity> allMovies = movieService.getAllMovies();

	    // Lọc danh sách phim để chỉ giữ lại những phim không có showtime
	    List<MovieEntity> moviesWithoutShowtimes = allMovies.stream()
	            .filter(movie -> !movieIdsWithShowtimes.contains(movie.getMovieId()))
	            .collect(Collectors.toList());

	    // Tính toán tổng số trang
	    int totalMovies = moviesWithoutShowtimes.size();
	    int totalPages = (int) Math.ceil((double) totalMovies / size);

	    // Lấy danh sách phim đã phân trang
	    int startItem = page * size;
	    List<MovieEntity> paginatedMovies = moviesWithoutShowtimes.stream()
	            .skip(startItem)
	            .limit(size)
	            .collect(Collectors.toList());

	    // Trả về dữ liệu dạng Map
	    Map<String, Object> response = new HashMap<>();
	    response.put("movies", paginatedMovies);
	    response.put("currentPage", page);
	    response.put("totalPages", totalPages);

	    return response;
	}


	// Hiển thị form thêm phim
	@GetMapping("/add")
	public String showAddMovieForm(Model model) {
		model.addAttribute("movie", new MovieEntity());
		return "add_movie"; // Tên file HTML để hiển thị form thêm phim
	}

	// Xử lý thêm phim
	@PostMapping("/add")
	public String addMovie(@ModelAttribute MovieEntity movie) {
		movieService.saveMovie(movie);
		return "redirect:/index"; // Quay lại danh sách phim sau khi thêm
	}

	// Xóa phim
	@GetMapping("/delete/{id}")
	public String deleteMovie(@PathVariable int id) {
		movieService.deleteMovie(id);
		return "redirect:/index"; // Quay lại danh sách phim sau khi xóa
	}

	@GetMapping("/details/{id}")
	public String getMovieDetails(@PathVariable("id") int movieId, Model model) {
		Optional<MovieEntity> movieOpt = movieService.getMovieById(movieId);

		if (movieOpt.isPresent()) {
			// Lấy danh sách ca chiếu theo movieId
			List<ShowtimeEntity> showtimes = showtimeService.getShowtimesByMovieId(movieId);




			// Lọc các ca chiếu có ngày chiếu lớn hơn ngày hiện tại
			List<ShowtimeEntity> filteredShowtimes = showtimes.stream()
					.filter(showtime -> showtime.getShowDate().isAfter(LocalDate.now())).collect(Collectors.toList());
			model.addAttribute("showtimes", filteredShowtimes);

			// Lấy danh sách tất cả phim để hiển thị bên phải
			List<MovieEntity> movies2 = movieService.getAllMovies();
			model.addAttribute("movie", movieOpt.get());
			model.addAttribute("movieDetails", movies2); // Thêm danh sách phim vào model

			// Đặt tên phim làm tiêu đề
			String titleMovieName = movieOpt.get().getMovieName();
			model.addAttribute("title", titleMovieName);

			// Lấy danh sách các showtime
			List<ShowtimeDetailsDTO> showTimes = showtimeService.getAllShowtime();

			// Gộp dữ liệu showtimes theo movieId
			Map<Integer, ShowtimeDetailsDTO> uniqueShowtimesMap = new HashMap<>();

			for (ShowtimeDetailsDTO showtime : showTimes) {
				if (!uniqueShowtimesMap.containsKey(showtime.getMovieId())) {
					uniqueShowtimesMap.put(showtime.getMovieId(), showtime);
				} else {
					// Nếu movieId đã tồn tại, có thể cập nhật thông tin khác nếu cần
					ShowtimeDetailsDTO existingShowtime = uniqueShowtimesMap.get(showtime.getMovieId());
					// Nếu có thêm thông tin khác muốn gộp, có thể thêm ở đây
				}
			}
			List<ShowtimeDetailsDTO> uniqueShowtimes = new ArrayList<>(uniqueShowtimesMap.values());
			model.addAttribute("showtimeDetails", uniqueShowtimes);

			List<ReviewEntity> reviews = reviewService.getReviewsByMovie(movieOpt.get());
			Collections.reverse(reviews);
			model.addAttribute("reviews", reviews);

			// Chỉ lấy các rạp chiếu có suất chiếu của phim đang xem
			List<CinemaInformationEntity> cinemaAll = cinemaInformationService.getCinemasWithShowtimesByMovie(movieId);
			model.addAttribute("cinemaAll", cinemaAll);

			return "main/user/chi_tiet_phim";
		} else {
			return "redirect:/index";
		}
	}

	

	public String formatText(String text) {
		// Thêm thẻ <br> sau mỗi dấu chấm
		return text.replaceAll("\\.\\s*", ".<br>");
	}
}
