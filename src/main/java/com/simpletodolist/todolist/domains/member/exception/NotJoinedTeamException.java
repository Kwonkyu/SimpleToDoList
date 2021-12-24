package com.simpletodolist.todolist.domains.member.exception;

import com.simpletodolist.todolist.domains.member.entity.Member;
import com.simpletodolist.todolist.domains.team.entity.Team;
import lombok.Getter;

@Getter
public class NotJoinedTeamException extends RuntimeException{
    public NotJoinedTeamException(Member member, Team team) {
        super(String.format("User %s is not a member of team %s.", member.getAlias(), team.getTeamName()));
    }
}
