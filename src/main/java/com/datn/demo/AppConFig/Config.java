package com.datn.demo.AppConFig;

import com.datn.demo.Entities.AccountEntity;
import com.datn.demo.Entities.RoleEntity;
import com.datn.demo.Repositories.AccountRepository;
import com.datn.demo.Repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class Config {

    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private RoleRepository roleRepository; // Thêm RoleRepository vào đây

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorize -> authorize
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Chỉ admin mới được vào
                        .requestMatchers("/user/**").hasRole("USER")
                        .requestMatchers("/forgot-password").permitAll() // Cho phép truy cập vào trang quên mật khẩu
                        .anyRequest().permitAll() // Các trang khác cho phép truy cập
                )
                .formLogin(form -> form
                        .loginPage("/login") // Trang đăng nhập custom
                        .loginProcessingUrl("/perform_login") // Đường dẫn để xử lý form
                        .defaultSuccessUrl("/index", true) // Trang sẽ được chuyển tới sau khi đăng nhập thành công
                        .failureUrl("/login?error=true") // Đường dẫn khi đăng nhập thất bại
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login") // Trang đăng nhập Google
                        .defaultSuccessUrl("/oauth2/success", true) // Đường dẫn thành công cho OAuth2
                        .failureUrl("/login?error=true") // Đường dẫn khi đăng nhập thất bại
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // Đường dẫn để xử lý logout
                        .logoutSuccessUrl("/login?logout=true") // Chuyển hướng sau khi logout thành công
                        .permitAll()
                )
                .csrf().disable(); // Tắt CSRF nếu không cần thiết

        return http.build();
    }

    // Dịch vụ OAuth2 tùy chỉnh
    private OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        return new DefaultOAuth2UserService();
    }

    // Phương thức để lưu người dùng mới với mật khẩu đã mã hóa
    public void saveUser(String username, String password, String fullName, String phoneNumber, String email,
                         RoleEntity role) {
        // Mã hóa mật khẩu trước khi lưu vào cơ sở dữ liệu
        String encodedPassword = passwordEncoder().encode(password);

        // Tạo một tài khoản mới với mật khẩu đã mã hóa
        AccountEntity account = new AccountEntity();
        account.setUsername(username);
        account.setPassword(encodedPassword); // Mật khẩu được lưu dưới dạng mã hóa
        account.setFullName(fullName);
        account.setPhoneNumber(phoneNumber);
        account.setEmail(email);
        account.setRole(role);

        // Lưu tài khoản vào cơ sở dữ liệu
        accountRepository.save(account);
    }
}
