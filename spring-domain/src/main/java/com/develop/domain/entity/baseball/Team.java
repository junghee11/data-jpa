package com.develop.domain.entity.baseball;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Entity
@ToString
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "team")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(name = "team_code")
    @Enumerated(EnumType.STRING)
    private MatchType.TeamCode teamCode;

    private String name;

    private Integer rank;

    private Integer win;

    private Integer loose;

    private String draw;

    private String director;

    @Column(name = "img_url")
    private String imgUrl;

    private String stadium;

    private String homepage;

    private String outline;

}
