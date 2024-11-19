package com.datn.demo.Services;
import com.datn.demo.Entities.AccountEntity;
import com.datn.demo.Repositories.AccountRepository;
import com.datn.demo.Utility.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {
	 @Autowired
	    private AccountRepository accountRepository;

	    @Autowired
	    private Email emailService;

	    @Autowired
	    private PasswordEncoder passwordEncoder;

	    @Autowired
	    private JavaMailSender mailSender; // Đảm bảo rằng mailSender được khởi tạo

	    // Tạo token reset mật khẩu
	    public String generateResetPasswordToken(String email) {
	        AccountEntity account = accountRepository.findByEmail(email);
	        if (account != null) {
	            LocalDateTime now = LocalDateTime.now();

	            // Kiểm tra và reset số lần yêu cầu nếu qua ngày mới
	            if (account.getLastResetRequestTime() != null && account.getLastResetRequestTime().toLocalDate().isBefore(now.toLocalDate())) {
	                account.setResetRequestCount(0); // Reset số lần yêu cầu về 0 nếu qua ngày mới
	            }

	            // Kiểm tra số lần yêu cầu đổi mật khẩu trong ngày
	            if (account.getResetRequestCount() >= 3) {
	                return "Bạn đã yêu cầu đổi mật khẩu 3 lần trong ngày. Vui lòng quay lại vào ngày mai.";
	            }

	            // Kiểm tra thời gian yêu cầu đổi mật khẩu gần nhất
	            if (account.getLastResetRequestTime() != null &&
	                account.getLastResetRequestTime().plusMinutes(5).isAfter(now)) {
	                return "Bạn phải đợi thêm 5 phút trước khi yêu cầu đổi mật khẩu.";
	            }

	            // Tạo token mới
	            String token = UUID.randomUUID().toString();
	            account.setToken(token);
	            account.setExpiration_time(now.plusMinutes(2)); // Thời gian hết hạn 2 phút
	            account.setLastResetRequestTime(now); // Cập nhật thời gian yêu cầu
	            account.setResetRequestCount(account.getResetRequestCount() + 1); // Tăng số lần yêu cầu
	            accountRepository.save(account);

	            // Gửi email chứa link reset mật khẩu
	            String resetUrl = "http://localhost:8080/reset_password?token=" + token;
	            String subject = "Link đặt lại mật khẩu";

	            // Gọi phương thức sendEmail từ Email service
	            String htmlContent = buildResetPasswordEmailContent(resetUrl);
	            emailService.sendEmail(email, subject, htmlContent);

	            return token; // Trả về token
	        }
	        return null; // Email không tồn tại
	    }

	    // Tìm tài khoản theo token
	    public Optional<AccountEntity> findByToken(String token) {
	        AccountEntity account = accountRepository.findByToken(token);
	        if (account != null && account.getExpiration_time().isAfter(LocalDateTime.now())) {
	            return Optional.of(account);
	        }
	        return Optional.empty(); // Token không hợp lệ hoặc hết hạn
	    }

	    // Đặt lại mật khẩu
	    public boolean resetPassword(String token, String newPassword) {
	        Optional<AccountEntity> accountOpt = findByToken(token);
	        if (accountOpt.isPresent()) {
	            AccountEntity account = accountOpt.get();
	            account.setPassword(passwordEncoder.encode(newPassword)); // Mã hóa mật khẩu mới
	            account.setToken(null); // Xóa token sau khi đổi mật khẩu
	            account.setExpiration_time(null);
	            accountRepository.save(account);
	            return true; // Đổi mật khẩu thành công
	        }
	        return false; // Token không hợp lệ hoặc hết hạn
	    }

	    // Xây dựng nội dung HTML cho email đặt lại mật khẩu
	    private String buildResetPasswordEmailContent(String resetUrl) {
	        StringBuilder htmlContent = new StringBuilder();
	        htmlContent.append("<div style='font-family: sans-serif; min-width: 100%; min-height: 100vh; background-color: rgba(250, 90, 90, 0.8); padding: 2rem;'>")
	                .append("<div style='max-width: 600px; margin: auto;'>")
	                .append("<div style='background-color: white; padding: 2rem; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);'>")
	                .append("<div style='text-align: center; border-bottom: 1px solid #eaeaea; margin-bottom: 2rem;'>")
	                .append("<img src='https://drive.google.com/uc?id=1nFnGPdWnlFK1XRuZtB-8hJWFEVMp0TSF' alt='Mô tả hình ảnh' style='width: 80px; height: 80px; margin-right: 550px;' >") // Đã sửa ở đây
	                .append("<h1 style='font-size: 2rem;'>Đặt Lại Mật Khẩu</h1>")
	                .append("</div>")
	                .append("<div style='padding: 1rem 0; border-bottom: 1px solid #eaeaea; margin-bottom: 2rem;'>")
	                .append("<p>Chào bạn, <br><br>Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn trên For The Love. Để hoàn tất quy trình, vui lòng nhấp vào nút dưới đây để đặt lại mật khẩu của bạn.</p>")
	                .append("<div style='text-align: center; margin: 1rem 0;'>") // Container cho nút
	                .append("<a href='").append(resetUrl).append("' style='background-color: #ff3d49; color: white; text-decoration: none; padding: 1rem 2rem; border-radius: 5px; display: inline-block;'>ĐẶT LẠI MẬT KHẨU</a>")
	                .append("</div>") // Kết thúc container cho nút
	                .append("<p style='margin-top: 1rem;text-align: center;'>Nếu bạn không yêu cầu đặt lại mật khẩu, hãy bỏ qua email này.<br>Chúc bạn một ngày tuyệt vời!<br>Đội ngũ For The Love</p>")
	                .append("</div>")
	                .append("<div style='text-align: center; color: #555;'>")
	                .append("<h3>Cảm ơn bạn đã sử dụng For The Love!</h3>")
	                .append("</div>")
	                .append("</div>")
	                .append("</div>");
	        return htmlContent.toString();
	    }
}
