package com.simpletodolist.todolist.exception.member;


import com.simpletodolist.todolist.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoMemberFoundException extends RuntimeException{
    private String error = Member.NO_MEMBER_FOUND;
    private String message = "Try with different member identification value.";
}
