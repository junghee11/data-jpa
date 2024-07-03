package com.develop.datajpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@ToString(of = {"id", "name", "nickname"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private final List<Article> article = new ArrayList<>();

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
