package com.simpletodolist.todolist;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpletodolist.todolist.Snippets.EntityResponseSnippets;
import com.simpletodolist.todolist.Snippets.RequestSnippets;
import com.simpletodolist.todolist.Snippets.ResponseSnippets;
import com.simpletodolist.todolist.common.bind.ApiResponse;
import com.simpletodolist.todolist.domains.team.bind.request.TeamInformationRequest;
import com.simpletodolist.todolist.domains.team.bind.request.TeamSearchRequest;
import com.simpletodolist.todolist.domains.member.bind.MemberDTO;
import com.simpletodolist.todolist.domains.team.bind.TeamDTO;
import com.simpletodolist.todolist.domains.team.exception.NoTeamFoundException;
import com.simpletodolist.todolist.domains.jwt.service.JwtService;
import com.simpletodolist.todolist.domains.member.service.BasicMemberService;
import com.simpletodolist.todolist.domains.team.service.BasicTeamService;
import com.simpletodolist.todolist.domains.todolist.service.BasicTodoListService;
import com.simpletodolist.todolist.util.MemberTestMaster;
import com.simpletodolist.todolist.util.TeamTestMaster;
import com.simpletodolist.todolist.util.TodoListTestMaster;
import com.simpletodolist.todolist.util.container.TestTeamDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
@Transactional
class TeamControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    BasicMemberService memberService;
    @Autowired
    BasicTodoListService todoListService;
    @Autowired
    BasicTeamService teamService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    JwtService jwtService;

    MemberTestMaster memberTestMaster;
    TeamTestMaster teamTestMaster;
    TodoListTestMaster todoListTestMaster;


    @BeforeEach
    void init() {
        memberTestMaster = new MemberTestMaster(memberService, jwtService);
        teamTestMaster = new TeamTestMaster(teamService);
        todoListTestMaster = new TodoListTestMaster(todoListService);
    }


    @Test
    @DisplayName("Search teams / except joined teams")
    void searchTeamsExceptJoinedTeams() throws Exception {
        String keyword = "FIND_ME";
        MemberDTO member1_3 = memberTestMaster.createNewMember();
        TeamDTO team1 = teamTestMaster.createNewTeam(member1_3.getUsername(), "Hello World" + keyword);
        TeamDTO team2 = teamTestMaster.createNewTeam(member1_3.getUsername(), "Cruel World" + keyword);
        TeamDTO team3 = teamTestMaster.createNewTeam(member1_3.getUsername(), "Hello Cruel" + keyword);
        String member1_3Token = memberTestMaster.getRequestToken(member1_3.getUsername());

        MemberDTO member4_5 = memberTestMaster.createNewMember();
        TeamDTO team4 = teamTestMaster.createNewTeam(member4_5.getUsername(), "Matrix: Reloaded" + keyword);
        TeamDTO team5 = teamTestMaster.createNewTeam(member4_5.getUsername(), "Matrix: Revolution" + keyword);
        String member4_5Token = memberTestMaster.getRequestToken(member4_5.getUsername());

        TeamSearchRequest request = new TeamSearchRequest();
        request.setSearchTeamField(TeamSearchRequest.SearchTeamField.NAME);
        request.setSearchValue(keyword);
        request.setIncludeJoined(false);

        // search teams except joined teams.
        MvcResult mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, member4_5Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
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
                                ResponseSnippets.ApiResponseDescriptor.success,
                                ResponseSnippets.ApiResponseDescriptor.result,
                                ResponseSnippets.ApiResponseDescriptor.message,
                                EntityResponseSnippets.Team.teams
                        )))
                .andReturn();

        ApiResponse<List<TestTeamDTO>> teams = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(3, teams.getResult().size());
        assertTrue(teams.getResult().stream()
                .anyMatch(teamDTO -> teamDTO.getTeamName().equals(team1.getTeamName())));
        assertTrue(teams.getResult().stream()
                .anyMatch(teamDTO -> teamDTO.getTeamName().equals(team2.getTeamName())));
        assertTrue(teams.getResult().stream()
                .anyMatch(teamDTO -> teamDTO.getTeamName().equals(team3.getTeamName())));

        mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, member1_3Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        teams = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(2, teams.getResult().size());
        assertTrue(teams.getResult().stream()
                .anyMatch(teamDTO -> teamDTO.getTeamName().equals(team4.getTeamName())));
        assertTrue(teams.getResult().stream()
                .anyMatch(teamDTO -> teamDTO.getTeamName().equals(team5.getTeamName())));
    }

    @Test
    @DisplayName("Search teams / include joined teams.")
    void searchTeamsIncludeJoinedTeams() throws Exception {
        String keyword = "FIND_ME";
        MemberDTO member1_3 = memberTestMaster.createNewMember();
        TeamDTO team1 = teamTestMaster.createNewTeam(member1_3.getUsername(), "Hello World" + keyword);
        TeamDTO team2 = teamTestMaster.createNewTeam(member1_3.getUsername(), "Cruel World" + keyword);
        TeamDTO team3 = teamTestMaster.createNewTeam(member1_3.getUsername(), "Hello Cruel" + keyword);
        String member1_3Token = memberTestMaster.getRequestToken(member1_3.getUsername());

        MemberDTO member4_5 = memberTestMaster.createNewMember();
        TeamDTO team4 = teamTestMaster.createNewTeam(member4_5.getUsername(), "Matrix: Reloaded" + keyword);
        TeamDTO team5 = teamTestMaster.createNewTeam(member4_5.getUsername(), "Matrix: Revolution" + keyword);
        String member4_5Token = memberTestMaster.getRequestToken(member4_5.getUsername());

        TeamSearchRequest request = new TeamSearchRequest();
        request.setSearchTeamField(TeamSearchRequest.SearchTeamField.NAME);
        request.setSearchValue(keyword);
        request.setIncludeJoined(true);

        // search teams include joined teams.
        MvcResult mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, member4_5Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<List<TestTeamDTO>> teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(5, teamsDTO.getResult().size());
        assertTrue(teamsDTO.getResult().stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team1.getTeamName())));
        assertTrue(teamsDTO.getResult().stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team2.getTeamName())));
        assertTrue(teamsDTO.getResult().stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team3.getTeamName())));
        assertTrue(teamsDTO.getResult().stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team4.getTeamName())));
        assertTrue(teamsDTO.getResult().stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team5.getTeamName())));

        mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, member1_3Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(5, teamsDTO.getResult().size());
        assertTrue(teamsDTO.getResult().stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team1.getTeamName())));
        assertTrue(teamsDTO.getResult().stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team2.getTeamName())));
        assertTrue(teamsDTO.getResult().stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team3.getTeamName())));
        assertTrue(teamsDTO.getResult().stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team4.getTeamName())));
        assertTrue(teamsDTO.getResult().stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team5.getTeamName())));
    }

    @Test
    @DisplayName("Search teams / by keyword / with joined.")
    void searchTeamsByKeywordWithJoined() throws Exception {
        String keyword = "FIND_ME";
        MemberDTO member1_3 = memberTestMaster.createNewMember();
        TeamDTO team1 = teamTestMaster.createNewTeam(member1_3.getUsername(), "Hello World" + keyword);
        TeamDTO team2 = teamTestMaster.createNewTeam(member1_3.getUsername(), "Cruel World" + keyword);
        TeamDTO team3 = teamTestMaster.createNewTeam(member1_3.getUsername(), "Hello Cruel" + keyword);
        String member1_3Token = memberTestMaster.getRequestToken(member1_3.getUsername());

        MemberDTO member4_5 = memberTestMaster.createNewMember();
        TeamDTO team4 = teamTestMaster.createNewTeam(member4_5.getUsername(), "Matrix: Reloaded" + keyword);
        TeamDTO team5 = teamTestMaster.createNewTeam(member4_5.getUsername(), "Matrix: Revolution" + keyword);
        String member4_5Token = memberTestMaster.getRequestToken(member4_5.getUsername());

        // search by keyword.
        TeamSearchRequest request = new TeamSearchRequest();
        request.setSearchTeamField(TeamSearchRequest.SearchTeamField.NAME);
        request.setSearchValue("World");
        request.setIncludeJoined(true);

        MvcResult mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, member1_3Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<List<TestTeamDTO>> teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(2, teamsDTO.getResult().size());
        assertTrue(teamsDTO.getResult().stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team1.getTeamName()))); // Hello World
        assertTrue(teamsDTO.getResult().stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team2.getTeamName()))); // Cruel World
        assertTrue(teamsDTO.getResult().stream().noneMatch(teamDTO -> teamDTO.getTeamName().equals(team3.getTeamName()))); // Cruel World

        request.setSearchValue("Matrix");

        mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, member4_5Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(2, teamsDTO.getResult().size());
        assertTrue(teamsDTO.getResult().stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team4.getTeamName()))); // Matrix: Reloaded
        assertTrue(teamsDTO.getResult().stream().anyMatch(teamDTO -> teamDTO.getTeamName().equals(team5.getTeamName()))); // Matrix: Revolution
    }

    @Test
    @DisplayName("Search teams / by keyword / without joined.")
    void searchTeamsByKeywordWithoutJoined() throws Exception {
        String keyword = "FIND_ME";
        MemberDTO member1_3 = memberTestMaster.createNewMember();
        teamTestMaster.createNewTeam(member1_3.getUsername(), "Hello World" + keyword);
        teamTestMaster.createNewTeam(member1_3.getUsername(), "Cruel World" + keyword);
        teamTestMaster.createNewTeam(member1_3.getUsername(), "Hello Cruel" + keyword);
        String member1_3Token = memberTestMaster.getRequestToken(member1_3.getUsername());

        MemberDTO member4_5 = memberTestMaster.createNewMember();
        teamTestMaster.createNewTeam(member4_5.getUsername(), "Matrix: Reloaded" + keyword);
        teamTestMaster.createNewTeam(member4_5.getUsername(), "Matrix: Revolution" + keyword);
        String member4_5Token = memberTestMaster.getRequestToken(member4_5.getUsername());

        // search by keyword.
        TeamSearchRequest request = new TeamSearchRequest();
        request.setSearchTeamField(TeamSearchRequest.SearchTeamField.NAME);
        request.setSearchValue("World");
        request.setIncludeJoined(false);

        MvcResult mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, member1_3Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<List<TestTeamDTO>> teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertTrue(teamsDTO.getResult().isEmpty());

        request.setSearchValue("Matrix");
        mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, member4_5Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertTrue(teamsDTO.getResult().isEmpty());
    }

    @Test
    @DisplayName("Search teams / by leader / with joined.")
    void searchTeamsByLeaderIncludeJoined() throws Exception {
        String keyword = "FIND_ME";
        MemberDTO member1_3 = memberTestMaster.createNewMember();
        TeamDTO team1 = teamTestMaster.createNewTeam(member1_3.getUsername(), "Hello World" + keyword);
        TeamDTO team2 = teamTestMaster.createNewTeam(member1_3.getUsername(), "Cruel World" + keyword);
        TeamDTO team3 = teamTestMaster.createNewTeam(member1_3.getUsername(), "Hello Cruel" + keyword);

        MemberDTO member4_5 = memberTestMaster.createNewMember();
        TeamDTO team4 = teamTestMaster.createNewTeam(member4_5.getUsername(), "Matrix: Reloaded" + keyword);
        TeamDTO team5 = teamTestMaster.createNewTeam(member4_5.getUsername(), "Matrix: Revolution" + keyword);


        // search by leader.
        MemberDTO joinMember = memberTestMaster.createNewMember();
        teamService.joinMember(team1.getId(), joinMember.getUsername());
        String joinToken = memberTestMaster.getRequestToken(joinMember.getUsername());

        TeamSearchRequest request = new TeamSearchRequest();
        request.setSearchTeamField(TeamSearchRequest.SearchTeamField.LEADER);
        request.setSearchValue(member1_3.getUsername());
        request.setIncludeJoined(true);

        MvcResult mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, joinToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<List<TestTeamDTO>> teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(3, teamsDTO.getResult().size());
        assertTrue(teamsDTO.getResult().stream().anyMatch(teamDTO -> teamDTO.getId() == team1.getId()));
        assertTrue(teamsDTO.getResult().stream().anyMatch(teamDTO -> teamDTO.getId() == team2.getId()));
        assertTrue(teamsDTO.getResult().stream().anyMatch(teamDTO -> teamDTO.getId() == team3.getId()));

        request.setSearchValue(member4_5.getUsername());

        mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, joinToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(2, teamsDTO.getResult().size());
        assertTrue(teamsDTO.getResult().stream().anyMatch(teamDTO -> teamDTO.getId() == team4.getId()));
        assertTrue(teamsDTO.getResult().stream().anyMatch(teamDTO -> teamDTO.getId() == team5.getId()));
    }

    @Test
    @DisplayName("Search teams / by leader / without joined.")
    void searchTeamsByLeaderWithoutJoined() throws Exception {
        String keyword = "FIND_ME";
        MemberDTO member1_3 = memberTestMaster.createNewMember();
        TeamDTO team1 = teamTestMaster.createNewTeam(member1_3.getUsername(), "Hello World" + keyword);
        TeamDTO team2 = teamTestMaster.createNewTeam(member1_3.getUsername(), "Cruel World" + keyword);
        TeamDTO team3 = teamTestMaster.createNewTeam(member1_3.getUsername(), "Hello Cruel" + keyword);

        MemberDTO member4_5 = memberTestMaster.createNewMember();
        TeamDTO team4 = teamTestMaster.createNewTeam(member4_5.getUsername(), "Matrix: Reloaded" + keyword);
        TeamDTO team5 = teamTestMaster.createNewTeam(member4_5.getUsername(), "Matrix: Revolution" + keyword);


        // search by leader.
        MemberDTO joinMember = memberTestMaster.createNewMember();
        teamService.joinMember(team1.getId(), joinMember.getUsername());
        String joinToken = memberTestMaster.getRequestToken(joinMember.getUsername());

        TeamSearchRequest request = new TeamSearchRequest();
        request.setSearchTeamField(TeamSearchRequest.SearchTeamField.LEADER);
        request.setSearchValue(member1_3.getUsername());
        request.setIncludeJoined(false);

        MvcResult mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, joinToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<List<TestTeamDTO>> teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(2, teamsDTO.getResult().size());
        assertTrue(teamsDTO.getResult().stream().anyMatch(teamDTO -> teamDTO.getId() == team2.getId()));
        assertTrue(teamsDTO.getResult().stream().anyMatch(teamDTO -> teamDTO.getId() == team3.getId()));

        request.setSearchValue(member4_5.getUsername());

        mvcResult = mockMvc.perform(get("/api/team")
                .header(HttpHeaders.AUTHORIZATION, joinToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(2, teamsDTO.getResult().size());
        assertTrue(teamsDTO.getResult().stream().anyMatch(teamDTO -> teamDTO.getId() == team4.getId()));
        assertTrue(teamsDTO.getResult().stream().anyMatch(teamDTO -> teamDTO.getId() == team5.getId()));
    }

    @Test
    @DisplayName("Request Team Information")
    void getTeam() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUsername());
        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUsername());
        todoListTestMaster.createNewTodoList(newMember.getUsername(), newTeam.getId());
        todoListTestMaster.createNewTodoList(newMember.getUsername(), newTeam.getId());

        // request without token.
        mockMvc.perform(get("/api/team/{teamId}", newTeam.getId()))
                .andExpect(status().isBadRequest());

        // request not existing team.
        mockMvc.perform(get("/api/team/{teamId}", 123456789)
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isNotFound());

        // normal request.
        mockMvc.perform(get("/api/team/{teamId}", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(newTeam.getId()))
                .andExpect(jsonPath("$.result.teamName").value(newTeam.getTeamName()))
                .andDo(document("TeamController/getTeam",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        pathParameters(
                                RequestSnippets.teamIdPath),
                        requestHeaders(
                                RequestSnippets.authorization
                        ),
                        responseFields(
                                ResponseSnippets.ApiResponseDescriptor.success,
                                ResponseSnippets.ApiResponseDescriptor.result,
                                ResponseSnippets.ApiResponseDescriptor.message,
                                EntityResponseSnippets.Team.id,
                                EntityResponseSnippets.Team.teamName,
                                EntityResponseSnippets.Team.leaderUsername,
                                EntityResponseSnippets.Team.leaderAlias,
                                EntityResponseSnippets.Team.todoLists,
                                EntityResponseSnippets.Team.locked)));
    }

    @Test
    @DisplayName("Create Team")
    void createTeam() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUsername());

        // request without token.
        mockMvc.perform(post("/api/team")).andExpect(status().isBadRequest());

        // request without mandatory values.
        mockMvc.perform(post("/api/team")
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // normal request.
        TeamInformationRequest request = new TeamInformationRequest();
        request.setTeamName("CREATED_TEAM");
        request.setLocked(false);

        MvcResult mvcResult = mockMvc.perform(post("/api/team")
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andDo(document("TeamController/createTeam",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        requestHeaders(
                                RequestSnippets.authorization
                        ),
                        requestFields(
                                RequestSnippets.Team.CreateTeam.teamName,
                                RequestSnippets.Team.CreateTeam.locked
                        ),
                        responseFields(
                                ResponseSnippets.ApiResponseDescriptor.success,
                                ResponseSnippets.ApiResponseDescriptor.result,
                                ResponseSnippets.ApiResponseDescriptor.message,
                                EntityResponseSnippets.Team.id,
                                EntityResponseSnippets.Team.teamName,
                                EntityResponseSnippets.Team.leaderUsername,
                                EntityResponseSnippets.Team.leaderAlias,
                                EntityResponseSnippets.Team.todoLists,
                                EntityResponseSnippets.Team.locked)))
                .andReturn();

        ApiResponse<TestTeamDTO> team = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(newMember.getUsername(), team.getResult().getLeaderUsername());
        assertEquals(newMember.getAlias(), team.getResult().getAlias());
        assertEquals(request.getTeamName(), team.getResult().getTeamName());
        assertTrue(team.getResult().getTodoLists().isEmpty());
        assertFalse(team.getResult().isLocked());
    }

    @Test
    @DisplayName("Update Team Information.")
    void updateTeam() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUsername());
        MemberDTO anotherMember = memberTestMaster.createNewMember();
        String anotherToken = memberTestMaster.getRequestToken(anotherMember.getUsername());
        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUsername());
        String updatedTeamName = "updatedTeam";

        // request without token.
        mockMvc.perform(put("/api/team/{teamId}", newTeam.getId())).andExpect(status().isBadRequest());

        // request without mandatory values.
        mockMvc.perform(put("/api/team/{teamId}", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // request without authorization
        TeamInformationRequest request = new TeamInformationRequest();
        request.setTeamName(updatedTeamName);
        request.setLocked(newTeam.isLocked());

        mockMvc.perform(put("/api/team/{teamId}", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, anotherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        // normal request.
        MvcResult mvcResult = mockMvc.perform(put("/api/team/{teamId}", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.teamName").value(updatedTeamName))
                .andDo(document("TeamController/updateTeam",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        pathParameters(
                                RequestSnippets.teamIdPath
                        ),
                        requestFields(
                                RequestSnippets.Team.UpdateTeam.teamName,
                                RequestSnippets.Team.UpdateTeam.locked
                        ),
                        requestHeaders(
                                RequestSnippets.authorization
                        ),
                        responseFields(
                                ResponseSnippets.ApiResponseDescriptor.success,
                                ResponseSnippets.ApiResponseDescriptor.result,
                                ResponseSnippets.ApiResponseDescriptor.message,
                                EntityResponseSnippets.Team.id,
                                EntityResponseSnippets.Team.teamName,
                                EntityResponseSnippets.Team.leaderUsername,
                                EntityResponseSnippets.Team.leaderAlias,
                                EntityResponseSnippets.Team.todoLists,
                                EntityResponseSnippets.Team.locked)))
                .andReturn();

        ApiResponse<TestTeamDTO> team = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(request.getTeamName(), team.getResult().getTeamName());
        assertEquals(request.isLocked(), team.getResult().isLocked());
    }

    @Test
    @DisplayName("Delete Team")
    void deleteTeam() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUsername());
        MemberDTO anotherMember = memberTestMaster.createNewMember();
        String anotherToken = memberTestMaster.getRequestToken(anotherMember.getUsername());
        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUsername());

        // request without token.
        mockMvc.perform(delete("/api/team/{teamId}", newTeam.getId())).andExpect(status().isBadRequest());

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
        long id = newTeam.getId();
        assertThrows(NoTeamFoundException.class, () -> teamService.readTeam(id));
    }
}
