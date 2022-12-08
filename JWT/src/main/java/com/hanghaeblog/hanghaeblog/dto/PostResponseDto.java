package com.hanghaeblog.hanghaeblog.dto;


import com.hanghaeblog.hanghaeblog.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponseDto {
    private Long id;
    private String title;
    private String username;
    private String contents;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt; // 스펠 틀릴 것 같으면 생성자 단축키로 만들 것

    public PostResponseDto(Post entity) {
        this.id = entity.getId();
        this.username = entity.getUsername();
        this.title = entity.getTitle();
        this.contents = entity.getContents();
        this.createdAt = entity.getCreatedAt();
        this.modifiedAt = entity.getModifiedAt();
    }
}
