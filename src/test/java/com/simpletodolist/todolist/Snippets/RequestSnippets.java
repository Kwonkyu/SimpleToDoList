package com.simpletodolist.todolist.Snippets;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;

public class RequestSnippets {
    public static final ParameterDescriptor teamIdPath = parameterWithName("teamId").description("팀의 식별자입니다.");
    public static final ParameterDescriptor userIdPath = parameterWithName("userId").description("사용자의 식별자입니다.");
    public static final ParameterDescriptor todoListIdPath = parameterWithName("todoListId").description("할 일 리스트의 식별자입니다.");
    public static final ParameterDescriptor todoIdPath = parameterWithName("todoId").description("할 일의 식별자입니다.");

    public static class Member {
        public static class CreateUser {
            public static final FieldDescriptor userId = fieldWithPath("userId").description("회원가입 할 사용자의 아이디입니다.");
            public static final FieldDescriptor username = fieldWithPath("username").description("회원가입 할 사용자의 아이디입니다.");
            public static final FieldDescriptor password = fieldWithPath("password").description("회원가입 할 사용자의 비밀번호입니다.");
        }

        public static class UpdateUser {
            // TODO: add constraint field
            public static final FieldDescriptor updateField = fieldWithPath("field").description("변경할 필드입니다. 사용자 이름(USERNAME), 비밀번호(PASSWORD)를 지원합니다.");
            public static final FieldDescriptor updateValue = fieldWithPath("value").description("변경할 값입니다.");
        }

        public static class LoginUser {
            public static final FieldDescriptor userId = fieldWithPath("userId").description("로그인 할 사용자의 아이디입니다.");
            public static final FieldDescriptor password = fieldWithPath("password").description("로그인 할 사용자의 비밀번호입니다.");
        }
    }

    public static class Team {
        public static class SearchTeam {
            public static final FieldDescriptor searchMode = fieldWithPath("field").description("팀 검색 방식입니다.");
            public static final FieldDescriptor searchValue = fieldWithPath("value").description("검색 값입니다.");
            public static final FieldDescriptor includeJoined = fieldWithPath("joined").description("이미 가입한 팀을 검색 결과에 포함할 지 여부입니다.");
        }

        public static class UpdateTeam {
            public static final FieldDescriptor updateField = fieldWithPath("field").description("수정하려는 팀의 항목입니다.");
            public static final FieldDescriptor updateValue = fieldWithPath("value").description("수정할 값입니다.");
        }

        public static class CreateTeam {
            public static final FieldDescriptor teamName = fieldWithPath("teamName").description("생성하려는 팀의 이름입니다.");
        }
    }

    public static class Todo {
        public static class CreateTodo {
            public static final FieldDescriptor title = fieldWithPath("title").description("생성하는 할 일의 제목입니다.");
            public static final FieldDescriptor content = fieldWithPath("content").description("생성하는 할 일의 내용입니다.");
        }

        public static class UpdateTodo {
            public static final FieldDescriptor field = fieldWithPath("field").description("수정할 할 일의 항목입니다.");
            public static final FieldDescriptor value = fieldWithPath("value").description("수정될 값입니다.");
            // TODO: append constraints 할 일의 제목(TITLE), 내용(CONTENT), 잠금(LOCKED)을 지원합니다. to field.
        }
    }

    public static class TodoList {
        public static class CreateTodoList {
            public static final FieldDescriptor todoListName = fieldWithPath("name").description("생성할 할 일 리스트의 제목입니다.");
        }

        public static class UpdateTodoList {
            public static final FieldDescriptor field = fieldWithPath("field").description("수정할 할 일 리스트의 항목입니다.");
            public static final FieldDescriptor value = fieldWithPath("value").description("수정될 값입니다.");
            // TODO: 수정 가능 항목 constraint.
        }
    }

    public static HeaderDescriptor authorization = headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰(JWT)을 담는 헤더입니다.");

}
