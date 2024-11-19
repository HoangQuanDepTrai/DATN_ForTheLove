package com.datn.demo.Controllers;

import com.datn.demo.Beans.AccountBean;
import com.datn.demo.DTO.ShowtimeNotificationDTO;
import com.datn.demo.Entities.AccountEntity;
import com.datn.demo.Entities.CinemaInformationEntity;
import com.datn.demo.Entities.InvoiceEntity;
import com.datn.demo.Entities.RoleEntity;
import com.datn.demo.Entities.ShowtimeEntity;
import com.datn.demo.Entities.TicketEntity;
import com.datn.demo.Repositories.AccountRepository;
import com.datn.demo.Repositories.InvoiceRepository;
import com.datn.demo.Repositories.RoleRepository;
import com.datn.demo.Repositories.TicketRepository;
import com.datn.demo.Services.AccountService;
import com.datn.demo.Services.CinemaInformationService;
import com.datn.demo.Services.MovieService;
import com.datn.demo.Services.PasswordResetService;
import com.datn.demo.Services.ShowtimeService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.UUID;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {
	@Autowired
	private AccountService accountService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private AccountBean accountBean;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private TicketRepository ticketRepository;
	@Autowired
	private InvoiceRepository invoiceRepository;
	@Autowired
	private MovieService movieService;
	@Autowired
	private CinemaInformationService cinemaService;
	@Autowired
	private PasswordResetService passwordResetService;
	@Autowired
	private ShowtimeService showtimeService;

	@GetMapping("/login")
	public String userLogin(Model model, HttpSession session) {

		return "main/user/user-login";
	}

	@GetMapping("/new")
	public String usernew(Model model, HttpSession session) {

		return "showtime/messageShowtimeNews";
	}

	@GetMapping("/reset")
	public String userreset(Model model, HttpSession session) {

		return "main/user/reset_password";
	}

	@GetMapping("/profile")
	public String userProfile(Model model, HttpSession session) {
		AccountEntity account = (AccountEntity) session.getAttribute("acc");
		model.addAttribute("account", account);
		List<CinemaInformationEntity> cinemas = cinemaService.getAllCinemas();
		model.addAttribute("cinemas", cinemas);
		return "main/user/profile"; // Trả về trang profile
	}

	@PostMapping("/profile/update")
	public String updateProfile(@ModelAttribute AccountEntity accountEntity, RedirectAttributes redirectAttributes,
			HttpSession session, Model model) {
		AccountEntity currentUser = (AccountEntity) session.getAttribute("acc");
		if (currentUser != null) {
			// Cập nhật thông tin
			if (accountRepository.findByEmail(accountEntity.getEmail()) != null
					&& !accountEntity.getEmail().equals(currentUser.getEmail())) {
				// Nếu email đã được sử dụng bởi tài khoản khác
				redirectAttributes.addFlashAttribute("emailtrung", "Email đã được sử dụng!");
				return "redirect:/profile"; // Quay lại trang profile với thông báo lỗi
			}
			String phoneNumber = accountEntity.getPhoneNumber();
			if (!phoneNumber.matches("\\d{10}")) {
				redirectAttributes.addFlashAttribute("phoneError", "Số điện thoại phải là 10 chữ số!");
				return "redirect:/profile";
			}
			String fullName = accountEntity.getFullName();
			if (fullName == null || fullName.trim().isEmpty()) {
				redirectAttributes.addFlashAttribute("nameError", "Tên không được để trống!");
				return "redirect:/profile";
			}
			currentUser.setFullName(accountEntity.getFullName());
			currentUser.setEmail(accountEntity.getEmail());
			currentUser.setPhoneNumber(accountEntity.getPhoneNumber());

			// Lưu vào cơ sở dữ liệu
			accountRepository.save(currentUser);

			// Cập nhật lại thông tin trong session
			session.setAttribute("acc", currentUser);

			redirectAttributes.addFlashAttribute("updateSuccess", "Cập nhật thông tin thành công!");
		}

		return "redirect:/profile"; // Quay lại trang profile
	}

	@GetMapping("/logout")
	public String logout(HttpSession session, RedirectAttributes redirectAttributes) {

		session.removeAttribute("acc"); // Xóa tài khoản
		redirectAttributes.addFlashAttribute("message", "Bạn đã đăng xuất thành công!");

		return "redirect:/login?logout=true";
	}

	@PostMapping("/login")
	public String loginCheck(@RequestParam(name = "path", defaultValue = "") String path,
			@RequestParam("username") String username, @RequestParam("password") String password,
			RedirectAttributes redirectAttributes, Model model, HttpServletResponse response, HttpSession session) {

		if (username.isEmpty() || password.isEmpty()) {
			model.addAttribute("empty", "Tên đăng nhập và mật khẩu không được để trống!");
			return "main/user/user-login"; // Trả về trang đăng nhập
		}

		// Kiểm tra thông tin tài khoản từ cơ sở dữ liệu
		AccountEntity acc = accountRepository.findByUsername(username);

		// Kiểm tra tài khoản và mật khẩu
		if (acc == null || !passwordEncoder.matches(password, acc.getPassword())) {
			model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng!");
			return "main/user/user-login";
		}

		// Tạo cookie và lưu vào session
		Cookie name = new Cookie("username", username);
		response.addCookie(name);
		session.setAttribute("acc", acc);
		name.setMaxAge(7 * 24 * 60 * 60);
		// Kiểm tra lịch chiếu và gửi thông báo nếu có dời lịch
		List<InvoiceEntity> invoices = invoiceRepository.findByAccount(acc); // Tìm tất cả hóa đơn của người dùng
		List<ShowtimeNotificationDTO> showtimeNotifications = new ArrayList<>();
		LocalDate currentDate = LocalDate.now(); // Lấy ngày hiện tại

		for (InvoiceEntity invoice : invoices) {
		    if (invoice.getShowtime() != null && invoice.getShowtime().getShowDate() != null) {
		        LocalDate originalDate = invoice.getShowtime().getOriginalShowDate(); // Ngày chiếu gốc
		        LocalDate currentDateInInvoice = invoice.getShowtime().getShowDate(); // Ngày chiếu hiện tại (đã dời)
		        
		        LocalTime originalStartTime = invoice.getShowtime().getOriginalStartTime(); // Giờ bắt đầu gốc
		        LocalTime originalEndTime = invoice.getShowtime().getOriginalEndTime(); // Giờ kết thúc gốc
		        LocalTime startTime = invoice.getShowtime().getStartTime(); // Giờ bắt đầu hiện tại
		        LocalTime endTime = invoice.getShowtime().getEndTime(); // Giờ kết thúc hiện tại

		        // Kiểm tra nếu ngày chiếu gốc và ngày chiếu hiện tại khác nhau
		        if (originalDate != null && !originalDate.isEqual(currentDateInInvoice)) {
		            String movieName = (invoice.getShowtime().getMovie() != null)
		                    ? invoice.getShowtime().getMovie().getMovieName()
		                    : "Tên phim không xác định";

		            // Kiểm tra nếu ngày chiếu mới chưa tới, sẽ hiển thị thông báo
		            if (currentDate.isBefore(currentDateInInvoice)) {
		                // Tạo đối tượng ShowtimeNotificationDTO
		                ShowtimeNotificationDTO notification = new ShowtimeNotificationDTO(
		                    movieName, 
		                    originalDate, 
		                    originalStartTime, 
		                    originalEndTime, 
		                    currentDateInInvoice, 
		                    startTime, 
		                    endTime
		                );

		                // Thêm đối tượng vào danh sách thông báo
		                showtimeNotifications.add(notification);
		            }
		        }
		    }
		}

		// Thêm danh sách đối tượng DTO vào model
		if (!showtimeNotifications.isEmpty()) {
		    model.addAttribute("showtimeNotifications", showtimeNotifications);
		}



		// Kiểm tra vai trò của người dùng
		String roleName = acc.getRole().getRoleName().toLowerCase();
		if (roleName.equals("admin")) {
			session.setAttribute("acc", acc); // Lưu thông tin admin vào session
			return "main/user/admin"; // Redirect cho admin
		} else if (roleName.equals("user")) {
		    redirectAttributes.addFlashAttribute("showtimeNotifications", showtimeNotifications);
			return "redirect:/index"; // Redirect cho user với ngôn ngữ đã lưu
		}

		model.addAttribute("error", "Tài khoản không có quyền truy cập!");

		return "main/user/user-login";
	}

	@GetMapping("/register")
	public String userRegister(Model model) {
		model.addAttribute("account", new AccountBean());
		return "main/user/user-dk";
	}

	@GetMapping("/oauth2/success")
	public String oauth2Success(Model model, OAuth2AuthenticationToken authentication, HttpSession session,
	        RedirectAttributes redirectAttributes) {
	    // Lấy thông tin người dùng từ OAuth2AuthenticationToken
	    OAuth2User oauth2User = authentication.getPrincipal();
	    String email = oauth2User.getAttribute("email"); // Lấy email
	    String username = email.split("@")[0]; // Lấy phần trước dấu @
	    String password = "";
	    String fullName = oauth2User.getAttribute("name"); // Lấy tên đầy đủ
	    String phoneNumber = ""; // Đặt giá trị là "google" hoặc "facebook"

	    // Kiểm tra xem tài khoản đã tồn tại trong cơ sở dữ liệu chưa
	    AccountEntity existingAccount = accountRepository.findByEmail(email);
	    AccountEntity newAccount = null;
	    
	    // Nếu tài khoản chưa tồn tại, tạo tài khoản mới
	    if (existingAccount == null) {
	        RoleEntity userRole = roleRepository.findByRoleName("user");
	        if (userRole == null) {
	            return "redirect:/login?error=Vai%20trò%20mặc%20định%20không%20tồn%20tại";
	        }

	        // Tạo mới tài khoản
	        newAccount = new AccountEntity();
	        newAccount.setUsername(username);
	        newAccount.setPassword(password);
	        newAccount.setEmail(email);
	        newAccount.setFullName(fullName);
	        newAccount.setPhoneNumber(phoneNumber); // "google" hoặc "facebook"
	        newAccount.setRole(userRole);

	        accountRepository.save(newAccount);
	        existingAccount = newAccount; // Gán tài khoản mới vừa tạo
	    }
		List<InvoiceEntity> invoices = invoiceRepository.findByAccount(existingAccount); // Tìm tất cả hóa đơn của người dùng
		List<ShowtimeNotificationDTO> showtimeNotifications = new ArrayList<>();
		LocalDate currentDate = LocalDate.now(); // Lấy ngày hiện tại

		for (InvoiceEntity invoice : invoices) {
		    if (invoice.getShowtime() != null && invoice.getShowtime().getShowDate() != null) {
		        LocalDate originalDate = invoice.getShowtime().getOriginalShowDate(); // Ngày chiếu gốc
		        LocalDate currentDateInInvoice = invoice.getShowtime().getShowDate(); // Ngày chiếu hiện tại (đã dời)
		        
		        LocalTime originalStartTime = invoice.getShowtime().getOriginalStartTime(); // Giờ bắt đầu gốc
		        LocalTime originalEndTime = invoice.getShowtime().getOriginalEndTime(); // Giờ kết thúc gốc
		        LocalTime startTime = invoice.getShowtime().getStartTime(); // Giờ bắt đầu hiện tại
		        LocalTime endTime = invoice.getShowtime().getEndTime(); // Giờ kết thúc hiện tại

		        // Kiểm tra nếu ngày chiếu gốc và ngày chiếu hiện tại khác nhau
		        if (originalDate != null && !originalDate.isEqual(currentDateInInvoice)) {
		            String movieName = (invoice.getShowtime().getMovie() != null)
		                    ? invoice.getShowtime().getMovie().getMovieName()
		                    : "Tên phim không xác định";

		            // Kiểm tra nếu ngày chiếu mới chưa tới, sẽ hiển thị thông báo
		            if (currentDate.isBefore(currentDateInInvoice)) {
		                // Tạo đối tượng ShowtimeNotificationDTO
		                ShowtimeNotificationDTO notification = new ShowtimeNotificationDTO(
		                    movieName, 
		                    originalDate, 
		                    originalStartTime, 
		                    originalEndTime, 
		                    currentDateInInvoice, 
		                    startTime, 
		                    endTime
		                );

		                // Thêm đối tượng vào danh sách thông báo
		                showtimeNotifications.add(notification);
		            }
		        }
		    }
		}

		// Thêm danh sách đối tượng DTO vào model
		if (!showtimeNotifications.isEmpty()) {
		    model.addAttribute("showtimeNotifications", showtimeNotifications);
		}
	
	    session.setAttribute("acc", existingAccount); // Đảm bảo gán tài khoản vào session
	    redirectAttributes.addFlashAttribute("showtimeNotifications", showtimeNotifications);

	    return "redirect:/index"; // Redirect cho user với ngôn ngữ đã lưu
	}


	@PostMapping("/register")
	public String saveRegister(@ModelAttribute("account") AccountBean accountBean, BindingResult result,
			@RequestParam("username") String username, @RequestParam("password") String password,
			@RequestParam("confirm_password") String confirmPassword, @RequestParam("fullName") String fullName,
			@RequestParam("phoneNumber") String phoneNumber, @RequestParam("email") String email, Model model,
			RedirectAttributes redirectAttributes) {

		// Kiểm tra nếu bất kỳ trường nào bị bỏ trống
		if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || fullName.isEmpty()
				|| phoneNumber.isEmpty() || email.isEmpty()) {
			model.addAttribute("empty", "Tất cả các trường phải được điền đầy đủ!");
			return "main/user/user-dk";
		}

		// Kiểm tra độ dài username
		if (username.length() < 6 || username.length() > 20) {
			model.addAttribute("usernameError", "Tên đăng nhập phải có từ 6 đến 20 ký tự!");
			return "main/user/user-dk";
		}

		// Kiểm tra độ dài mật khẩu
		if (password.length() < 6) {
			model.addAttribute("passwordError", "Mật khẩu phải có ít nhất 6 ký tự!");
			return "main/user/user-dk";
		}

		// Kiểm tra mật khẩu xác nhận
		if (!confirmPassword.equals(password)) {
			model.addAttribute("confirmError", "Mật khẩu xác nhận phải giống với mật khẩu!");
			return "main/user/user-dk";
		}

		// Kiểm tra định dạng email
		String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
		if (!email.matches(emailRegex)) {
			model.addAttribute("emailError", "Email không đúng định dạng!");
			return "main/user/user-dk";
		}

		// Kiểm tra định dạng số điện thoại
		String phoneRegex = "^\\d{10}$";
		if (!phoneNumber.matches(phoneRegex)) {
			model.addAttribute("phoneError", "Số điện thoại phải bao gồm 10 chữ số!");
			return "main/user/user-dk";
		}

		// Kiểm tra tên đăng nhập có trùng không
		if (accountRepository.findByUsername(username) != null) {
			model.addAttribute("usernameError", "Tên đăng nhập đã tồn tại!");
			return "main/user/user-dk";
		}

		// Kiểm tra email có trùng không
		if (accountRepository.findByEmail(email) != null) {
			model.addAttribute("emailError", "Email đã được sử dụng!");
			return "main/user/user-dk";
		}

		// Tạo đối tượng AccountEntity và mã hóa mật khẩu
		AccountEntity accountEntity = new AccountEntity();
		accountEntity.setUsername(username);
		accountEntity.setPassword(passwordEncoder.encode(password));
		accountEntity.setFullName(fullName);
		accountEntity.setPhoneNumber(phoneNumber);
		accountEntity.setEmail(email);
		accountEntity.setResetRequestCount(0); // Thiết lập mặc định

		// Gán vai trò mặc định là 'user'
		RoleEntity userRole = roleRepository.findByRoleName("user");
		if (userRole == null) {
			model.addAttribute("roleError", "Vai trò mặc định không tồn tại!");
			return "main/user/user-dk";
		}
		accountEntity.setRole(userRole);

		accountRepository.save(accountEntity);
		redirectAttributes.addFlashAttribute("registerSuccess", "Đăng ký thành công! Vui lòng đăng nhập.");
		return "redirect:/login"; // Hoặc redirect đến trang khác
	}

	@GetMapping("/forgot-password")
	public String userForgotPassword(Model model) {
		return "main/user/user-qmk";
	}

	@PostMapping("/forgot-password")
	public String processForgotPassword(@RequestParam("email") String email, Model model,
			RedirectAttributes redirectAttributes) {
		if (email.isEmpty()) {
			model.addAttribute("emailempty", "Không được bỏ trống email!");
			return "main/user/user-qmk"; // Trả về trang quên mật khẩu
		}

		// Gọi phương thức generateResetPasswordToken để nhận phản hồi từ service
		String response = passwordResetService.generateResetPasswordToken(email);

		// Kiểm tra phản hồi từ service
		if (response != null
				&& response.equals("Bạn đã yêu cầu đổi mật khẩu 3 lần trong ngày. Vui lòng quay lại vào ngày mai.")) {
			redirectAttributes.addFlashAttribute("kochonhap", response); // Thông báo vượt quá giới hạn
			return "redirect:/forgot-password"; // Dừng tại đây nếu vượt quá giới hạn
		}

		// Nếu không vượt quá giới hạn và response không phải null thì có thể gửi email
		if (response != null
				&& !response.equals("Bạn đã yêu cầu đổi mật khẩu 3 lần trong ngày. Vui lòng quay lại vào ngày mai.")) {
			redirectAttributes.addFlashAttribute("message",
					"Link đặt lại mật khẩu đã được gửi đến email của bạn. Vui lòng kiểm tra hộp thư.");
		} else {
			redirectAttributes.addFlashAttribute("error", "Không tìm thấy tài khoản với email đã cung cấp.");
		}

		// Lấy số lần còn lại chỉ khi chưa vượt quá giới hạn
		AccountEntity account = accountService.findAccountByEmail(email);
		if (account != null && account.getResetRequestCount() < 3) {
			int remainingAttempts = 3 - account.getResetRequestCount();
			redirectAttributes.addFlashAttribute("remainingAttempts", remainingAttempts);
		}

		return "redirect:/forgot-password"; // Redirect về trang quên mật khẩu
	}

	@GetMapping("/reset_password")
	public String showResetPasswordPage(@RequestParam("token") String token, Model model,
			@RequestParam(value = "lang", defaultValue = "vi") String lang) {
		if (passwordResetService.findByToken(token).isPresent()) {
			model.addAttribute("token", token);
			model.addAttribute("currentLang", lang); // Thêm ngôn ngữ vào model
			return "main/user/reset_password"; // Trang nhập mật khẩu mới
		} else {
			model.addAttribute("error", lang.equals("en") ? "The password reset link is invalid or has expired."
					: "Link đặt lại mật khẩu không hợp lệ hoặc đã hết hạn.");

			return "main/user/user-qmk"; // Quay lại trang quên mật khẩu
		}
	}

	@PostMapping("/reset_password")
	public String processResetPassword(@RequestParam("token") String token,
			@RequestParam("newPassword") String newPassword, @RequestParam("confirmPassword") String confirmPassword,
			RedirectAttributes redirectAttributes) {

		// Kiểm tra xem mật khẩu mới và mật khẩu xác nhận có khớp hay không
		if (newPassword == null || newPassword.isEmpty() || confirmPassword == null || confirmPassword.isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "Mật khẩu mới và xác nhận mật khẩu không được để trống.");
			return "redirect:/reset_password?token=" + token; // Chuyển hướng về trang nhập mật khẩu mới với token
		}
		if (newPassword.length() < 6) {
			redirectAttributes.addFlashAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự.");
			return "redirect:/reset_password?token=" + token;
		}
		if (!newPassword.equals(confirmPassword)) {
			redirectAttributes.addFlashAttribute("error", "Mật khẩu mới và mật khẩu xác nhận không khớp.");
			return "redirect:/reset_password?token=" + token; // Chuyển hướng về trang nhập mật khẩu mới với token
		}

		boolean success = passwordResetService.resetPassword(token, newPassword);
		if (success) {
			redirectAttributes.addFlashAttribute("message", "Đổi mật khẩu thành công."); // Thêm thông báo thành công
			return "redirect:/forgot-password"; // Chuyển hướng về trang quên mật khẩu
		} else {
			redirectAttributes.addFlashAttribute("error", "Link đặt lại mật khẩu không hợp lệ hoặc đã hết hạn.");
			return "redirect:/reset_password?token=" + token; // Chuyển hướng về trang nhập mật khẩu mới với token
		}
	}

}
