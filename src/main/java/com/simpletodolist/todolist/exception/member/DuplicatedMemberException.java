package com.simpletodolist.todolist.exception.member;

import com.simpletodolist.todolist.domain.entity.Member;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DuplicatedMemberException extends RuntimeException{
    private String error = Member.DUPLICATED_MEMBER_FOUND;
    private String message = "Try with different user identification value.";
}
