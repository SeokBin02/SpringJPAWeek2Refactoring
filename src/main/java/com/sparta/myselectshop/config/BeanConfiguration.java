package com.sparta.myselectshop.config;

import com.sparta.myselectshop.repository.ProductRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
//public class BeanConfiguration {
//
//    @Bean
//    public ProductRepository productRepository(){
//        String dbUrl = "jdbc:h2:mem:db";
//        String username = "sa";
//        String password = "";
//        return new ProductRepository(dbUrl, username, password);
//    }
//    // 스프링 서버가 뜰 때 스프링 IoC에 '빈' 저장
//    // public ProductRepository productRepository() {..} → productRepository
//}
