package com.simpletodolist.todolist.Snippets;

import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.List;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;

public class EntityDescriptor {

    public static class Team {
        // https://stackoverflow.com/questions/31162140/how-to-document-top-level-array-as-response-payload-with-spring-rest-docs
        public static final FieldDescriptor teams = subsectionWithPath("[]").description("팀의 목록입니다.");
        public static final FieldDescriptor teamId = fieldWithPath("id").description("팀의 식별자입니다.");
        public static final FieldDescriptor teamLeaderUserId = fieldWithPath("teamLeaderUserId").description("팀장의 회원 아이디입니다.");
        public static final FieldDescriptor teamLeaderUsername = fieldWithPath("teamLeaderUsername").description("팀장의 회원 이름입니다.");
        public static final FieldDescriptor teamName = fieldWithPath("teamName").description("팀의 이름입니다.");
        public static final FieldDescriptor todoLists = fieldWithPath("todoLists").description("팀의 할 일 리스트 목록입니다.");
        public static final FieldDescriptor locked = fieldWithPath("locked").description("팀의 잠금 상태입니다.");
        public static final List<FieldDescriptor> teamInformation = List.of(
                teamId, teamName, teamLeaderUserId, teamLeaderUsername, todoLists, locked);
    }

    public static class Member {
        public static final FieldDescriptor members = subsectionWithPath("[]").description("회원의 목록입니다.");
        public static final FieldDescriptor id = fieldWithPath("id").description("사용자의 식별값입니다.");
        public static final FieldDescriptor userId = fieldWithPath("userId").description("사용자의 아이디입니다.");
        public static final FieldDescriptor username = fieldWithPath("username").description("사용자의 이름입니다.");
        public static final FieldDescriptor password = fieldWithPath("password").description("사용자의 비밀번호입니다.");
        public static final FieldDescriptor locked = fieldWithPath("locked").description("사용자의 잠금 상태입니다.");
        public static final FieldDescriptor teams = subsectionWithPath("teams").description("사용자가 가입한 팀의 정보입니다.");
        public static final List<FieldDescriptor> memberInformation = List.of(
                id, userId, username, password, locked, teams);
        public static final List<FieldDescriptor> loginMemberInformation = List.of(
                id, userId, username, password, locked, teams, ResponseSnippets.JWT.token);
    }

    public static class Todo {
        public static final FieldDescriptor todos = subsectionWithPath("[]").description("할 일의 목록입니다.");
        public static final FieldDescriptor id = fieldWithPath("id").description("할 일의 식별자입니다.");
        public static final FieldDescriptor title = fieldWithPath("title").description("할 일의 제목입니다.");
        public static final FieldDescriptor content = fieldWithPath("content").description("할 일의 내용입니다.");
        public static final FieldDescriptor locked = fieldWithPath("locked").description("할 일의 잠금 상태입니다.");
        public static final List<FieldDescriptor> todoInformation = List.of(
                id, title, content, locked);
    }

    public static class TodoList {
        public static final FieldDescriptor todoLists = subsectionWithPath("[]").description("할 일 리스트의 목록입니다.");
        public static final FieldDescriptor id = fieldWithPath("id").description("할 일 리스트의 식별자입니다.");
        public static final FieldDescriptor ownerUserId = fieldWithPath("ownerUserId").description("할 일 리스트를 생성한 회원의 아이디입니다.");
        public static final FieldDescriptor name = fieldWithPath("name").description("할 일 리스트의 이름입니다.");
        public static final FieldDescriptor todos = fieldWithPath("todos").description("할 일의 목록입니다.");
        public static final FieldDescriptor locked = fieldWithPath("locked").description("할 일 리스트의 잠금 상태입니다.");
        public static final List<FieldDescriptor> todoListInformation = List.of(
                id, ownerUserId, name, todos, locked);
    }
}
