package com.simpletodolist.todolist.Snippets;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.snippet.Attributes.*;

public class RequestSnippets {
    public static final ParameterDescriptor teamIdPath = parameterWithName("teamId").description("팀의 식별자입니다.");
    public static final ParameterDescriptor usernamePath = parameterWithName("username").description("사용자의 아이디입니다.");
    public static final ParameterDescriptor todoListIdPath = parameterWithName("todoListId").description("할 일 리스트의 식별자입니다.");
    public static final ParameterDescriptor todoIdPath = parameterWithName("todoId").description("할 일의 식별자입니다.");

    public static class Token {
        public static final FieldDescriptor refreshToken = fieldWithPath("refreshToken").description("기존에 발급받은 JWT Refresh Token 입니다.")
                .attributes(key("constraint").value("만료된 토큰은 사용할 수 없습니다."));
        public static final FieldDescriptor accessToken = fieldWithPath("accessToken").description("기존에 발급받은 JWT Access Token 입니다.")
                .attributes(key("constraint").value("만료된 토큰은 사용할 수 없습니다."));
    }

    public static class Member {
        public static final FieldDescriptor username = fieldWithPath("username").description("사용자의 아이디입니다.")
                .attributes(key("constraint").value("32 글자를 초과할 수 없습니다."));
        public static final FieldDescriptor alias = fieldWithPath("alias").description("사용자의 이름입니다.")
                .attributes(key("constraint").value("32 글자를 초과할 수 없습니다."));
        public static final FieldDescriptor password = fieldWithPath("password").description("사용자의 비밀번호입니다.")
                .attributes(key("constraint").value("64 글자를 초과할 수 없습니다."));
    }

    public static class Team {
        public static class SearchTeam {
            public static final FieldDescriptor searchMode = fieldWithPath("field").description("팀 검색 방식입니다.")
                    .attributes(key("constraint").value("팀 이름(NAME), 팀장의 아이디(LEADER)만 가능합니다."));
            public static final FieldDescriptor searchValue = fieldWithPath("value").description("검색 값입니다.");
            public static final FieldDescriptor includeJoined = fieldWithPath("joined").description("이미 가입한 팀을 검색 결과에 포함할 지 여부입니다.");
        }

        public static class UpdateTeam {
            public static final FieldDescriptor teamName = fieldWithPath("teamName").description("변경하려는 팀의 이름입니다.")
                    .attributes(key("constraint").value("64 글자를 초과할 수 없습니다."));
            public static final FieldDescriptor locked = fieldWithPath("locked").description("변경하려는 팀의 잠금 상태입니다.")
                    .attributes(key("constraint").value("불리언 값이어야 합니다."));
        }

        public static class CreateTeam {
            public static final FieldDescriptor teamName = fieldWithPath("teamName").description("생성하려는 팀의 이름입니다.")
                    .attributes(key("constraint").value("64 글자를 초과할 수 없습니다."));
            public static final FieldDescriptor locked = fieldWithPath("locked").description("생성하려는 팀의 잠금 상태입니다.")
                    .attributes(key("constraint").value("불리언 값이어야 합니다."));
        }
    }

    public static class Todo {
        public static final FieldDescriptor title = fieldWithPath("title").description("할 일의 제목입니다.")
                .attributes(key("constraint").value("64 글자를 초과할 수 없습니다."));
        public static final FieldDescriptor content = fieldWithPath("content").description("할 일의 내용입니다.")
                .attributes(key("constraint").value("1024 글자를 초과할 수 없습니다."));
        public static final FieldDescriptor locked = fieldWithPath("locked").description("할 일의 잠금 상태입니다.")
                .attributes(key("constraint").value("불리언 값입니다."));
    }

    public static class TodoList {
        public static final FieldDescriptor todoListName = fieldWithPath("name").description("할 일 리스트의 제목입니다.")
                .attributes(key("constraint").value("64 글자를 초과할 수 없습니다."));
        public static final FieldDescriptor locked = fieldWithPath("locked").description("할 일 리스트의 잠금 상태입니다.")
                .attributes(key("constraint").value("불리언 값입니다."));
    }

    public static HeaderDescriptor authorization = headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰(JWT)을 담는 헤더입니다.");

}
