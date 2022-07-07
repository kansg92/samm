package com.samm.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UsersVo {
    private String id;
    private String pwd;
    private String name;
    private String address;
    private String phone;

    // 기본 생성자
    public UsersVo(String id, String pwd, String name, String address, String phone) {
        this.id = id;
        this.pwd = pwd;
        this.name = name;
        this.address = address;
        this.phone = phone;
    }
    public UsersVo() {
    }

    // 로그인
    public UsersVo(String id, String pwd) {
        this.id = id;
        this.pwd = pwd;
    }

    // 최소 회원가입
    public UsersVo(String id, String pwd, String name) {
        this.id = id;
        this.pwd = pwd;
        this.name = name;
    }
}
