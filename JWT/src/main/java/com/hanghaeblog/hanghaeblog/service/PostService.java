package com.hanghaeblog.hanghaeblog.service;


import com.hanghaeblog.hanghaeblog.JWT.JwtUtil;
import com.hanghaeblog.hanghaeblog.dto.PostRequestDto;
import com.hanghaeblog.hanghaeblog.dto.PostResponseDto;
import com.hanghaeblog.hanghaeblog.entity.Post;
import com.hanghaeblog.hanghaeblog.entity.User;
import com.hanghaeblog.hanghaeblog.repository.PostRepository;
import com.hanghaeblog.hanghaeblog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Transactional // 리스트 형태로 받아와야하기 때문에 List<Post>
    public List<Post> getPosts() {
        return postRepository.findAllByOrderByModifiedAtDesc();
    }

    @Transactional // 데이터들을 넣어주려면 비워내야하기 때문에 new 사용
    public PostResponseDto createPost(PostRequestDto requestDto, HttpServletRequest request) {

        String token = jwtUtil.resolveToken(request);
        Claims claims;

        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("Token error");
            }

            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );

            Post post = postRepository.saveAndFlush(new Post(requestDto, user.getId()));

            return new PostResponseDto(post);
        } else {
            return null;
        }
    }

    // 단순히 리스트만 받아오는거면 jwt 토큰이 필요할까? 필요 없다면 String ~ ("사용자가 존재하지 않습니다") 까지
    @Transactional // 수정 및 삭제 findById(id).orElseThrow
    public PostResponseDto update(Long postId, PostRequestDto requestDto, HttpServletRequest request) {
        User userFromToken = findUserFromToken(request);

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("아이디가 존재하지 않습니다.")
        );
        post.update(requestDto);
        return new PostResponseDto(post);

    }

    @Transactional
    public String deletePost(Long id , HttpServletRequest request) {

        User userFromToken = findUserFromToken(request);

        Post post = postRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("아이디가 없습니다.")
        );
        String username = userFromToken.getUsername();
        String postUsername = post.getUsername();

        if (!username.equals(postUsername)){
            throw new IllegalArgumentException("본인이 작성하지 않은 게시글은 삭제할 수 없습니다.");
        }

        postRepository.delete(post);        // 리턴 값 설정하는 법

        return "정상 삭제 완료";
    }


    @Transactional
    public PostResponseDto getPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("게시글이 존재하지 않습니다.")
        );
        return new PostResponseDto(post);
    }

    public User findUserFromToken(HttpServletRequest request){
        User user = new User();
        String token = jwtUtil.resolveToken(request);

        if (token != null) {
            Claims claims;
            if (jwtUtil.validateToken(token)) {
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("Token error");
            }

            user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );
            return user;
        }
        return user;
    }
}

