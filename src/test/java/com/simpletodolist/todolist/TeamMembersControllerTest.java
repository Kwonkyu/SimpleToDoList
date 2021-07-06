package com.simpletodolist.todolist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpletodolist.todolist.domain.dto.MemberDTO;
import com.simpletodolist.todolist.domain.dto.MemberJoinTeamDTO;
import com.simpletodolist.todolist.domain.dto.MembersDTO;
import com.simpletodolist.todolist.domain.dto.TeamDTO;
import com.simpletodolist.todolist.service.member.MemberService;
import com.simpletodolist.todolist.service.team.TeamService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// to use path parameter, use static methods of rest documentation request builders.
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TeamMembersControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    MemberService memberService;
    @Autowired
    TeamService teamService;
    @Autowired
    ObjectMapper objectMapper;


    String testUserId = "testuserid";
    String testUsername = "testusername";
    String testPassword = "testpassword";

    String anotherUserId = "anotheruserid";
    String anotherUsername = "anotherusername";
    String anotherPassword = "anotherpassword";

    String testTeamName = "testTeamName";
    long testTeamId;

    String anotherTeamName = "anotherTeamName";
    long anotherTeamId;

    String testToken;
    String anotherToken;

    @BeforeAll
    public void init() {
        memberService.registerMember(new MemberDTO(testUserId, testUsername, testPassword));
        // TODO: 이거 계속 까먹는데 테스트 전용 유틸 클래스를 만들어도 될듯.
        testToken = String.format("Bearer %s", memberService.loginMember(testUserId, testPassword).getToken());
        memberService.registerMember(new MemberDTO(anotherUserId, anotherUsername, anotherPassword));
        anotherToken = String.format("Bearer %s", memberService.loginMember(anotherUserId, anotherPassword).getToken());

        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setTeamName(testTeamName);
        testTeamId = teamService.createTeam(testUserId, teamDTO).getId();

        teamDTO.setTeamName(anotherTeamName);
        anotherTeamId = teamService.createTeam(anotherUserId, teamDTO).getId();
    }

    @Test
    @DisplayName("Get members of team")
    public void getMembersOfTeam() throws Exception {
        // request without token
        mockMvc.perform(get("/api/team/{teamId}/members", testTeamId))
                .andExpect(status().isUnauthorized());

        // normal request.
        MvcResult mvcResult = mockMvc.perform(get("/api/team/{teamId}/members", testTeamId)
                .header(HttpHeaders.AUTHORIZATION, testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.members").isArray())
                .andDo(document("TeamMembersController/getMembers",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        RequestSnippets.teamIdPathVariable,
                        RequestSnippets.authorization,
                        ResponseSnippets.membersInformation))
                .andReturn();

        // check result contains member.
        MembersDTO membersDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), MembersDTO.class);
        assertTrue(membersDTO.getMembers().stream().anyMatch(dto -> dto.getUserId().equals(testUserId)));
    }

    @Test
    @DisplayName("Join member to team")
    public void joinMember() throws Exception {
        // request without token
        mockMvc.perform(post("/api/team/{teamId}/members", testTeamId))
                .andExpect(status().isUnauthorized());

        // normal request.
        MvcResult mvcResult = mockMvc.perform(post("/api/team/{teamId}/members", testTeamId)
                .header(HttpHeaders.AUTHORIZATION, testToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new MemberJoinTeamDTO(anotherUserId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.members").isArray())
                .andDo(document("TeamMembersController/joinMember",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        RequestSnippets.teamIdPathVariable,
                        RequestSnippets.authorization,
                        RequestSnippets.userId,
                        ResponseSnippets.membersInformation))
                .andReturn();

        // check result contains member.
        MembersDTO membersDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), MembersDTO.class);
        assertTrue(membersDTO.getMembers().stream().anyMatch(dto -> dto.getUserId().equals(testUserId)));
        assertTrue(membersDTO.getMembers().stream().anyMatch(dto -> dto.getUserId().equals(anotherUserId)));
    }

    @Test
    @DisplayName("Withdraw member from team")
    public void withdrawMember() throws Exception {
        String etcUserId = "etcuserid";
        memberService.registerMember(new MemberDTO(etcUserId, "name", "pw"));

        String deletedUserId = "deleteme";
        memberService.registerMember(new MemberDTO(deletedUserId, "deleteusername", "deletepassword"));
        teamService.joinMember(testTeamId, deletedUserId);

        // request without token
        mockMvc.perform(delete("/api/team/{teamId}/members/{userId}", testTeamId, deletedUserId))
                .andExpect(status().isUnauthorized());

        // withdraw not joined member.
        mockMvc.perform(delete("/api/team/{teamId}/members/{userId}", testTeamId, etcUserId)
                .header(HttpHeaders.AUTHORIZATION, testToken))
                .andExpect(status().isBadRequest());

        // normal request.
        MvcResult mvcResult = mockMvc.perform(delete("/api/team/{teamId}/members/{userId}", testTeamId, deletedUserId)
                .header(HttpHeaders.AUTHORIZATION, testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.members").isArray())
                .andDo(document("TeamMembersController/deleteMember",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
// TODO: https://github.com/spring-projects/spring-restdocs/issues/285 any way to get around this?
//                        RequestSnippets.teamIdPathVariable,
//                        RequestSnippets.userIdPathVariable,
                        pathParameters(
                                parameterWithName("userId").description("사용자의 아이디입니다."),
                                parameterWithName("teamId").description("팀의 식별자입니다.")),
                        RequestSnippets.authorization,
                        ResponseSnippets.membersInformation))
                .andReturn();

        // check result contains member.
        MembersDTO membersDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), MembersDTO.class);
        assertTrue(membersDTO.getMembers().stream().noneMatch(dto -> dto.getUserId().equals(deletedUserId)));
    }

    @Test
    @DisplayName("Change team leader")
    public void changeLeader() throws Exception {
        String leaderUserId = "imleader";
        memberService.registerMember(new MemberDTO(leaderUserId, "leaderusername", "leaderpassword"));
        String leaderToken = memberService.loginMember(leaderUserId, "leaderpassword").getToken();
        teamService.joinMember(anotherTeamId, leaderUserId);

        // request without token
        mockMvc.perform(put("/api/team/{teamId}/leader/{userId}", anotherTeamId, leaderUserId))
                .andExpect(status().isUnauthorized());

        // try making not joined member as leader.
        mockMvc.perform(put("/api/team/{teamId}/leader/{userId}", anotherTeamId, "notexist")
                .header(HttpHeaders.AUTHORIZATION, anotherToken))
                .andExpect(status().isNotFound());

        // try change leader by unauthorized user.
        mockMvc.perform(put("/api/team/{teamId}/leader/{userId}", anotherTeamId, leaderUserId)
                .header(HttpHeaders.AUTHORIZATION, leaderToken))
                .andExpect(status().isUnauthorized());

        // try change leader of unauthorized team.
        mockMvc.perform(put("/api/team/{teamId}/leader/{userId}", testTeamId, leaderUserId)
                .header(HttpHeaders.AUTHORIZATION, anotherToken))
                .andExpect(status().isUnauthorized());

        // normal request.
        MvcResult mvcResult = mockMvc.perform(put("/api/team/{teamId}/leader/{userId}", anotherTeamId, leaderUserId)
                .header(HttpHeaders.AUTHORIZATION, anotherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamLeaderUserId").value(leaderUserId))
                .andDo(document("TeamMembersController/deleteMember",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
//                        RequestSnippets.teamIdPathVariable,
//                        RequestSnippets.userIdPathVariable,
                        pathParameters(
                                parameterWithName("userId").description("사용자의 아이디입니다."),
                                parameterWithName("teamId").description("팀의 식별자입니다.")),
                        RequestSnippets.authorization,
                        ResponseSnippets.teamInformation))
                .andReturn();
    }
}
