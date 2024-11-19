package com.datn.demo.Repositories;

import com.datn.demo.Entities.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<SeatEntity, Integer> {

    // Bạn có thể thêm các phương thức truy vấn tùy chỉnh tại đây
}
