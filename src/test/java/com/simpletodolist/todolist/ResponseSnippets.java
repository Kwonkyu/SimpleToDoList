package com.simpletodolist.todolist;

import org.springframework.restdocs.payload.ResponseFieldsSnippet;

import static org.springframework.restdocs.payload.PayloadDocumentation.*;


public class ResponseSnippets {
    public static final ResponseFieldsSnippet memberInformation = responseFields(
            fieldWithPath("id").description("사용자의 식별값입니다."),
            fieldWithPath("userId").description("사용자의 아이디입니다."),
            fieldWithPath("username").description("사용자의 이름입니다."),
            fieldWithPath("password").description("사용자의 비밀번호입니다."));
    // TODO: add hypermedia links with links() like HATEOAS.

    public static final ResponseFieldsSnippet membersInformation = responseFields(
            subsectionWithPath("members").description("해당 팀의 팀원 목록입니다.")).and(
            fieldWithPath("members[].id").description("팀원의 식별자입니다."),
            fieldWithPath("members[].userId").description("팀원의 아이디입니다."),
            fieldWithPath("members[].username").description("팀원의 이름입니다."),
            fieldWithPath("members[].password").description("팀원의 비밀번호입니다."));

    public static final ResponseFieldsSnippet tokenInformation = memberInformation.and(
            fieldWithPath("token").description("사용자의 인증 토큰(JWT)입니다."));

    public static final ResponseFieldsSnippet teamInformation = responseFields(
            fieldWithPath("id").description("팀의 식별자입니다."),
            fieldWithPath("teamLeaderUserId").description("팀장의 회원 아이디입니다."),
            fieldWithPath("teamLeaderUsername").description("팀장의 회원 이름입니다."),
            fieldWithPath("teamName").description("팀의 이름입니다."),
            fieldWithPath("members").description("팀원의 목록입니다."),
            fieldWithPath("locked").description("팀의 잠금 상태입니다."));

    public static final ResponseFieldsSnippet teamsInformation = responseFields(
            subsectionWithPath("teams").description("팀의 목록입니다."),
            fieldWithPath("teams[].id").description("팀의 식별자입니다."),
            fieldWithPath("teams[].leaderUserId").description("팀장의 회원 아이디입니다."),
            fieldWithPath("teams[].leaderUsername").description("팀장의 회원 이름입니다."),
            fieldWithPath("teams[].teamName").description("팀의 이름입니다."),
            fieldWithPath("teams[].members[]").description("팀원의 아이디 목록입니다."),
            fieldWithPath("teams[].locked").description("팀의 잠금 상태입니다."));


}
