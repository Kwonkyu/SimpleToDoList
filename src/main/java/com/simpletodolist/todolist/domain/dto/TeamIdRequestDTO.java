package com.simpletodolist.todolist.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Positive;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
// TODO: request, response dto는 컨트롤러 쪽에 내부 클래스로 설정?
public class TeamIdRequestDTO {
    @Positive
    private long teamId;
}
