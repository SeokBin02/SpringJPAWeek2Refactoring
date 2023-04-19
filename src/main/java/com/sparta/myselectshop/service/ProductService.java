package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

//@RequiredArgsConstructor
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto requestDto){
//        Product product = new Product(requestDto);
        // 요청 받은 DTO로 DB에 저장할 객체 만들기
        // save() 와 saveAndFlush의 차이점
        // save는 영속성 컨택스트에 저장되어 있다가 flush() 또는 commit() 수행시 DB에 저장된다.
        // saveAndFlush는 즉시 DB에 변경사항을 적용하는 방식이다.
        // createProduct 메서드는 값 하나를 저장하기 때문에 sql 지연쓰기가 필요없기 때문에 saveAndFlush를 사용한다.
        Product product = productRepository.saveAndFlush(new Product(requestDto));
//        ProductRepository productRepository = new ProductRepository();
        return new ProductResponseDto(product);
    }

    @Transactional
    public List<ProductResponseDto> getProducts(){
//        ProductRepository productRepository = new ProductRepository();

//        List<ProductResponseDto> responseDtos = new ArrayList<>();
//
//        List<Product> all = productRepository.findAll();
//        for (Product product : all) {
//            responseDtos.add(new ProductResponseDto(product));
//        }

        // 위 코드를 steam()으로 리펙토링
        List<ProductResponseDto> responseDtos = productRepository.findAll().stream().map(ProductResponseDto::new).collect(Collectors.toList());
        return responseDtos;
    }

    @Transactional
    public Long updateProduct(Long id, ProductMypriceRequestDto requestDto){
//        ProductRepository productRepository = new ProductRepository();
        Product product = productRepository.findById(id).orElseThrow(() -> new NullPointerException("해당 상품은 존재하지 않아용!!"));
        product.setMyprice(requestDto.getMyprice());

        return product.getId();
    }
}
