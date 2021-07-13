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
    public static final ParameterDescriptor userIdPath = parameterWithName("userId").description("사용자의 식별자입니다.");
    public static final ParameterDescriptor todoListIdPath = parameterWithName("todoListId").description("할 일 리스트의 식별자입니다.");
    public static final ParameterDescriptor todoIdPath = parameterWithName("todoId").description("할 일의 식별자입니다.");

    public static class Member {
        public static class CreateUser {
            public static final FieldDescriptor userId = fieldWithPath("userId").description("회원가입 할 사용자의 아이디입니다.")
                    .attributes(key("constraint").value("32 글자를 초과할 수 없습니다."));
            public static final FieldDescriptor username = fieldWithPath("username").description("회원가입 할 사용자의 아이디입니다.")
                    .attributes(key("constraint").value("32 글자를 초과할 수 없습니다."));
            public static final FieldDescriptor password = fieldWithPath("password").description("회원가입 할 사용자의 비밀번호입니다.")
                    .attributes(key("constraint").value("64 글자를 초과할 수 없습니다."));
        }

        public static class UpdateUser {
            public static final FieldDescriptor updateField = fieldWithPath("field").description("수정할 필드입니다.")
                    .attributes(key("constraint").value("사용자 이름(USERNAME), 비밀번호(PASSWORD), 잠금 상태(LOCKED)만 가능합니다."));
            public static final FieldDescriptor updateValue = fieldWithPath("value").description("수정할 값입니다.")
                    .attributes(key("constraint").value("잠금 상태의 경우 Boolean 값으로 변환될 수 있는 true/false 값이어야 합니다."));
        }

        public static class LoginUser {
            public static final FieldDescriptor userId = fieldWithPath("userId").description("로그인 할 사용자의 아이디입니다.")
                    .attributes(key("constraint").value("32 글자를 초과할 수 없습니다."));
            public static final FieldDescriptor password = fieldWithPath("password").description("로그인 할 사용자의 비밀번호입니다.")
                    .attributes(key("constraint").value("32 글자를 초과할 수 없습니다."));
        }
    }

    public static class Team {
        public static class SearchTeam {
            public static final FieldDescriptor searchMode = fieldWithPath("field").description("팀 검색 방식입니다.")
                    .attributes(key("constraint").value("팀 이름(NAME), 팀장의 아이디(LEADER)만 가능합니다."));;
            public static final FieldDescriptor searchValue = fieldWithPath("value").description("검색 값입니다.");
            public static final FieldDescriptor includeJoined = fieldWithPath("joined").description("이미 가입한 팀을 검색 결과에 포함할 지 여부입니다.");
        }

        public static class UpdateTeam {
            public static final FieldDescriptor updateField = fieldWithPath("field").description("수정하려는 팀의 항목입니다.")
                    .attributes(key("constraint").value("팀 이름(TEAMNAME), 잠금 상태(LOCKED)만 가능합니다."));
            public static final FieldDescriptor updateValue = fieldWithPath("value").description("수정할 값입니다.")
                    .attributes(key("constraint").value("잠금 상태의 경우 Boolean 값으로 변환될 수 있는 true/false 값이어야 합니다."));
        }

        public static class CreateTeam {
            public static final FieldDescriptor teamName = fieldWithPath("teamName").description("생성하려는 팀의 이름입니다.")
                    .attributes(key("constraint").value("64 글자를 초과할 수 없습니다."));
        }
    }

    public static class Todo {
        public static class CreateTodo {
            public static final FieldDescriptor title = fieldWithPath("title").description("생성하는 할 일의 제목입니다.")
                    .attributes(key("constraint").value("64 글자를 초과할 수 없습니다."));
            public static final FieldDescriptor content = fieldWithPath("content").description("생성하는 할 일의 내용입니다.")
                    .attributes(key("constraint").value("255 글자를 초과할 수 없습니다."));
        }

        public static class UpdateTodo {
            public static final FieldDescriptor field = fieldWithPath("field").description("수정할 할 일의 항목입니다.")
                    .attributes(key("constraint").value("할 일의 제목(TITLE), 내용(CONTENT), 잠금 상태(LOCKED)만 가능합니다."));
            public static final FieldDescriptor value = fieldWithPath("value").description("수정될 값입니다.")
                    .attributes(key("constraint").value("잠금 상태의 경우 Boolean 값으로 변환될 수 있는 true/false 값이어야 합니다."));
        }
    }

    public static class TodoList {
        public static class CreateTodoList {
            public static final FieldDescriptor todoListName = fieldWithPath("name").description("생성할 할 일 리스트의 제목입니다.");
        }

        public static class UpdateTodoList {
            public static final FieldDescriptor field = fieldWithPath("field").description("수정할 할 일 리스트의 항목입니다.")
                    .attributes(key("constraint").value("할 일 리스트의 이름(NAME), 잠금 상태(LOCKED)만 가능합니다."));
            public static final FieldDescriptor value = fieldWithPath("value").description("수정될 값입니다.")
                    .attributes(key("constraint").value("잠금 상태의 경우 Boolean 값으로 변환될 수 있는 true/false 값이어야 합니다."));
        }
    }

    public static HeaderDescriptor authorization = headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰(JWT)을 담는 헤더입니다.");

}
