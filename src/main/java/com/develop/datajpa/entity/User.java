package com.develop.datajpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "users")
public class User {

    @Id
    @Column(name = "user_id")
    private String userId;

    private String name;

    private String nickname;

    private String phone;

    private Integer grade;

    private Integer state;

    private String pw;

    private String ip;

    private LocalDateTime created_at;

    public void setPw(String pw) {
        this.pw = pw;
    }

    @Builder
    public User(String userId, String name, String nickname, String phone, String pw) {
        this.userId = userId;
        this.name = name;
        this.nickname = nickname;
        this.phone = phone;
        this.pw = pw;
    }
}
