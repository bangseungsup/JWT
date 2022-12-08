package com.hanghaeblog.hanghaeblog.entity;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;


import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity(name = "users")
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // nullable: null 허용 여부
    // unique: 중복 허용 여부 (false 일때 중복 허용)
    @Column(nullable = false, unique = true)
    @Size(min = 4, max= 10, message = "최소 4자 이상, 10자 이하를 입력하셔야 합니다. ")
    private String username;

    @Column(nullable = false)
    @Pattern(regexp = "[a-zA-Z][0-9]")
    @Size(min=8, max=15)
    private String password;


    // 조건 설정이 잘 되었는지, 그리고 위에 내용과 합쳐도 되는건지, 그리고 @어노테이션 사용 방법
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

}