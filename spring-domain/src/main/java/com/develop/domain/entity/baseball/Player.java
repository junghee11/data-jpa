package com.develop.domain.entity.baseball;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Getter
@Entity
@ToString
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "player")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    private String name;

    private String num;

    private String position;

    private LocalDateTime birth;

    private String body;

    private String team;

    private String awards;

    private String song;

    @Column(name = "img_url")
    private String imgUrl;

    private int pay;

    private int hit;

    @Column(name = "home_run")
    private int homeRun;

    private int run;

    private String inning;

    @Column(name = "four_ball")
    private int fourBall;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
