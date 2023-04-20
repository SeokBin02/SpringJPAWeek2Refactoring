package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.entity.User;
import com.sparta.myselectshop.entity.UserRoleEnum;
import com.sparta.myselectshop.jwt.JwtUtil;
import com.sparta.myselectshop.naver.dto.ItemDto;
import com.sparta.myselectshop.repository.ProductRepository;
import com.sparta.myselectshop.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//@RequiredArgsConstructor
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto requestDto, HttpServletRequest request){
//        Product product = new Product(requestDto);
        // 요청 받은 DTO로 DB에 저장할 객체 만들기
        // save() 와 saveAndFlush의 차이점
        // save는 영속성 컨택스트에 저장되어 있다가 flush() 또는 commit() 수행시 DB에 저장된다.
        // saveAndFlush는 즉시 DB에 변경사항을 적용하는 방식이다.
        // createProduct 메서드는 값 하나를 저장하기 때문에 sql 지연쓰기가 필요없기 때문에 saveAndFlush를 사용한다.
        // Request에서 Token 가져오기
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        // 해당 토큰의 null 유무, 유효성 유무를 체크한 후 token에서 사용자 정보를 꺼내와서 사용
        if(token != null){
            if(jwtUtil.validateToken(token)){
                claims = jwtUtil.getUserInfoFromToken(token);
            }else{
                throw new IllegalArgumentException("Token Error");
            }

            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

            Product product = productRepository.saveAndFlush(new Product(requestDto, user.getId()));

            return new ProductResponseDto(product);

        } else {
            return null;
        }
//        Product product = productRepository.saveAndFlush(new Product(requestDto,request));
////        ProductRepository productRepository = new ProductRepository();
//        return new ProductResponseDto(product);
    }

    @Transactional(readOnly = true)
//    트랜잭션이 커밋되어도 영속성 컨텍스트를 플러시하지 않는다. 플러시할 때 수행되는 엔티티의 스냅샷 비교 로직이 수행되지 않으므로
//    성능을 향상 시킬 수 있다.
    public List<ProductResponseDto> getProducts(HttpServletRequest request){
        // Request 에서 Token 가져오기
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        // 토큰이 있는 경우에만 관심상품 조회 가능
        if(token != null) {
            if (jwtUtil.validateToken(token)) { // 토큰의 유효성 검사
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("Token ERROR");
            }

            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

            // 사용자 권한 가져와서 ADMIN 이면 전체 조회, USER면 본인이 추가한 부분만 조회
            UserRoleEnum userRoleEnum = user.getRole();
            System.out.println("role = " + userRoleEnum);

            List<ProductResponseDto> list = new ArrayList<>();
            List<Product> productList;

            if (userRoleEnum == UserRoleEnum.USER) {// 사용자 권한이 User라면 해당 user의 id값으로 상품을 불러오기
                productList = productRepository.findAllByUserId(user.getId());
            } else {                                // 사용자 권한이 admin이라면 모든 상품 불러오기
                productList = productRepository.findAll();
            }

            for (Product product : productList) {
                list.add(new ProductResponseDto(product));
            }

            return list;
        } else{
            return null;
        }

//        ProductRepository productRepository = new ProductRepository();

//        List<ProductResponseDto> responseDtos = new ArrayList<>();
//
//        List<Product> all = productRepository.findAll();
//        for (Product product : all) {
//            responseDtos.add(new ProductResponseDto(product));
//        }

        // 위 코드를 steam()으로 리펙토링
//        List<ProductResponseDto> responseDtos = productRepository.findAll().stream().map(ProductResponseDto::new).collect(Collectors.toList());
//        return responseDtos;
    }

    @Transactional
    public Long updateProduct(Long id, ProductMypriceRequestDto requestDto, HttpServletRequest request){
        // Request 에서 Token 가져오기
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        // 토큰이 있는 경우에만 관심상품 조회 가능
        if(token != null) {
            if (jwtUtil.validateToken(token)) { // 토큰의 유효성 검사
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("Token ERROR");
            }

            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );

            Product product = productRepository.findByIdAndUserId(id, user.getId()).orElseThrow(
                    () -> new NullPointerException("해당 상품은 존재하지 않습니다.")
            );

            product.update(requestDto);

            return product.getId();
        } else {
            return null;
        }

////        ProductRepository productRepository = new ProductRepository();
//        Product product = productRepository.findById(id).orElseThrow(() -> new NullPointerException("해당 상품은 존재하지 않아용!!"));
////        Optional<Product> product = productRepository.findById(id); // Optional로 NPE를 막을 순 있지만 아래 method는 실행 불가
//        product.setMyprice(requestDto.getMyprice());
//
//        return product.getId();
    }

    @Transactional
    public void updateBySearch(Long id, ItemDto itemDto) {
        Product product = productRepository.findById(id).orElseThrow(()-> new NullPointerException("해당 상품은 존재하지 않습니다.") );

        product.updateByItemDto(itemDto);
    }
}
