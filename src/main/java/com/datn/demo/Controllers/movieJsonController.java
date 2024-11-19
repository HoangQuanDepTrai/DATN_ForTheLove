package com.datn.demo.Controllers;

import com.datn.demo.DTO.ShowtimeDetailsDTO;
import com.datn.demo.Entities.MovieEntity;
import com.datn.demo.Services.MovieService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
public class movieJsonController {

    @Autowired
    private MovieService movieService;

    // Hiển thị danh sách phim
    @GetMapping("/testJson")
    public List<MovieEntity> listMovies() {
        return movieService.getAllMovies();
    }
}
