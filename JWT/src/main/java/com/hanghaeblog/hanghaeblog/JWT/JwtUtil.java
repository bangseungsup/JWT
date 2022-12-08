package com.hanghaeblog.hanghaeblog.JWT;

import com.hanghaeblog.hanghaeblog.entity.UserRoleEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    // Header Key 값
    public static final String AUTHORIZATION_HEADER = "Authorization";  // Authorization의 key 값 (Header에 들어가는)
    // 사용자 권한 값의 Key
    public static final String AUTHORIZATION_KEY = "auth";
    // Token 식별자
    private static final String BEARER_PREFIX = "Bearer ";  // Token 만들 때 같이 값이 붙어서 들어가는 부분
    // 토큰 만료시간
    private static final long TOKEN_TIME = 60 * 60 * 1000L;

    @Value("${jwt.secret.key}")
    // @value 는 application properties 에 있는 jwt.secret.key 키 값을 중괄호 안에 내용으로 가져올 수 있음
    private String secretKey;
    private Key key;
    // key 객체는 우리가 토큰을 만들 때 넣어줄 key 값이라고 생각하면 된다.
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct  // 처음 객체가 생성될 때 초기화하는 함수
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        // @jwt.secret.key가 base64로 인코딩 되어 있기 때문에 값을 한번 가져와서 decode를 하는 과정
        // 이 반환 값이 byte 배열이어서 byte 배열로 받은 다음에 위에 key 객체에 넣어줄껀데 key 객체를 넣을 떄는,
        key = Keys.hmacShaKeyFor(bytes);
        // hmacShaKeyFor 이라는 메소드에 bytes 객체 값을 넣어주면 만들어진다. -> key 완성
    }

    // header 토큰을 가져오기
    public String resolveToken(HttpServletRequest request) {
        // HttpServletRequest 객체 안에는 우리가 가져와야 할 토큰이 Header에 들어있다.
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        // getHeader 후 거기에 파라미터로 어떠한 key를 가져올지 넣어주면 된다. (여기선 AUTHORIZATION_HEADER) 키에 들어있는 Token 값 가져온다.
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            // bearToken을 가지고 있는지, BEARER_PREFIX 로 시작하는지 확인하고
            return bearerToken.substring(7);
            // substring(7)으로 앞에 7자리를 지워주고 반환(Return)  ------> (BEARER 6글자 + 띄어쓰기 1자 -> 총 7글자) 토큰에 연관없는 7글자여서
        }
        return null;
    }

    // 토큰 생성 (JWT 만드는 메소드)
    public String createToken(String username) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username)
                        // 특정한 공간, 그 공간 안에 username을 넣어 줄 것이다.
                        .claim(AUTHORIZATION_KEY , username)
                        // 사용자의 권한, 그 권한을 가져올 때는 우리가 지정해놓은 Auth key를 사용할 것이다.
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME))
                        // setExpriration은 우리가 이 토큰을 언제까지 유효하게 가져갈 것인지 지정해주는 부분,
                        // date 객체는 앞의 new Date에서 가져온거고, 여기에 getTime()을 하면 현재 시간을 가지고 오고
                        // 거기에 우리가 지정해 놓은 (TOKEN_TIME - 60 * 60 * 1000L); 시간까지 유효하다
                        .setIssuedAt(date)
                        // 이 토큰이 언제 만들어졌는지 넣어주는 부분 (생략해도 된다.)
                        .signWith(key, signatureAlgorithm)
                        // 위에서 Secret Key를 사용해서 만든 key 객체와 그 key 객체를 어떤 알고리즘을 사용해서 암호화 할 것인지 지정해주는 부분
                        // 위에서 siganatureAlgorithm 의 암호화 형태는 다양한데, siganatureAlgorithm 객체를 보면 "HS256"을 통해 암호화
                        .compact();
        // compact 를 통해 String 형식의 JWT 토큰으로 반환
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            // JWTs 안에 있는 parseBuilder를 통해 검증을 할 건데, setSigningKey에다가 우리가 Token을 만들 떄 사용한 key를 넣어주고,
            // parseClaimsJws 파라미터에 token을 넣어주면 내부적으로 토큰을 검증해 준다.
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        } // 그리고 아래 catch로 맞는지 맞지 않는지 잡아줄 수 있다.
        return false;
    }

    // 토큰에서 사용자 정보 가져오기.
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        // 위의 검증과 동일한 구조를 보이지만, 마지막에 getBody()를 통해서 그 안에 있는 정보들을 가지고 올 수 있다.
    }       // 사실 이 부분만해도 검증이 된다고 봐도 무방하다.
    // 앞에 ValidateToken을 통해 유효한 토큰임을 이미 인증했기 때문에 여기에는 catch가 없다.

}