package com.develop.domain.entity.user;

import com.develop.core.exception.ClientException;
import com.develop.domain.entity.baseball.MatchType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    private String pw;

    private String name;

    private String nickname;

    private String phone;

    private Integer grade;

    private Long point;

    @Enumerated(EnumType.STRING)
    private MatchType.TeamCode team;

    private int[] stadium;

    private int[] player;

    private Integer role;

    @Column(name = "profile_img_url")
    private String profileImgUrl;

    private String country;

    private String ip;

    private LocalDateTime created_at;

    public void updatePoint(Long amount) {
        if (this.point + amount < 0) {
            throw new ClientException("포인트가 부족합니다.");
        }
        this.point += amount;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public void setTeam(MatchType.TeamCode team) {
        this.team = team;
    }

    public void setStadium(int[] stadium) {
        this.stadium = stadium;
    }

    public void setPlayer(int[] player) {
        this.player = player;
    }

    public void setRole(Integer role) {
        if (role == UserType.Role.DEVELOPER.ordinal() || role == UserType.Role.ADMIN.ordinal()) {
            throw new ClientException("허용되지 않은 요청입니다");
        }
        this.role = role;
    }

    @Builder
    public User(String userId, String pw, String name, String nickname, String phone, String country, String ip) {
        this.userId = userId;
        this.pw = pw;
        this.name = name;
        this.nickname = nickname;
        this.phone = phone;
        this.country = country;
        this.ip = ip;
    }
}
