package com.datn.demo.Repositories;

import com.datn.demo.Entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {

	// Bạn có thể thêm các phương thức truy vấn tùy chỉnh ở đây nếu cần
}
