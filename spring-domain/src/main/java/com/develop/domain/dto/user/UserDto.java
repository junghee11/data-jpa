package com.develop.domain.dto.user;


import com.develop.domain.entity.baseball.MatchType;

public interface UserDto {
    String getUserId();

    String getNickname();

    String getName();

    String getPhone();

    Integer getRole();

    String getGrade();

    String getPoint();

    String getProfileImgUrl();

    MatchType.TeamCode getTeam();

    int[] getStadium();

    int[] getPlayer();

}
