package com.develop.datajpa.dto.user;


import com.develop.datajpa.entity.baseball.MatchType;

public interface UserDto {
    String getUserId();

    String getNickname();

    String getName();

    String getPhone();

    String getGrade();

    String getPoint();

    String getProfileImgUrl();

    MatchType.TeamCode getTeam();

    int[] getStadium();

    int[] getPlayer();

}
