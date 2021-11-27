package com.simpletodolist.todolist.Snippets;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;

public class EntityResponseSnippets {
public static class JWT {
    public static final FieldDescriptor accessToken = fieldWithPath("result.accessToken")
            .description("JWT Access Token 입니다. 이를 이용하여 리소스에 접근할 수 있습니다.");
    public static final FieldDescriptor refreshToken = fieldWithPath("result.refreshToken")
            .description("JWT Refresh Token 입니다. 이를 이용하여 새로운 Access Token을 생성할 수 있습니다.");
}

    public static class Team {
        // https://stackoverflow.com/questions/31162140/how-to-document-top-level-array-as-response-payload-with-spring-rest-docs
        public static final FieldDescriptor id = fieldWithPath("result.id").description("팀의 식별자입니다.");
        public static final FieldDescriptor leaderUsername = fieldWithPath("result.leaderUsername").description("팀장의 아이디입니다.");
        public static final FieldDescriptor leaderAlias = fieldWithPath("result.leaderAlias").description("팀장의 이름입니다.");
        public static final FieldDescriptor teamName = fieldWithPath("result.teamName").description("팀의 이름입니다.");
        public static final FieldDescriptor todoLists = subsectionWithPath("result.todoLists").description("팀의 할 일 리스트 목록입니다.");
        public static final FieldDescriptor locked = fieldWithPath("result.locked").description("팀의 잠금 상태입니다.");
        public static final FieldDescriptor teams = subsectionWithPath("result.[]").description("팀의 목록입니다.");
    }

    public static class Member {
        public static final FieldDescriptor id = fieldWithPath("result.id").description("사용자의 식별값입니다.");
        public static final FieldDescriptor username = fieldWithPath("result.username").description("사용자의 아이디입니다.");
        public static final FieldDescriptor alias = fieldWithPath("result.alias").description("사용자의 이름입니다.");
        public static final FieldDescriptor password = fieldWithPath("result.password").description("사용자의 비밀번호입니다. 암호화되어 \"ENCRYPTED\" 문자열로 반환됩니다.");
        public static final FieldDescriptor locked = fieldWithPath("result.locked").description("사용자의 잠금 상태입니다.");
        public static final FieldDescriptor members = subsectionWithPath("result.[]").description("사용자의 목록입니다.");
    }

    public static class Todo {
        public static final FieldDescriptor id = fieldWithPath("result.id").description("할 일의 식별자입니다.");
        public static final FieldDescriptor username = fieldWithPath("result.username").description("할 일의 작성자의 아이디입니다.");
        public static final FieldDescriptor alias = fieldWithPath("result.alias").description("할 일의 작성자의 이름입니다.");
        public static final FieldDescriptor title = fieldWithPath("result.title").description("할 일의 제목입니다.");
        public static final FieldDescriptor content = fieldWithPath("result.content").description("할 일의 내용입니다.");
        public static final FieldDescriptor locked = fieldWithPath("result.locked").description("할 일의 잠금 상태입니다.");
        public static final FieldDescriptor todos = subsectionWithPath("result.[]").description("할 일의 목록입니다.");
    }

    public static class TodoList {
        public static final FieldDescriptor id = fieldWithPath("result.id").description("할 일 리스트의 식별자입니다.");
        public static final FieldDescriptor username = fieldWithPath("result.username").description("할 일 리스트를 생성한 회원의 아이디입니다.");
        public static final FieldDescriptor alias = fieldWithPath("result.alias").description("할 일 리스트를 생성한 회원의 이름입니다.");
        public static final FieldDescriptor name = fieldWithPath("result.name").description("할 일 리스트의 이름입니다.");
        public static final FieldDescriptor todos = subsectionWithPath("result.todos").description("할 일의 목록입니다.");
        public static final FieldDescriptor locked = fieldWithPath("result.locked").description("할 일 리스트의 잠금 상태입니다.");
        public static final FieldDescriptor todoLists = subsectionWithPath("result.[]").description("할 일 리스트의 목록입니다.");
    }
}
