package com.datn.demo.Services;

import com.datn.demo.DTO.FullMovieShowtimeDTO;
import com.datn.demo.DTO.ShowtimeDetailsDTO;
import com.datn.demo.Entities.AccountEntity;
import com.datn.demo.Entities.InvoiceEntity;
import com.datn.demo.Entities.RoomEntity;
import com.datn.demo.Entities.ShowtimeEntity;
import com.datn.demo.Entities.TicketEntity;
import com.datn.demo.Repositories.InvoiceRepository;
import com.datn.demo.Repositories.ShowtimeRepository;
import com.datn.demo.Repositories.TicketRepository;
import com.datn.demo.Utility.Email;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class ShowtimeService {

	@Autowired
	private ShowtimeRepository showtimeRepository;
	@Autowired
	private TicketRepository ticketRepository;
	@Autowired
	private Email emailService; // Để gửi email
	@Autowired
	private InvoiceRepository invoiceRepository;
	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Lấy tất cả các ca chiếu với thông tin chi tiết.
	 *
	 * @return Danh sách ShowtimeDetailsDTO
	 */
	public List<ShowtimeDetailsDTO> getAllShowtime() {
		return showtimeRepository.findAllShowtimeDetails();
	}

	public List<ShowtimeEntity> getAllShowtimes() {
		return showtimeRepository.findAll(); // Trả về tất cả các ca chiếu
	}

	public List<ShowtimeEntity> findShowtimesByCinemaAndDate(int cinemaId, LocalDate date) {
		return showtimeRepository.findShowtimesByCinemaAndDate(cinemaId, date);
	}

	/**
	 * Lấy các ca chiếu theo movieId, sắp xếp theo ngày chiếu.
	 *
	 * @param movieId ID của phim
	 * @return Danh sách ShowtimeEntity
	 */
	public List<ShowtimeEntity> getShowtimesByMovieId(Integer movieId) {
		return showtimeRepository.findByMovieMovieIdOrderByShowDate(movieId);
	}

	/**
	 * Lấy thông tin ca chiếu theo showtimeId.
	 *
	 * @param showtimeId ID của ca chiếu
	 * @return Optional chứa ShowtimeEntity nếu tìm thấy
	 */
	public Optional<ShowtimeEntity> getShowtimeById(int showtimeId) {
		return showtimeRepository.findById(showtimeId);
	}

	public void saveShowtime(ShowtimeEntity showtime) {
		showtimeRepository.save(showtime);
	}

	public void deleteShowtime(int id) {
		showtimeRepository.deleteById(id);
	}

	public void rescheduleShowtime(int showtimeId, LocalDate newShowDate, LocalTime newStartTime, LocalTime newEndTime,
			RoomEntity newRoom) {
		Optional<ShowtimeEntity> optionalShowtime = showtimeRepository.findById(showtimeId);

		if (optionalShowtime.isPresent()) {
			ShowtimeEntity showtime = optionalShowtime.get();

			if (showtime.getOriginalShowDate() == null) {
				showtime.setOriginalShowDate(showtime.getShowDate());
				showtime.setOriginalStartTime(showtime.getStartTime());
				showtime.setOriginalEndTime(showtime.getEndTime());
				showtime.setOriginalRoom(showtime.getRoom());
			}

			showtime.setShowDate(newShowDate);
			showtime.setStartTime(newStartTime);
			showtime.setEndTime(newEndTime);
			showtime.setRoom(newRoom);

			showtimeRepository.save(showtime);

			notifyCustomersForOldShowtime(showtime);
		} else {
			throw new RuntimeException("Không tìm thấy suất chiếu với ID: " + showtimeId);
		}
	}

	public void notifyCustomersForOldShowtime(ShowtimeEntity showtime) {
	    // Truyền vào tham số LocalDate và LocalTime trực tiếp
	    LocalDate originalShowDate = showtime.getOriginalShowDate();
	    LocalTime originalStartTime = showtime.getOriginalStartTime();
	    LocalTime originalEndTime = showtime.getOriginalEndTime();
	    
	    // Kiểm tra nếu originalRoom là null trước khi truy cập getRoomId()
	    Integer originalRoomId = null;
	    if (showtime.getOriginalRoom() != null) {
	        originalRoomId = showtime.getOriginalRoom().getRoomId();
	    }

	    // Tiếp tục nếu originalRoomId không phải là null
	    if (originalRoomId != null) {
	        List<InvoiceEntity> invoices = invoiceRepository.findByOriginalShowtimeDetails(
	                originalShowDate, originalStartTime, originalEndTime, originalRoomId);

	        for (InvoiceEntity invoice : invoices) {
	            String emailContent = buildShowtimeChangeEmailContent(showtime);
	            emailService.sendEmail(invoice.getAccount().getEmail(), "Thông báo dời lịch chiếu", emailContent);
	        }
	    }
	}



	private String buildShowtimeChangeEmailContent(ShowtimeEntity showtime) {
		StringBuilder htmlContent = new StringBuilder();
		htmlContent.append(
				"<div style='font-family: sans-serif; min-width: 100%; min-height: 100vh; background-color: rgba(250, 90, 90, 0.8); padding: 2rem;'>")
				.append("<div style='max-width: 600px; margin: auto;'>")
				.append("<div style='background-color: white; padding: 2rem; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);'>")
				.append("<div style='text-align: center; border-bottom: 1px solid #eaeaea; margin-bottom: 2rem;'>")
				.append("<img src='https://drive.google.com/uc?id=1nFnGPdWnlFK1XRuZtB-8hJWFEVMp0TSF' alt='Thông báo dời lịch chiếu' style='width: 80px; height: 80px;margin-right: 550px;' >")
				.append("<h1 style='font-size: 2rem;'>Thông Báo Dời Lịch Chiếu</h1>").append("</div>")
				.append("<div style='padding: 1rem 0; border-bottom: 1px solid #eaeaea; margin-bottom: 2rem;'>")
				.append("<p>Xin chào quý khách,</p>").append("<p>Chúng tôi xin thông báo rằng suất chiếu phim <b>")
				.append(showtime.getMovie().getMovieName()) // Tên phim
				.append("</b> vào ngày <b>").append(showtime.getOriginalShowDate())
				.append("</b> đã được dời sang ngày <b>").append(showtime.getShowDate()).append("</b>.</p>")
				.append("<p>Thời gian chiếu mới: <b>").append(showtime.getStartTime()).append(" - ")
				.append(showtime.getEndTime()).append("</b></p>") // Thời gian chiếu
				.append("<p>Chúng tôi xin lỗi vì sự bất tiện này và cảm ơn quý khách đã thông cảm.</p>")
				.append("<p>Thông tin ghế ngồi đã đặt:</p>").append("<ul>");

		// Truy vấn vé từ ticketRepository
		List<TicketEntity> tickets = ticketRepository.findTicketsByShowtimeId(showtime.getShowtimeId());

		// Thêm thông tin ghế đã đặt
		for (TicketEntity ticket : tickets) {
			htmlContent.append("<li>Ghế: <b>").append(ticket.getSeat().getSeatName()).append("</b>, Phòng: <b>")
					.append(ticket.getSeat().getRoom().getRoomName()).append("</b></li>");
		}

		htmlContent.append("</ul>").append(
				"<p style='margin-top: 1rem;text-align: center;'>Nếu có bất kỳ câu hỏi nào, vui lòng liên hệ chúng tôi.<br>Đội ngũ hỗ trợ</p>")
				.append("</div>").append("<div style='text-align: center; color: #555;'>")
				.append("<h3>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!</h3>").append("</div>").append("</div>")
				.append("</div>");
		return htmlContent.toString();
	}

	public Map<LocalDate, List<ShowtimeEntity>> getShowtimesForNextSevenDays() {
		LocalDate today = LocalDate.now();
		LocalDate startDate = today.plusDays(1);
		LocalDate endDate = today.plusDays(7);

		// Tạo TreeMap để sắp xếp lịch chiếu theo thứ tự tăng dần của ngày
		Map<LocalDate, List<ShowtimeEntity>> showtimesMap = new TreeMap<>();

		// Lấy tất cả ca chiếu trong khoảng từ ngày mai đến 7 ngày tới
		List<ShowtimeEntity> allShowtimes = showtimeRepository.findAllByShowDateBetween(startDate, endDate);

		// Gom nhóm các ca chiếu theo ngày
		Map<LocalDate, List<ShowtimeEntity>> groupedShowtimes = allShowtimes.stream()
				.collect(Collectors.groupingBy(ShowtimeEntity::getShowDate));

		// Thêm tất cả các ngày vào map, kể cả ngày không có ca chiếu
		for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
			// Nếu ngày có ca chiếu thì thêm vào từ groupedShowtimes, ngược lại thêm một
			// danh sách rỗng
			showtimesMap.put(date, groupedShowtimes.getOrDefault(date, new ArrayList<>()));
		}

		return showtimesMap;
	}

	public List<ShowtimeEntity> getShowtimesByDate(LocalDate date) {
		return showtimeRepository.findByShowDate(date);
	}

	public List<ShowtimeEntity> findShowtimesByDate(LocalDate date) {
		return showtimeRepository.findByShowDate(date);
	}

	public List<FullMovieShowtimeDTO> getAllShowtimeDetails() {
		return showtimeRepository.findAllShowtimeCineMovieRoom();
	}



}
