package com.simpletodolist.todolist.Snippets;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;


public class ResponseSnippets {

    static class JWT {
        static final FieldDescriptor token = fieldWithPath("token").description("사용자의 인증 토큰(JWT)입니다.");
    }

}
