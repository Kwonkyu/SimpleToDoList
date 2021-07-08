package com.simpletodolist.todolist;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpletodolist.todolist.domain.UpdatableTeamInformation;
import com.simpletodolist.todolist.domain.dto.MemberDTO;
import com.simpletodolist.todolist.domain.dto.TeamDTO;
import com.simpletodolist.todolist.domain.dto.TeamInformationUpdateRequestDTO;
import com.simpletodolist.todolist.dto.request.TeamRegisterDTO;
import com.simpletodolist.todolist.exception.team.NoTeamFoundException;
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
public class TeamControllerTest {

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

    String testTeamName = "testTeamName";
    long testTeamId;
    String anotherTeamName = "anotherTeamName";
    long anotherTeamId;


    @BeforeAll
    public void initUser() {
        memberService.registerMember(new MemberDTO(testUserId, testUsername, testPassword));
        testToken = String.format("Bearer %s", memberService.loginMember(testUserId, testPassword).getToken());

        memberService.registerMember(new MemberDTO(anotherUserId, anotherUsername, anotherPassword));
        anotherToken = String.format("Bearer %s", memberService.loginMember(anotherUserId, anotherPassword).getToken());

        TeamDTO testTeamDTO = new TeamDTO();
        testTeamDTO.setTeamName(testTeamName);
        TeamDTO team = teamService.createTeam(testUserId, testTeamDTO);
        testTeamId = team.getId();
    }

    @Test
    @DisplayName("Request Team Information")
    public void getTeam() throws Exception {
        // request without token.
        mockMvc.perform(get("/api/team/{teamId}", testTeamId))
                .andExpect(status().isUnauthorized());

        // request not existing team.
        mockMvc.perform(get("/api/team/{teamId}", 123456789)
                .header(HttpHeaders.AUTHORIZATION, testToken))
                .andExpect(status().isNotFound());

        // normal request.
        mockMvc.perform(get("/api/team/{teamId}", testTeamId)
                .header(HttpHeaders.AUTHORIZATION, testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testTeamId))
                .andExpect(jsonPath("$.teamName").value(testTeamName))
                .andDo(document("TeamController/getTeam",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("teamId").description("팀의 식별자입니다.")),
                        RequestSnippets.authorization,
                        ResponseSnippets.teamInformation));
    }

    @Test
    @DisplayName("Register Team")
    public void registerTeam() throws Exception {
        // request without token.
        mockMvc.perform(post("/api/team")).andExpect(status().isUnauthorized());

        // request without mandatory values.
        mockMvc.perform(post("/api/team")
                .header(HttpHeaders.AUTHORIZATION, anotherToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        TeamRegisterDTO registerDTO = new TeamRegisterDTO(anotherTeamName);

        // normal request.
        mockMvc.perform(post("/api/team")
                .header(HttpHeaders.AUTHORIZATION, anotherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamName").value(anotherTeamName))
                .andExpect(jsonPath("$.teamLeaderUserId").value(anotherUserId))
                .andExpect(jsonPath("$.teamLeaderUsername").value(anotherUsername))
                .andDo(document("TeamController/createTeam",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        RequestSnippets.teamName,
                        RequestSnippets.authorization,
                        ResponseSnippets.teamInformation));
    }

    @Test
    @DisplayName("Update Team Information.")
    public void updateTeam() throws Exception {
        String updateTeamName = "updatedteamname";
        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setTeamName("updateme");
        TeamDTO team = teamService.createTeam(anotherUserId, teamDTO);
        long updateTeamId = team.getId();

        // request without token.
        mockMvc.perform(put("/api/team/{teamId}", updateTeamId)).andExpect(status().isUnauthorized());

        // request without mandatory values.
        mockMvc.perform(put("/api/team/{teamId}", updateTeamId)
                .header(HttpHeaders.AUTHORIZATION, anotherToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // normal request.
        mockMvc.perform(put("/api/team/{teamId}", updateTeamId)
                .header(HttpHeaders.AUTHORIZATION, anotherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TeamInformationUpdateRequestDTO(UpdatableTeamInformation.NAME, updateTeamName))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamName").value(updateTeamName))
                .andDo(document("TeamController/updateTeam",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("teamId").description("팀의 식별자입니다.")),
                        RequestSnippets.updateTeam,
                        RequestSnippets.authorization,
                        ResponseSnippets.teamInformation));
    }

    @Test
    @DisplayName("Delete Team")
    public void deleteTeam() throws Exception {
        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setTeamName("deleteme");
        TeamDTO team = teamService.createTeam(anotherUserId, teamDTO);
        long teamId = team.getId();

        // request without token.
        mockMvc.perform(delete("/api/team/{teamId}", teamId)).andExpect(status().isUnauthorized());

        // normal request.
        mockMvc.perform(delete("/api/team/{teamId}", teamId)
                .header(HttpHeaders.AUTHORIZATION, anotherToken))
                .andExpect(status().isOk())
                .andDo(document("TeamController/deleteTeam",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("teamId").description("팀의 식별자입니다.")),
                        RequestSnippets.authorization));

        // check if team is deleted.
        assertThrows(NoTeamFoundException.class, () -> teamService.getTeamDetails(teamId));
    }
}
