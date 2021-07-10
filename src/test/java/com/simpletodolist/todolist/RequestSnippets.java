package com.simpletodolist.todolist;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.headers.HeaderDocumentation;
import org.springframework.restdocs.headers.RequestHeadersSnippet;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.PathParametersSnippet;

import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;

public class RequestSnippets {
    // TODO: API 종류(컨트롤러)에 따라 스니펫을 분리해야 할듯.
    public static final RequestFieldsSnippet login = requestFields(
            fieldWithPath("userId").description("사용자의 아이디입니다."),
            fieldWithPath("password").description("사용자의 비밀번호입니다."));

    public static final RequestFieldsSnippet register = login.and(
            fieldWithPath("username").description("사용자의 이름입니다."));

    public static final RequestFieldsSnippet updateUser = requestFields(
            fieldWithPath("field").description("변경할 필드입니다. 사용자 이름(USERNAME), 비밀번호(PASSWORD)를 지원합니다."),
            fieldWithPath("value").description("변경할 값입니다."));

    public static RequestHeadersSnippet authorization = requestHeaders(
            headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer 토큰(JWT)을 담는 헤더입니다."));

    public static final RequestFieldsSnippet userId = requestFields(
            fieldWithPath("userId").description("사용자의 아이디입니다."));

    public static final RequestFieldsSnippet teamId = requestFields(
            fieldWithPath("teamId").description("팀의 식별자입니다."));

    public static final PathParametersSnippet teamIdPath = pathParameters(
            parameterWithName("teamId").description("팀의 식별자입니다."));

    public static final PathParametersSnippet teamIdAndTodoListIdPath = pathParameters(
            parameterWithName("teamId").description("팀의 식별자입니다."),
            parameterWithName("todoListId").description("할 일 리스트의 식별자입니다."));

    public static final RequestFieldsSnippet teamName = requestFields(
            fieldWithPath("teamName").description("팀의 이름입니다."));

    public static final RequestFieldsSnippet updateTeam = requestFields(
            fieldWithPath("field").description("변경할 필드입니다. 팀의 이름(NAME)을 지원합니다."),
            fieldWithPath("value").description("변경할 값입니다."));

    public static final RequestFieldsSnippet todo = requestFields(
            fieldWithPath("title").description("할 일의 제목입니다."),
            fieldWithPath("content").description("할 일의 내용입니다."));

    public static final RequestFieldsSnippet updateTodo = requestFields(
            fieldWithPath("field").description("변경할 필드입니다. 할 일의 제목(TITLE), 내용(CONTENT), 잠금(LOCKED)을 지원합니다."),
            fieldWithPath("value").description("변경할 값입니다. 잠금은 불리언(true/false)으로 변환될 수 있는 값을 지원합니다."));


    public static final RequestFieldsSnippet todoListName = requestFields(
            fieldWithPath("name").description("할 일 리스트의 이름입니다."));

    public static final RequestFieldsSnippet todoListUpdate = requestFields(
            fieldWithPath("field").description("변경할 필드입니다. 할 일 리스트의 이름(NAME), 잠금(LOCKED)을 지원합니다."),
            fieldWithPath("value").description("변경할 값입니다. 이름은 문자열, 잠금은 불리언(true/false)으로 변환될 수 있는 값을 지원합니다."));

    public static final PathParametersSnippet teamIdAndTodoListIdAndTodoIdPath = pathParameters(
            parameterWithName("teamId").description("팀의 식별자입니다."),
            parameterWithName("todoListId").description("할 일 리스트의 식별자입니다."),
            parameterWithName("todoId").description("할 일의 식별자입니다."));
}
