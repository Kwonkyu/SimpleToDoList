package com.simpletodolist.todolist.exception.member;

import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.domain.entity.Team;
import lombok.*;

@Getter
public class DuplicatedTeamJoinException extends RuntimeException{
    public DuplicatedTeamJoinException(Member member, Team team) {
        super(String.format("Member %s already joined team %s.", member.getAlias(), team.getTeamName()));
    }
}
