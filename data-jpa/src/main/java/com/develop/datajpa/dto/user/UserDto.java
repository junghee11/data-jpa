package com.develop.datajpa.dto.user;


import com.develop.datajpa.entity.baseball.MatchType;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

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
