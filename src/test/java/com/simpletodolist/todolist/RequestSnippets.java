package com.simpletodolist.todolist;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.headers.HeaderDocumentation;
import org.springframework.restdocs.headers.RequestHeadersSnippet;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.request.PathParametersSnippet;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;

public class RequestSnippets {
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

    public static final PathParametersSnippet userIdPathVariable = pathParameters(
            parameterWithName("userId").description("사용자의 아이디입니다."));

    public static final RequestFieldsSnippet teamId = requestFields(
            fieldWithPath("teamId").description("팀의 식별자입니다."));

    public static final PathParametersSnippet teamIdPathVariable = pathParameters(
            parameterWithName("teamId").description("팀의 식별자입니다."));

    public static final RequestFieldsSnippet teamName = requestFields(
            fieldWithPath("teamName").description("팀의 이름입니다."));

    public static final RequestFieldsSnippet updateTeam = requestFields(
            fieldWithPath("field").description("변경할 필드입니다. 팀의 이름(NAME)을 지원합니다."),
            fieldWithPath("value").description("변경할 값입니다."));
}
