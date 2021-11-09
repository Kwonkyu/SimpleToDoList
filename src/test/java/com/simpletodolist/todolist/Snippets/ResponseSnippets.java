package com.simpletodolist.todolist.Snippets;

import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.List;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;


public class ResponseSnippets {
    public static class ApiResponseDescriptor {
        public static final FieldDescriptor success = fieldWithPath("success").description("요청의 성공 여부를 나타냅니다.");
        public static final FieldDescriptor result = subsectionWithPath("result").description("요청의 결과값을 담고 있습니다.");
        public static final FieldDescriptor message = fieldWithPath("message").description("요청의 실행 메시지를 나타냅니다.");
        public static final FieldDescriptor token = fieldWithPath("message").description("해당 계정으로 발급된 JWT 값을 나타냅니다.");
        public static final List<FieldDescriptor> apiResponse = List.of(success, result, message);

    }
}
