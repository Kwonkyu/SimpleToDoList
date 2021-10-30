package com.simpletodolist.todolist.exception.member;

import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.domain.entity.Team;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
public class NotJoinedTeamException extends RuntimeException{
    public NotJoinedTeamException(Member member, Team team) {
        super(String.format("User %s is not a member of team %s.", member.getAlias(), team.getTeamName()));
    }
}
