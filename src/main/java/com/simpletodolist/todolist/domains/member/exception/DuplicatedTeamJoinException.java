package com.simpletodolist.todolist.domains.member.exception;

import com.simpletodolist.todolist.domains.member.entity.Member;
import com.simpletodolist.todolist.domains.team.entity.Team;
import lombok.*;

@Getter
public class DuplicatedTeamJoinException extends RuntimeException{
    public DuplicatedTeamJoinException(Member member, Team team) {
        super(String.format("Member %s already joined team %s.", member.getAlias(), team.getTeamName()));
    }
}
