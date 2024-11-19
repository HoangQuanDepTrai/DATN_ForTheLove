package com.datn.demo.Repositories;

import com.datn.demo.Entities.RoomEntity;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Integer> {
    Optional<RoomEntity> findById(int roomId);

	// Bạn có thể thêm các phương thức truy vấn tùy chỉnh ở đây nếu cần
}
