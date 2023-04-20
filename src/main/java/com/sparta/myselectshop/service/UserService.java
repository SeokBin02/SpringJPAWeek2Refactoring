package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.LoginRequestDto;
import com.sparta.myselectshop.dto.SignupRequestDto;
import com.sparta.myselectshop.entity.User;
import com.sparta.myselectshop.entity.UserRoleEnum;
import com.sparta.myselectshop.jwt.JwtUtil;
import com.sparta.myselectshop.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private static final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    @Transactional
    public void signup(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();
        String email = requestDto.getEmail();

       // 회원 중복 체크
        Optional<User> nameCheck = userRepository.findByUsername(username);
//       isPresent() : 값이 있으면 true를 반환하고 그렇지 않으면 false를 반환합니다.
       if(nameCheck.isPresent()){
           throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
       }

       // 사용자 ROLE 확인
        UserRoleEnum role = UserRoleEnum.USER;
       if(requestDto.isAdmin()){
           if(!requestDto.getAdminToken().equals(ADMIN_TOKEN)){     // 회원가입시 입력한 관리자 암호가 서버에서 사용중인 관리자 암호와 같은지 비교
               throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능 합니다.");
           }
           role = UserRoleEnum.ADMIN;
       }

       User user = new User(username, password, email, role);
       userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public void login(LoginRequestDto requestDto, HttpServletResponse response){
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();

        // 사용자 확인
        User user = userRepository.findByUsername(username).orElseThrow(()-> new IllegalArgumentException("등록된 사용자가 없습니다.!!"));

        // 비밀 번호 확인
        if(!user.getPassword().equals(password)){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.!!");
        }

        response.addHeader(jwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(user.getUsername(), user.getRole()));
    }
//    public void login(LoginRequestDto requestDto) {
//        String username = requestDto.getUsername();
//        String password = requestDto.getPassword();
//
//        // 사용자 확인
//        User user = userRepository.findByUsername(username).orElseThrow(()-> new IllegalArgumentException("등록된 사용자가 없습니다."));
//
//        // 비밀 번호 확인
//        if(!user.getPassword().equals(requestDto.getPassword())){
//            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
//        }
//    }
}
