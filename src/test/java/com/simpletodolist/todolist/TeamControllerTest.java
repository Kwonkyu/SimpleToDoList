package com.simpletodolist.todolist;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpletodolist.todolist.Snippets.EntityDescriptor;
import com.simpletodolist.todolist.Snippets.RequestSnippets;
import com.simpletodolist.todolist.domain.bind.MemberDTO;
import com.simpletodolist.todolist.domain.bind.TeamDTO;
import com.simpletodolist.todolist.exception.team.NoTeamFoundException;
import com.simpletodolist.todolist.service.member.MemberService;
import com.simpletodolist.todolist.service.team.TeamService;
import com.simpletodolist.todolist.util.MemberTestMaster;
import com.simpletodolist.todolist.util.TeamTestMaster;
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

import java.util.List;
import java.util.Map;

import static com.simpletodolist.todolist.util.DocumentUtil.commonRequestPreprocessor;
import static com.simpletodolist.todolist.util.DocumentUtil.commonResponsePreprocessor;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
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

    MemberTestMaster memberTestMaster;
    TeamTestMaster teamTestMaster;


    @BeforeAll
    public void init() {
        memberTestMaster = new MemberTestMaster(memberService);
        teamTestMaster = new TeamTestMaster(teamService);
    }


    @Test
    @DisplayName("Search Teams")
    public void searchTeams() throws Exception {
        String keyword = "FIND_ME";
        MemberDTO.Response member1_3 = memberTestMaster.createNewMember();
        TeamDTO.Response team1 = teamTestMaster.createNewTeam(member1_3.getUserId(), "Hello World" + keyword);
        TeamDTO.Response team2 = teamTestMaster.createNewTeam(member1_3.getUserId(), "Cruel World" + keyword);
        TeamDTO.Response team3 = teamTestMaster.createNewTeam(member1_3.getUserId(), "Hello Cruel" + keyword);
        String member1_3Token = memberTestMaster.getRequestToken(member1_3.getUserId(), member1_3.getPassword());

        MemberDTO.Response member4_5 = memberTestMaster.createNewMember();
        TeamDTO.Response team4 = teamTestMaster.createNewTeam(member4_5.getUserId(), "Matrix: Reloaded" + keyword);
        TeamDTO.Response team5 = teamTestMaster.createNewTeam(member4_5.getUserId(), "Matrix: Revolution" + keyword);
        String member4_5Token = memberTestMaster.getRequestToken(member4_5.getUserId(), member4_5.getPassword());

        String requestContent = objectMapper.writeValueAsString(TeamDTO.SearchRequest.builder()
                .searchTeamField(TeamDTO.SearchRequest.SearchTeamField.NAME)
                .searchValue(keyword)
                .includeJoined(false)
                .build());

        // search teams except joined teams.
        MvcResult mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, member4_5Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent))
                .andExpect(status().isOk())
                .andDo(document("TeamController/searchTeam",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        requestFields(
                                RequestSnippets.Team.SearchTeam.searchMode,
                                RequestSnippets.Team.SearchTeam.searchValue,
                                RequestSnippets.Team.SearchTeam.includeJoined),
                        requestHeaders(
                                RequestSnippets.authorization),
                        responseFields(
                                EntityDescriptor.Team.teams
                        )))
                .andReturn();

        List<TeamDTO.Response> teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(3, teamsDTO.size());
        assertTrue(teamsDTO.stream().noneMatch(teamDTO -> teamDTO.getTeamName().equals(team4.getTeamName())));
        assertTrue(teamsDTO.stream().noneMatch(teamDTO -> teamDTO.getTeamName().equals(team5.getTeamName())));

        mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, member1_3Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent))
                .andExpect(status().isOk())
                .andReturn();

        teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});
        assertEquals(2, teamsDTO.size());
        assertTrue(teamsDTO.stream().noneMatch(teamDTO -> teamDTO.getTeamName().equals(team1.getTeamName())));
        assertTrue(teamsDTO.stream().noneMatch(teamDTO -> teamDTO.getTeamName().equals(team2.getTeamName())));
        assertTrue(teamsDTO.stream().noneMatch(teamDTO -> teamDTO.getTeamName().equals(team3.getTeamName())));


        requestContent = objectMapper.writeValueAsString(TeamDTO.SearchRequest.builder()
                .searchTeamField(TeamDTO.SearchRequest.SearchTeamField.NAME)
                .searchValue(keyword)
                .includeJoined(true)
                .build());

        // search teams include joined teams.
        mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, member4_5Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent))
                .andExpect(status().isOk())
                .andReturn();

        teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});
        assertEquals(5, teamsDTO.size());
        assertTrue(teamsDTO.stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team1.getTeamName())));
        assertTrue(teamsDTO.stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team2.getTeamName())));
        assertTrue(teamsDTO.stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team3.getTeamName())));
        assertTrue(teamsDTO.stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team4.getTeamName())));
        assertTrue(teamsDTO.stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team5.getTeamName())));

        mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, member1_3Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent))
                .andExpect(status().isOk())
                .andReturn();

        teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});
        assertEquals(5, teamsDTO.size());
        assertTrue(teamsDTO.stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team1.getTeamName())));
        assertTrue(teamsDTO.stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team2.getTeamName())));
        assertTrue(teamsDTO.stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team3.getTeamName())));
        assertTrue(teamsDTO.stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team4.getTeamName())));
        assertTrue(teamsDTO.stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team5.getTeamName())));


        // search by keyword.
        mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, member1_3Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TeamDTO.SearchRequest.builder()
                        .searchTeamField(TeamDTO.SearchRequest.SearchTeamField.NAME)
                        .searchValue("World")
                        .includeJoined(true)
                        .build())))
                .andExpect(status().isOk())
                .andReturn();

        teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});
        assertEquals(2, teamsDTO.size());
        assertTrue(teamsDTO.stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team1.getTeamName()))); // Hello World
        assertTrue(teamsDTO.stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team2.getTeamName()))); // Cruel World

        mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, member1_3Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TeamDTO.SearchRequest.builder()
                        .searchTeamField(TeamDTO.SearchRequest.SearchTeamField.NAME)
                        .searchValue("World")
                        .includeJoined(false)
                        .build())))
                .andExpect(status().isOk())
                .andReturn();

        teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});
        assertTrue(teamsDTO.isEmpty());


        mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, member4_5Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TeamDTO.SearchRequest.builder()
                        .searchTeamField(TeamDTO.SearchRequest.SearchTeamField.NAME)
                        .searchValue("Matrix")
                        .includeJoined(true)
                        .build())))
                .andExpect(status().isOk())
                .andReturn();

        teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});
        assertEquals(2, teamsDTO.size());
        assertTrue(teamsDTO.stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team4.getTeamName()))); // Matrix: Reloaded
        assertTrue(teamsDTO.stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team5.getTeamName()))); // Matrix: Revolution


        mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, member4_5Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TeamDTO.SearchRequest.builder()
                        .searchTeamField(TeamDTO.SearchRequest.SearchTeamField.NAME)
                        .searchValue("Matrix")
                        .includeJoined(false)
                        .build())))
                .andExpect(status().isOk())
                .andReturn();

        teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});
        assertTrue(teamsDTO.isEmpty());


        // search by leader.
        MemberDTO.Response joinMember = memberTestMaster.createNewMember();
        teamService.joinMember(team1.getId(), joinMember.getUserId());
        String joinToken = memberTestMaster.getRequestToken(joinMember.getUserId(), joinMember.getPassword());

        mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, joinToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TeamDTO.SearchRequest.builder()
                        .searchTeamField(TeamDTO.SearchRequest.SearchTeamField.LEADER)
                        .searchValue(member1_3.getUserId())
                        .includeJoined(true)
                        .build())))
                .andExpect(status().isOk())
                .andReturn();

        teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});
        assertEquals(3, teamsDTO.size());
        assertTrue(teamsDTO.stream().anyMatch(teamDTO -> teamDTO.getId() == team1.getId()));
        assertTrue(teamsDTO.stream().anyMatch(teamDTO -> teamDTO.getId() == team2.getId()));
        assertTrue(teamsDTO.stream().anyMatch(teamDTO -> teamDTO.getId() == team3.getId()));

        mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, joinToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TeamDTO.SearchRequest.builder()
                        .searchTeamField(TeamDTO.SearchRequest.SearchTeamField.LEADER)
                        .searchValue(member1_3.getUserId())
                        .includeJoined(false)
                        .build())))
                .andExpect(status().isOk())
                .andReturn();

        teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});
        assertEquals(2, teamsDTO.size());
        assertTrue(teamsDTO.stream().anyMatch(teamDTO -> teamDTO.getId() == team2.getId()));
        assertTrue(teamsDTO.stream().anyMatch(teamDTO -> teamDTO.getId() == team3.getId()));


        // search by leader not joined team.
        mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, joinToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TeamDTO.SearchRequest.builder()
                        .searchTeamField(TeamDTO.SearchRequest.SearchTeamField.LEADER)
                        .searchValue(member4_5.getUserId())
                        .includeJoined(true)
                        .build())))
                .andExpect(status().isOk())
                .andReturn();

        teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});
        assertEquals(2, teamsDTO.size());
        assertTrue(teamsDTO.stream().anyMatch(teamDTO -> teamDTO.getId() == team4.getId()));
        assertTrue(teamsDTO.stream().anyMatch(teamDTO -> teamDTO.getId() == team5.getId()));

        mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, joinToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TeamDTO.SearchRequest.builder()
                        .searchTeamField(TeamDTO.SearchRequest.SearchTeamField.LEADER)
                        .searchValue(member4_5.getUserId())
                        .includeJoined(false)
                        .build())))
                .andExpect(status().isOk())
                .andReturn();

        teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});
        assertEquals(2, teamsDTO.size());
        assertTrue(teamsDTO.stream().anyMatch(teamDTO -> teamDTO.getId() == team4.getId()));
        assertTrue(teamsDTO.stream().anyMatch(teamDTO -> teamDTO.getId() == team5.getId()));
    }


    @Test
    @DisplayName("Request Team Information")
    public void getTeam() throws Exception {
        MemberDTO.Response newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());
        TeamDTO.Response newTeam = teamTestMaster.createNewTeam(newMember.getUserId());

        // request without token.
        mockMvc.perform(get("/api/team/{teamId}", newTeam.getId()))
                .andExpect(status().isUnauthorized());

        // request not existing team.
        mockMvc.perform(get("/api/team/{teamId}", 123456789)
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isNotFound());

        // normal request.
        mockMvc.perform(get("/api/team/{teamId}", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(newTeam.getId()))
                .andExpect(jsonPath("$.teamName").value(newTeam.getTeamName()))
                .andDo(document("TeamController/getTeam",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        pathParameters(
                                RequestSnippets.teamIdPath),
                        requestHeaders(
                                RequestSnippets.authorization
                        ),
                        responseFields(
                                EntityDescriptor.Team.teamInformation
                        )));
    }

    @Test
    @DisplayName("Create Team")
    public void createTeam() throws Exception {
        MemberDTO.Response newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());

        // request without token.
        mockMvc.perform(post("/api/team")).andExpect(status().isUnauthorized());

        // request without mandatory values.
        mockMvc.perform(post("/api/team")
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        String teamName = "createdTeam";

        // normal request.
        mockMvc.perform(post("/api/team")
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TeamDTO.RegisterRequest.builder().teamName(teamName).build())))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("$.teamName").value(teamName))
                .andExpect(jsonPath("$.teamLeaderUserId").value(newMember.getUserId()))
                .andExpect(jsonPath("$.teamLeaderUsername").value(newMember.getUsername()))
                .andDo(document("TeamController/createTeam",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        requestHeaders(
                                RequestSnippets.authorization
                        ),
                        requestFields(
                                RequestSnippets.Team.CreateTeam.teamName
                        ),
                        responseFields(
                                EntityDescriptor.Team.teamInformation
                        )));
    }

    @Test
    @DisplayName("Update Team Information.")
    public void updateTeam() throws Exception {
        MemberDTO.Response newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());
        MemberDTO.Response anotherMember = memberTestMaster.createNewMember();
        String anotherToken = memberTestMaster.getRequestToken(anotherMember.getUserId(), anotherMember.getPassword());
        TeamDTO.Response newTeam = teamTestMaster.createNewTeam(newMember.getUserId());
        String updatedTeamName = "updatedTeam";

        // request without token.
        mockMvc.perform(patch("/api/team/{teamId}", newTeam.getId())).andExpect(status().isUnauthorized());

        // request without mandatory values.
        mockMvc.perform(patch("/api/team/{teamId}", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // request without authorization
        mockMvc.perform(patch("/api/team/{teamId}", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, anotherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TeamDTO.UpdateRequest.builder()
                        .field(TeamDTO.UpdateRequest.UpdatableTeamInformation.NAME)
                        .value(updatedTeamName)
                        .build())))
                .andExpect(status().isForbidden());

        // normal request.
        mockMvc.perform(patch("/api/team/{teamId}", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TeamDTO.UpdateRequest.builder()
                        .field(TeamDTO.UpdateRequest.UpdatableTeamInformation.NAME)
                        .value(updatedTeamName)
                        .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamName").value(updatedTeamName))
                .andDo(document("TeamController/updateTeam",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        pathParameters(
                                RequestSnippets.teamIdPath
                        ),
                        requestFields(
                                RequestSnippets.Team.UpdateTeam.updateField,
                                RequestSnippets.Team.UpdateTeam.updateValue
                        ),
                        requestHeaders(
                                RequestSnippets.authorization
                        ),
                        responseFields(
                                EntityDescriptor.Team.teamInformation
                        )));
    }

    @Test
    @DisplayName("Delete Team")
    public void deleteTeam() throws Exception {
        MemberDTO.Response newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());
        MemberDTO.Response anotherMember = memberTestMaster.createNewMember();
        String anotherToken = memberTestMaster.getRequestToken(anotherMember.getUserId(), anotherMember.getPassword());
        TeamDTO.Response newTeam = teamTestMaster.createNewTeam(newMember.getUserId());

        // request without token.
        mockMvc.perform(delete("/api/team/{teamId}", newTeam.getId())).andExpect(status().isUnauthorized());

        // request without authorization.
        mockMvc.perform(delete("/api/team/{teamId}", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, anotherToken))
                .andExpect(status().isForbidden());

        // normal request.
        mockMvc.perform(delete("/api/team/{teamId}", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isOk())
                .andDo(document("TeamController/deleteTeam",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        pathParameters(
                                RequestSnippets.teamIdPath
                        ),
                        requestHeaders(
                                RequestSnippets.authorization
                        )));

        // check if team is deleted.
        assertThrows(NoTeamFoundException.class, () -> teamService.getTeamDetails(newTeam.getId()));
    }
}
