package com.simpletodolist.todolist;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpletodolist.todolist.domain.UpdatableMemberInformation;
import com.simpletodolist.todolist.domain.dto.*;
import com.simpletodolist.todolist.exception.member.NoMemberFoundException;
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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
public class MemberControllerTest {

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

    String testToken;
    String anotherToken;


    @BeforeAll
    public void initUser() {
        memberService.registerMember(new MemberDTO(testUserId, testUsername, testPassword));
        testToken = String.format("Bearer %s", memberService.loginMember(testUserId, testPassword).getToken());

        memberService.registerMember(new MemberDTO(anotherUserId, anotherUsername, anotherPassword));
        anotherToken = String.format("Bearer %s", memberService.loginMember(anotherUserId, anotherPassword).getToken());
    }


    @Test
    @DisplayName("Get user information")
    public void getUserInfo() throws Exception {
        // request without token
        mockMvc.perform(get("/api/member"))
                .andExpect(status().isUnauthorized()); // by spring security, token-less request is not authorized.

        // normal request.
        MvcResult mvcResult = mockMvc.perform(get("/api/member")
                .header(HttpHeaders.AUTHORIZATION, testToken))
                .andExpect(status().isOk())
                // expression from https://goessner.net/articles/JsonPath/
                .andExpect(jsonPath("$.userId").value(testUserId))
                .andExpect(jsonPath("$.username").value(testUsername))
                .andDo(document("MemberController/readMemberInfo",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        RequestSnippets.authorization,
                        ResponseSnippets.memberInformation))
                .andReturn();
    }

    @Test
    @DisplayName("Update user information")
    public void updateInformation() throws Exception{
        // request without token
        mockMvc.perform(patch("/api/member"))
                .andExpect(status().isUnauthorized());

        // normal request.
        MvcResult mvcResult = mockMvc.perform(patch("/api/member")
                .header(HttpHeaders.AUTHORIZATION, anotherToken)
                .content(objectMapper.writeValueAsString(new MemberInformationUpdateRequestDTO(UpdatableMemberInformation.USERNAME, "newUsername")))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(anotherUserId))
                .andExpect(jsonPath("$.username").value("newUsername"))
                .andDo(document("MemberController/updateMemberInfo",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        RequestSnippets.updateUser,
                        RequestSnippets.authorization,
                        ResponseSnippets.memberInformation
                ))
                .andReturn();
    }

    @Test
    @DisplayName("Delete user")
    public void deleteUser() throws Exception {
        String deleteUserId = "deletemeid";
        String deleteUsername = "deletemename";
        String deletePassword = "deletemepw";
        memberService.registerMember(new MemberDTO(deleteUserId, deleteUsername, deletePassword));
        String deleteToken = String.format("Bearer %s", memberService.loginMember(deleteUserId, deletePassword).getToken());

        // request without token
        mockMvc.perform(delete("/api/member"))
                .andExpect(status().isUnauthorized());

        // normal request.
        MvcResult mvcResult = mockMvc.perform(delete("/api/member")
                .header(HttpHeaders.AUTHORIZATION, deleteToken))
                .andExpect(status().isOk())
                .andDo(document("MemberController/deleteMember",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        RequestSnippets.authorization))
                .andReturn();

        // check member information
        assertThrows(NoMemberFoundException.class, () -> memberService.getMemberDetails(deleteUserId));
    }

    @Test
    @DisplayName("Get teams of member.")
    public void getTeams() throws Exception {
        String teamName = "A-Team";
        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setTeamName(teamName);
        teamService.createTeam(testUserId, teamDTO);

        // request without token
        mockMvc.perform(get("/api/member/teams"))
                .andExpect(status().isUnauthorized());
        // TODO: 나중에 헤더가 없으면 unauthorized 대신 bad request로?

        // normal request.
        MvcResult mvcResult = mockMvc.perform(get("/api/member/teams")
                .header(HttpHeaders.AUTHORIZATION, testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teams").isArray())
                .andDo(document("MemberController/getMemberTeams",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        RequestSnippets.authorization,
                        ResponseSnippets.teamsInformation
                ))
                .andReturn();

        // check member information
        TeamsDTO teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TeamsDTO.class);
        assertTrue(teamsDTO.getTeams().stream().anyMatch(dto -> dto.getTeamName().equals(teamName)));
    }

    @Test
    @DisplayName("Join Team")
    public void joinTeam() throws Exception{
        String teamName = "B-Team";
        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setTeamName(teamName);
        TeamDTO team = teamService.createTeam(testUserId, teamDTO);

        // request without token
        mockMvc.perform(post("/api/member/teams"))
                .andExpect(status().isUnauthorized());

        // TODO: 잠긴 팀에 가입하려는 시도.

        // TODO: 이미 가입한 팀에 다시 가입하려는 시도.

        // normal request.
        MvcResult mvcResult = mockMvc.perform(post("/api/member/teams")
                .header(HttpHeaders.AUTHORIZATION, anotherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TeamIdRequestDTO(team.getId()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.members").isArray())
                .andDo(document("MemberController/joinTeam",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        RequestSnippets.authorization,
                        RequestSnippets.teamId,
                        ResponseSnippets.membersInformation
                ))
                .andReturn();

        // check member is joined.
        MembersDTO membersDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), MembersDTO.class);
        assertTrue(membersDTO.getMembers().stream().anyMatch(dto -> dto.getUserId().equals(anotherUserId)));
    }

    @Test
    @DisplayName("Quit Team")
    public void quitTeam() throws Exception{
        String teamName = "C-Team";
        TeamDTO teamDTO = new TeamDTO();

        teamDTO.setTeamName(teamName);
        TeamDTO team = teamService.createTeam(anotherUserId, teamDTO);
        teamService.joinMember(team.getId(), testUserId);

        teamDTO.setTeamName("anotherTeam");
        TeamDTO anotherTeam = teamService.createTeam(anotherUserId, teamDTO);

        // request without token.
        mockMvc.perform(delete("/api/member/teams/{teamId}", team.getId()))
                .andExpect(status().isUnauthorized());

        // withdraw from not joined team.
        mockMvc.perform(delete("/api/member/teams/{teamId}", anotherTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, testToken))
                .andExpect(status().isBadRequest());

        // normal request.
        MvcResult mvcResult = mockMvc.perform(delete("/api/member/teams/{teamId}", team.getId())
                .header(HttpHeaders.AUTHORIZATION, testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.members").isArray())
                .andDo(document("MemberController/quitTeam",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        RequestSnippets.authorization,
                        pathParameters(parameterWithName("teamId").description("팀의 식별자입니다.")),
                        ResponseSnippets.membersInformation
                ))
                .andReturn();

        // check member is quit.
        MembersDTO membersDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), MembersDTO.class);
        assertTrue(membersDTO.getMembers().stream().noneMatch(dto -> dto.getUserId().equals(testUserId)));
    }
}
