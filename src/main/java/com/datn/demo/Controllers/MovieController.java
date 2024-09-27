package com.datn.demo.Controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.datn.demo.Entities.MovieEntity;
import com.datn.demo.Services.MovieService;

@Controller
@RequestMapping("/userIndex")
public class MovieController {
	@Autowired
    private MovieService movieService;

    // Hiển thị danh sách phim Hoàng Quân
    @GetMapping
    public String listMovies(Model model) {
        List<MovieEntity> movie = movieService.getAllMovies();
        model.addAttribute("movies", movie);
        return "slider_1-main/index"; // Tên file HTML để hiển thị danh sách phim
//        Khánh dú is soo cool
        
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
        return "redirect:/userIndex"; // Quay lại danh sách phim sau khi thêm
    }

    // Xóa phim
    @GetMapping("/delete/{id}")
    public String deleteMovie(@PathVariable String id) {
        movieService.deleteMovie(id);
        return "redirect:/userIndex"; // Quay lại danh sách phim sau khi xóa
    }
    

      
    @GetMapping("/details/{id}")
    public String getMovieDetails(@PathVariable("id") String movieId, Model model) {
        Optional<MovieEntity> movieOpt = movieService.getMovieById(movieId);
        if (movieOpt.isPresent()) {
        	 List<MovieEntity> movies2 = movieService.getAllMovies();  // Lấy danh sách tất cả phim để hiển thị bên phải
        	 System.out.println(movies2);
            model.addAttribute("movie", movieOpt.get());     
            model.addAttribute("movieDetails",movies2); //thêm danh sách phim mới vào model
            
            // Lấy tên phim từ đối tượng movieOpt để đặt làm tiêu đề
            String titleMovieName = movieOpt.get().getMovieName(); // Lấy tên phim đã chọn
            model.addAttribute("title", titleMovieName); // Thêm tên phim vào model
            
            return "slider_1-main/chi_tiet_phim"; // Tên của file HTML hiển thị chi tiết phim
        } else {
            return "slider_1-main/index"; // Tên của file HTML khi không tìm thấy phim
        }
    }
    
    
    public String formatText(String text) {
        // Thêm thẻ <br> sau mỗi dấu chấm
        return text.replaceAll("\\.\\s*", ".<br>");
    }

   
    
    

}
