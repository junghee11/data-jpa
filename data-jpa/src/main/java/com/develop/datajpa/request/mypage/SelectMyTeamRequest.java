package com.develop.datajpa.request.mypage;

import com.develop.datajpa.entity.baseball.MatchType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SelectMyTeamRequest {

    @NotNull(message = "응원하려는 팀을 확인해주세요")
    private MatchType.TeamCode team;

}
