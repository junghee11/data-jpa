package com.develop.datajpa.request.baseball;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class GetMatchListRequest {

    private LocalDate date = LocalDate.now();

}
