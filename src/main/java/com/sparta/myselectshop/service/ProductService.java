package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Component
public class ProductService {

    public ProductResponseDto createProduct(ProductRequestDto requestDto) throws SQLException {
        Product product = new Product(requestDto);
        ProductRepository productRepository = new ProductRepository();

        return productRepository.createProduct(product);
    }

    public List<ProductResponseDto> getProducts() throws SQLException{
        ProductRepository productRepository = new ProductRepository();

        return productRepository.getProducts();
    }
}