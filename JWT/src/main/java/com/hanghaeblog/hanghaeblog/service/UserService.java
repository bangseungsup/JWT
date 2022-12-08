package com.hanghaeblog.hanghaeblog.service;

import com.hanghaeblog.hanghaeblog.JWT.JwtUtil;
import com.hanghaeblog.hanghaeblog.dto.LoginRequestDto;
import com.hanghaeblog.hanghaeblog.dto.SignupRequestDto;
import com.hanghaeblog.hanghaeblog.entity.User;
import com.hanghaeblog.hanghaeblog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;    // Repository와 연결
    private final JwtUtil jwtUtil;  // JwtUtil @Component 빈 등록을 통해 의존성 주입 가능

    @Transactional
    public void signup(SignupRequestDto signupRequestDto){
        //1. 컨트롤러에서 전달받은 signupRequestDto의 username을 객체화 한다.
        String username = signupRequestDto.getUsername();               // Repository에 username이 있는지 없는지 확인하기 위해
        //2. 컨트롤러에서 전달받은 signupRequestDto의 password를 객체화 한다.
        String password = signupRequestDto.getPassword();
        //3. userRepository의 findByUsername 메서드를 사용하여 데이터베이스에 접근 User 테이블에 username 컬럽에 파라미터로 전달받은 username과 일치하는 데이터가 있으면 찾아온다.
        Optional<User> found = userRepository.findByUsername(username); // Repository에 추가
        if (found.isPresent()){
            throw new IllegalArgumentException("중복된 아이디가 있습니다.");
        }

        User user = new User(username, password);
        userRepository.save(user);
        }

    @Transactional(readOnly = true)
    public void login(LoginRequestDto loginRequestDto) {

        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        // 사용자 확인
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("등록된 사용자가 없습니다.")
        );

        // 비밀번호 확인
        if(!user.getPassword().equals(password)){
            throw  new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        // 정상적으로 로그인처리를 해줘야하는 유저이기 때문에 response 데이터를 생성
        HttpServletResponse response = null;

        // 우리가 들고 온 response에 addHeader를 사용하면 Header쪽에 값을 같이 낳어줄 수 있는데,
        // key 값에는 AUTHORIZATION_HEADER 와 createToken 메소드를 사용해서 token을 만들어줄건데 앞에서 봤듯이 유저의 이름과 유저의 권한을 넣어 줄 것이다.
        // 위에 User user = userRepository.findByUsername(username) 에서 User를 가져왔기 때문에 넣어 줄 수 있다.
        // 여기서 jwtUtil을 사용해서 위에서 의존성 주입을 해야 한다. ( private final JwtUtil jwtUtil;)

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(user.getUsername()));
    }



}
