package com.datn.demo.Repositories;

import com.datn.demo.Entities.InvoiceDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceDetailsRepository extends JpaRepository<InvoiceDetailsEntity, Integer> {

    // Thêm các phương thức truy vấn tùy chỉnh nếu cần
}
