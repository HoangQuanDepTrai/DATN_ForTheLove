package com.datn.demo.Beans;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class AccountBean {

    private String username;

    private String password;

    private String fullName;

    private String phoneNumber;

    private String email;
}