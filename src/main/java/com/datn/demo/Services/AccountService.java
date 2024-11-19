package com.datn.demo.Services;

import com.datn.demo.Entities.AccountEntity;
import com.datn.demo.Repositories.AccountRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {


    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder; 
    
    public AccountEntity findAccountByUsername(String username) {
        return accountRepository.findByUsername(username);
    }
    public AccountEntity findAccountByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

//    // Tạo token reset mật khẩu
//    public String generateResetPasswordToken(String email) {
//        AccountEntity account = accountRepository.findByEmail(email);
//        if (account != null) {
//            LocalDateTime now = LocalDateTime.now();
//
//            // Kiểm tra và reset số lần yêu cầu nếu qua ngày mới
//            if (account.getLastResetRequestTime() != null && account.getLastResetRequestTime().toLocalDate().isBefore(now.toLocalDate())) {
//                account.setResetRequestCount(0); // Reset số lần yêu cầu về 0 nếu qua ngày mới
//            }
//            
//
//            // Kiểm tra số lần yêu cầu đổi mật khẩu trong ngày
//            if (account.getResetRequestCount() >= 3) {
//                return "Bạn đã yêu cầu đổi mật khẩu 3 lần trong ngày. Vui lòng quay lại vào ngày mai.";
//            }
//
//            // Kiểm tra thời gian yêu cầu đổi mật khẩu gần nhất
//            if (account.getLastResetRequestTime() != null &&
//                account.getLastResetRequestTime().plusMinutes(5).isAfter(now)) {
//                return "Bạn phải đợi thêm 5 phút trước khi yêu cầu đổi mật khẩu.";
//            }
//
//            // Tạo token mới
//            String token = UUID.randomUUID().toString();
//            account.setToken(token);
//            account.setExpiration_time(now.plusMinutes(2)); // Thời gian hết hạn 2 phút
//            account.setLastResetRequestTime(now); // Cập nhật thời gian yêu cầu
//            account.setResetRequestCount(account.getResetRequestCount() + 1); // Tăng số lần yêu cầu
//            accountRepository.save(account);
//
//            // Gửi email chứa link reset mật khẩu
//            String resetUrl = "http://localhost:8080/reset_password?token=" + token;
//            String subject = "Link đặt lại mật khẩu";
//            sendEmail(email, subject, resetUrl);
//
//            return token; // Trả về token
//        }
//        return null; // Email không tồn tại
//    }
//
//   
//
//    // Phương thức gửi email với lựa chọn ngôn ngữ
//    private void sendEmail(String to, String subject, String text) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject(subject);
//
//        message.setText(text); // Sử dụng nội dung đã được cung cấp
//
//        mailSender.send(message);
//    }
//
//    // Tìm tài khoản theo token
//    public Optional<AccountEntity> findByToken(String token) {
//        AccountEntity account = accountRepository.findByToken(token);
//        if (account != null && account.getExpiration_time().isAfter(LocalDateTime.now())) {
//            return Optional.of(account);
//        }
//        return Optional.empty(); // Token không hợp lệ hoặc hết hạn
//    }
//
//    // Đặt lại mật khẩu
//    public boolean resetPassword(String token, String newPassword) {
//        Optional<AccountEntity> accountOpt = findByToken(token);
//        if (accountOpt.isPresent()) {
//            AccountEntity account = accountOpt.get();
//            account.setPassword(passwordEncoder.encode(newPassword)); // Mã hóa mật khẩu mới
//            account.setToken(null); // Xóa token sau khi đổi mật khẩu
//            account.setExpiration_time(null);
//            accountRepository.save(account);
//            return true; // Đổi mật khẩu thành công
//        }
//        return false; // Token không hợp lệ hoặc hết hạn
//    }
//    public String createVerificationToken(AccountEntity account) {
//        String token = UUID.randomUUID().toString();
//        account.setToken(token);
//        account.setExpiration_time(LocalDateTime.now().plusHours(24)); // Token có hiệu lực trong 24 giờ
//        accountRepository.save(account); // Lưu tài khoản để cập nhật token
//        return token;
//    }
//
//    // Gửi email xác nhận
//    public void sendConfirmationEmail(String email, String confirmationLink) {
//        String subject = "Xác nhận đăng ký tài khoản";
//        String text = confirmationLink; // Chỉ hiển thị liên kết mà không có thông điệp
//        sendEmail(email, subject, text);
//    }


}
