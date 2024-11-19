package com.datn.demo.Services;

import com.datn.demo.Entities.ProductEntity;
import com.datn.demo.Repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // Phương thức để tìm sản phẩm theo ID
    public Optional<ProductEntity> getProductById(int id) {
        return productRepository.findById(id);
    }

    // Lấy tất cả sản phẩm
    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }

    // Tạo sản phẩm mới
    public ProductEntity createProduct(ProductEntity product) {
        return productRepository.save(product);
    }

    // Xóa sản phẩm theo ID
    public void deleteProduct(int id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Product not found with ID: " + id);
        }
    }
}
