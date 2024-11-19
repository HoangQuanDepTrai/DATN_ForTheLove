package com.datn.demo.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@Entity
@Table(name = "ACCOUNT")
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACCOUNT_ID")  // Tên cột trong bảng ACCOUNT
    private int accountId;

    @Column(name = "USERNAME", nullable = false, unique = true) // Username không được để trống và phải duy nhất
    private String username;

    @Column(name = "PASSWORD", nullable = false) // Mật khẩu không được để trống
    private String password;

    @Column(name = "FULL_NAME", nullable = false) // Họ và tên
    private String fullName;

    @Column(name = "PHONE_NUMBER", nullable = false, unique = true)
    // Số điện thoại không được để trống và phải duy nhất
    private String phoneNumber;

    @Column(name = "EMAIL", nullable = false, unique = true) // Email không được để trống và phải duy nhất
    private String email;

    @ManyToOne
    @JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID", nullable = false) // Liên kết với ROLE
    private RoleEntity role; // Liên kết với RoleEntity

    @Column(name = "TOKEN")
    private String token;

    @Column(name = "EXPIRE_TIME")
    private LocalDateTime expiration_time;

    @Column(name = "RESET_REQUEST_COUNT") // Số lần yêu cầu reset mật khẩu
    private Integer resetRequestCount = 0; // Khởi tạo với giá trị mặc định là 0

    @Column(name = "LAST_RESET_REQUEST_TIME") // Thời gian yêu cầu reset mật khẩu gần nhất
    private LocalDateTime lastResetRequestTime;
    
}
