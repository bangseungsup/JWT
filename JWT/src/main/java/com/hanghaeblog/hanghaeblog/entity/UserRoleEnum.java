package com.hanghaeblog.hanghaeblog.entity;
// 유저의 권한을 알려주는 Enum (Enum :

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserRoleEnum {
    USER,  // 사용자 권한
    ADMIN  // 관리자 권한
}