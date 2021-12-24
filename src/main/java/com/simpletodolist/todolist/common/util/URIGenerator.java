package com.simpletodolist.todolist.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;

import java.net.URI;

import static com.simpletodolist.todolist.common.util.URIs.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class URIGenerator {

    public static URI joinTeam(long teamId) {
        UriBuilder uriBuilder = new DefaultUriBuilderFactory(HOST).builder();
        return uriBuilder.pathSegment(MEMBER_API, MEMBER_TEAMS_API, "{teamId}")
                .build(teamId);
    }

    public static URI createTeam(long teamId) {
        UriBuilder uriBuilder = new DefaultUriBuilderFactory(HOST).builder();
        return uriBuilder.pathSegment(TEAM_API, "{teamId}")
                .build(teamId);
    }

    public static URI inviteMemberToteam(String memberId, long teamId) {
        UriBuilder uriBuilder = new DefaultUriBuilderFactory(HOST).builder();
        return uriBuilder.pathSegment(TEAM_API, "{teamId}", TEAM_MEMBERS_API, "{memberId}")
                .build(teamId, memberId);
    }

    public static URI createTodoList(long teamId, long todoListId) {
        UriBuilder uriBuilder = new DefaultUriBuilderFactory(HOST).builder();
        return uriBuilder.pathSegment(TEAM_API, "{teamId}", TEAM_TODOLISTS_API, "{todolistId}")
                .build(teamId, todoListId);
    }

    public static URI createTodo(long teamId, long todoListId, long todoId) {
        UriBuilder uriBuilder = new DefaultUriBuilderFactory(HOST).builder();
        return uriBuilder.pathSegment(TEAM_API, "{teamId}", TEAM_TODOLISTS_API, "{todolistId}", TODOLIST_TODOS_API, "{todoId}")
                .build(teamId, todoListId, todoId);
    }
}
