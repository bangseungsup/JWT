package com.hanghaeblog.hanghaeblog.Controller;

import com.hanghaeblog.hanghaeblog.dto.PostRequestDto;
import com.hanghaeblog.hanghaeblog.dto.PostResponseDto;
import com.hanghaeblog.hanghaeblog.entity.Post;
import com.hanghaeblog.hanghaeblog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {  // request 요청 형태 다시 한번 복습하고 하기

    private final PostService postService;

    @GetMapping("/api/posts") // 얻어와야 한다-> get ,, 목록 조회이기 때문에 list
    public List<Post> getPosts() {
        return postService.getPosts();
    }

    @PostMapping("/api/posts") // 작성해서 보내준다 -> post
    public PostResponseDto createPost(@RequestBody PostRequestDto requestDto, HttpServletRequest request) {
        return postService.createPost(requestDto, request);
    }

    @PutMapping("/api/posts/{id}") // put은 전체, patch는 일부 --> 일단 put 쓰라니까 put
    public PostResponseDto updatePost(@PathVariable Long postId, @RequestBody PostRequestDto postRequestDto, HttpServletRequest request) {
        return postService.update(postId, postRequestDto, request);
    }

    @DeleteMapping("/api/posts/{id}") // delete는 delete 쓸 것
    public String deletePost(@PathVariable Long id, HttpServletRequest request) {
        return postService.deletePost(id, request);
    }

    @GetMapping("/api/posts/{id}") //id 값을 그대로 받아와야하기 떄문에
    public PostResponseDto getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

}
