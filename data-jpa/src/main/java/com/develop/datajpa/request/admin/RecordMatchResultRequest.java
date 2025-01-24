package com.develop.datajpa.request.admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RecordMatchResultRequest {

    @NotNull(message = "경기번호가 확인되지 않습니다")
    private long idx;

    @Min(value = 0, message = "점수는 0보다 작을 수 없습니다")
    private int home = 0;

    @Min(value = 0, message = "점수는 0보다 작을 수 없습니다")
    private int away = 0;

}
