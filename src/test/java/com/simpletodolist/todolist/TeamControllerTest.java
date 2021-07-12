package com.simpletodolist.todolist;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpletodolist.todolist.controller.bind.MembersDTO;
import com.simpletodolist.todolist.controller.bind.TeamsDTO;
import com.simpletodolist.todolist.controller.bind.request.TeamSearchRequest;
import com.simpletodolist.todolist.controller.bind.request.field.SearchTeamField;
import com.simpletodolist.todolist.controller.bind.request.field.UpdatableTeamInformation;
import com.simpletodolist.todolist.controller.bind.MemberDTO;
import com.simpletodolist.todolist.controller.bind.TeamDTO;
import com.simpletodolist.todolist.controller.bind.request.TeamInformationUpdateRequest;
import com.simpletodolist.todolist.dto.request.TeamRegisterDTO;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

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
        MemberDTO member1_3 = memberTestMaster.createNewMember();
        TeamDTO team1 = teamTestMaster.createNewTeam(member1_3.getUserId(), "Hello World" + keyword);
        TeamDTO team2 = teamTestMaster.createNewTeam(member1_3.getUserId(), "Cruel World" + keyword);
        TeamDTO team3 = teamTestMaster.createNewTeam(member1_3.getUserId(), "Hello Cruel" + keyword);
        String member1_3Token = memberTestMaster.getRequestToken(member1_3.getUserId(), member1_3.getPassword());

        MemberDTO member4_5 = memberTestMaster.createNewMember();
        TeamDTO team4 = teamTestMaster.createNewTeam(member4_5.getUserId(), "Matrix: Reloaded" + keyword);
        TeamDTO team5 = teamTestMaster.createNewTeam(member4_5.getUserId(), "Matrix: Revolution" + keyword);
        String member4_5Token = memberTestMaster.getRequestToken(member4_5.getUserId(), member4_5.getPassword());

        // search teams except joined teams.
        MvcResult mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, member4_5Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TeamSearchRequest(SearchTeamField.TEAMNAME, keyword, false))))
                .andExpect(status().isOk())
                .andReturn();

        TeamsDTO teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TeamsDTO.class);
        assertEquals(3, teamsDTO.getTeams().size());
        assertTrue(teamsDTO.getTeams().stream().noneMatch(teamDTO -> teamDTO.getTeamName().equals(team4.getTeamName())));
        assertTrue(teamsDTO.getTeams().stream().noneMatch(teamDTO -> teamDTO.getTeamName().equals(team5.getTeamName())));

        mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, member1_3Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TeamSearchRequest(SearchTeamField.TEAMNAME, keyword, false))))
                .andExpect(status().isOk())
                .andReturn();

        teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TeamsDTO.class);
        assertEquals(2, teamsDTO.getTeams().size());
        assertTrue(teamsDTO.getTeams().stream().noneMatch(teamDTO -> teamDTO.getTeamName().equals(team1.getTeamName())));
        assertTrue(teamsDTO.getTeams().stream().noneMatch(teamDTO -> teamDTO.getTeamName().equals(team2.getTeamName())));
        assertTrue(teamsDTO.getTeams().stream().noneMatch(teamDTO -> teamDTO.getTeamName().equals(team3.getTeamName())));


        // search teams include joined teams.
        mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, member4_5Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TeamSearchRequest(SearchTeamField.TEAMNAME, keyword, true))))
                .andExpect(status().isOk())
                .andReturn();

        teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TeamsDTO.class);
        assertEquals(5, teamsDTO.getTeams().size());
        assertTrue(teamsDTO.getTeams().stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team1.getTeamName())));
        assertTrue(teamsDTO.getTeams().stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team2.getTeamName())));
        assertTrue(teamsDTO.getTeams().stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team3.getTeamName())));
        assertTrue(teamsDTO.getTeams().stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team4.getTeamName())));
        assertTrue(teamsDTO.getTeams().stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team5.getTeamName())));

        mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, member1_3Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TeamSearchRequest(SearchTeamField.TEAMNAME, keyword, true))))
                .andExpect(status().isOk())
                .andReturn();

        teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TeamsDTO.class);
        assertEquals(5, teamsDTO.getTeams().size());
        assertTrue(teamsDTO.getTeams().stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team1.getTeamName())));
        assertTrue(teamsDTO.getTeams().stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team2.getTeamName())));
        assertTrue(teamsDTO.getTeams().stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team3.getTeamName())));
        assertTrue(teamsDTO.getTeams().stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team4.getTeamName())));
        assertTrue(teamsDTO.getTeams().stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team5.getTeamName())));


        // search by keyword.
        mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, member1_3Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TeamSearchRequest(SearchTeamField.TEAMNAME, "World", true))))
                .andExpect(status().isOk())
                .andReturn();

        teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TeamsDTO.class);
        assertEquals(2, teamsDTO.getTeams().size());
        assertTrue(teamsDTO.getTeams().stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team1.getTeamName()))); // Hello World
        assertTrue(teamsDTO.getTeams().stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team2.getTeamName()))); // Cruel World

        mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, member1_3Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TeamSearchRequest(SearchTeamField.TEAMNAME, "World", false))))
                .andExpect(status().isOk())
                .andReturn();

        teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TeamsDTO.class);
        assertTrue(teamsDTO.getTeams().isEmpty());


        mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, member4_5Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TeamSearchRequest(SearchTeamField.TEAMNAME, "Matrix", true))))
                .andExpect(status().isOk())
                .andReturn();

        teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TeamsDTO.class);
        assertEquals(2, teamsDTO.getTeams().size());
        assertTrue(teamsDTO.getTeams().stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team4.getTeamName()))); // Matrix: Reloaded
        assertTrue(teamsDTO.getTeams().stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team5.getTeamName()))); // Matrix: Revolution


        mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, member4_5Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TeamSearchRequest(SearchTeamField.TEAMNAME, "Matrix", false))))
                .andExpect(status().isOk())
                .andReturn();

        teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TeamsDTO.class);
        assertTrue(teamsDTO.getTeams().isEmpty());


        // search by leader.
        MemberDTO joinMember = memberTestMaster.createNewMember();
        teamService.joinMember(team1.getId(), joinMember.getUserId());
        String joinToken = memberTestMaster.getRequestToken(joinMember.getUserId(), joinMember.getPassword());

        mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, joinToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TeamSearchRequest(SearchTeamField.LEADER, member1_3.getUserId(), true))))
                .andExpect(status().isOk())
                .andReturn();

        teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TeamsDTO.class);
        assertEquals(3, teamsDTO.getTeams().size());
        assertTrue(teamsDTO.getTeams().stream().anyMatch(teamDTO -> teamDTO.getId() == team1.getId()));
        assertTrue(teamsDTO.getTeams().stream().anyMatch(teamDTO -> teamDTO.getId() == team2.getId()));
        assertTrue(teamsDTO.getTeams().stream().anyMatch(teamDTO -> teamDTO.getId() == team3.getId()));

        mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, joinToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TeamSearchRequest(SearchTeamField.LEADER, member1_3.getUserId(), false))))
                .andExpect(status().isOk())
                .andReturn();

        teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TeamsDTO.class);
        assertEquals(2, teamsDTO.getTeams().size());
        assertTrue(teamsDTO.getTeams().stream().anyMatch(teamDTO -> teamDTO.getId() == team2.getId()));
        assertTrue(teamsDTO.getTeams().stream().anyMatch(teamDTO -> teamDTO.getId() == team3.getId()));


        // search by leader not joined team.
        mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, joinToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TeamSearchRequest(SearchTeamField.LEADER, member4_5.getUserId(), true))))
                .andExpect(status().isOk())
                .andReturn();

        teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TeamsDTO.class);
        assertEquals(2, teamsDTO.getTeams().size());
        assertTrue(teamsDTO.getTeams().stream().anyMatch(teamDTO -> teamDTO.getId() == team4.getId()));
        assertTrue(teamsDTO.getTeams().stream().anyMatch(teamDTO -> teamDTO.getId() == team5.getId()));

        mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, joinToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TeamSearchRequest(SearchTeamField.LEADER, member4_5.getUserId(), false))))
                .andExpect(status().isOk())
                .andReturn();

        teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TeamsDTO.class);
        assertEquals(2, teamsDTO.getTeams().size());
        assertTrue(teamsDTO.getTeams().stream().anyMatch(teamDTO -> teamDTO.getId() == team4.getId()));
        assertTrue(teamsDTO.getTeams().stream().anyMatch(teamDTO -> teamDTO.getId() == team5.getId()));
    }


    @Test
    @DisplayName("Request Team Information")
    public void getTeam() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());
        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUserId());

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
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        RequestSnippets.teamIdPath,
                        RequestSnippets.authorization,
                        ResponseSnippets.teamInformation));
    }

    @Test
    @DisplayName("Create Team")
    public void createTeam() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());

        // request without token.
        mockMvc.perform(post("/api/team")).andExpect(status().isUnauthorized());

        // request without mandatory values.
        mockMvc.perform(post("/api/team")
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        String teamName = "createdTeam";
        TeamRegisterDTO registerDTO = new TeamRegisterDTO(teamName);

        // normal request.
        mockMvc.perform(post("/api/team")
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("$.teamName").value(teamName))
                .andExpect(jsonPath("$.teamLeaderUserId").value(newMember.getUserId()))
                .andExpect(jsonPath("$.teamLeaderUsername").value(newMember.getUsername()))
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
        MemberDTO newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());
        MemberDTO anotherMember = memberTestMaster.createNewMember();
        String anotherToken = memberTestMaster.getRequestToken(anotherMember.getUserId(), anotherMember.getPassword());
        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUserId());
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
                .content(objectMapper.writeValueAsString(new TeamInformationUpdateRequest(UpdatableTeamInformation.NAME, updatedTeamName))))
                .andExpect(status().isForbidden());

        // normal request.
        mockMvc.perform(patch("/api/team/{teamId}", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TeamInformationUpdateRequest(UpdatableTeamInformation.NAME, updatedTeamName))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamName").value(updatedTeamName))
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
        MemberDTO newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());
        MemberDTO anotherMember = memberTestMaster.createNewMember();
        String anotherToken = memberTestMaster.getRequestToken(anotherMember.getUserId(), anotherMember.getPassword());
        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUserId());

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
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("teamId").description("팀의 식별자입니다.")),
                        RequestSnippets.authorization));

        // check if team is deleted.
        assertThrows(NoTeamFoundException.class, () -> teamService.getTeamDetails(newTeam.getId()));
    }
}
