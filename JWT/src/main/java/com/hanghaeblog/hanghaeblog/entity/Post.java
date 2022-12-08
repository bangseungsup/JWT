package com.hanghaeblog.hanghaeblog.entity;

import com.hanghaeblog.hanghaeblog.dto.PostRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Getter
@Entity
@NoArgsConstructor
public class Post extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)       //
    private Long userId;

//    @Column(nullable = false)
//    private String password; // 스펠 틀릴 것 같으면 생성자 단축키로 만들 것

    public Post(PostRequestDto requestDto, Long userId) {
        this.title = requestDto.getTitle();
        this.username = requestDto.getUsername();
        this.contents = requestDto.getContents();
        this.userId = userId;       // 내가 작성한 게시물 및 나와 관련된 게시물 찾는데 사용
//        this.password = requestDto.getPassword();
    }


    public void update(PostRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.username = requestDto.getUsername();
        this.contents = requestDto.getContents();
//        this.password = requestDto.getPassword();
    }



}
