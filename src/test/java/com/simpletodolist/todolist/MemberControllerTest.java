package com.simpletodolist.todolist;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpletodolist.todolist.Snippets.EntityResponseSnippets;
import com.simpletodolist.todolist.Snippets.RequestSnippets;
import com.simpletodolist.todolist.Snippets.ResponseSnippets;
import com.simpletodolist.todolist.common.bind.ApiResponse;
import com.simpletodolist.todolist.domains.member.bind.request.MemberUpdateRequest;
import com.simpletodolist.todolist.domains.team.bind.request.TeamInformationRequest;
import com.simpletodolist.todolist.domains.member.bind.MemberDTO;
import com.simpletodolist.todolist.domains.team.bind.TeamDTO;
import com.simpletodolist.todolist.domains.todolist.bind.TodoListDTO;
import com.simpletodolist.todolist.domains.member.exception.NoMemberFoundException;
import com.simpletodolist.todolist.domains.jwt.service.JwtService;
import com.simpletodolist.todolist.domains.member.service.BasicMemberService;
import com.simpletodolist.todolist.domains.team.service.BasicTeamService;
import com.simpletodolist.todolist.domains.todo.service.BasicTodoService;
import com.simpletodolist.todolist.domains.todolist.service.BasicTodoListService;
import com.simpletodolist.todolist.util.MemberTestMaster;
import com.simpletodolist.todolist.util.TeamTestMaster;
import com.simpletodolist.todolist.util.TodoListTestMaster;
import com.simpletodolist.todolist.util.TodoTestMaster;
import com.simpletodolist.todolist.util.container.TestTeamDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ActiveProfiles("test")
@Transactional
class MemberControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    BasicMemberService memberService;
    @Autowired
    BasicTodoService todoService;
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
    TodoTestMaster todoTestMaster;
    TodoListTestMaster todoListTestMaster;

    @BeforeEach
    void init() {
        memberTestMaster = new MemberTestMaster(memberService, jwtService);
        teamTestMaster = new TeamTestMaster(teamService);
        todoTestMaster = new TodoTestMaster(todoService);
        todoListTestMaster = new TodoListTestMaster(todoListService);
    }

    @Test
    @DisplayName("Get user information")
    void getUserInfo() throws Exception {
        // request without token
        mockMvc.perform(get("/api/member"))
                .andExpect(status().isBadRequest()); // by spring security, token-less request is not authorized.

        // generate dummy data for documentation.
        MemberDTO newMember = memberTestMaster.createNewMember();
        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUsername());
        String requestToken = memberTestMaster.getRequestToken(newMember.getUsername());
        TodoListDTO newTodoList = todoListTestMaster.createNewTodoList(newMember.getUsername(), newTeam.getId());
        todoTestMaster.createNewTodo(newMember.getUsername(), newTeam.getId(), newTodoList.getId(), false);

        // normal request.
        mockMvc.perform(get("/api/member")
                .header(HttpHeaders.AUTHORIZATION, requestToken))
                .andExpect(status().isOk())
                // expression from https://goessner.net/articles/JsonPath/
                .andExpect(jsonPath("$.result.username").value(newMember.getUsername()))
                .andExpect(jsonPath("$.result.alias").value(newMember.getAlias()))
                .andExpect(jsonPath("$.result.id").value(newMember.getId()))
                .andExpect(jsonPath("$.result.locked").value(newMember.isLocked()))
                .andExpect(jsonPath("$.result.password").value("ENCRYPTED"))
                .andDo(document("MemberController/readMemberInfo",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        requestHeaders(
                                RequestSnippets.authorization
                        ),
                        responseFields(
                                ResponseSnippets.ApiResponseDescriptor.success,
                                ResponseSnippets.ApiResponseDescriptor.result,
                                ResponseSnippets.ApiResponseDescriptor.message,
                                EntityResponseSnippets.Member.id,
                                EntityResponseSnippets.Member.username,
                                EntityResponseSnippets.Member.alias,
                                EntityResponseSnippets.Member.password,
                                EntityResponseSnippets.Member.locked)));
    }

    @Test
    @DisplayName("Update user information")
    void updateInformation() throws Exception {
        // request without token
        mockMvc.perform(patch("/api/member"))
                .andExpect(status().isBadRequest());

        MemberDTO newMember = memberTestMaster.createNewMember();
        String requestToken = memberTestMaster.getRequestToken(newMember.getUsername());

        MemberUpdateRequest request = new MemberUpdateRequest();
        request.setAlias("UPDATED_ALIAS");
        request.setPassword("UPDATED_PASSWORD");

        // normal request.
        mockMvc.perform(patch("/api/member")
                .header(HttpHeaders.AUTHORIZATION, requestToken)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.username").value(newMember.getUsername()))
                .andExpect(jsonPath("$.result.alias").value(request.getAlias()))
                .andDo(document("MemberController/updateMemberInfo",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        requestHeaders(
                                RequestSnippets.authorization
                        ),
                        requestFields(
                                RequestSnippets.Member.alias,
                                RequestSnippets.Member.password
                        ),
                        responseFields(
                                ResponseSnippets.ApiResponseDescriptor.success,
                                ResponseSnippets.ApiResponseDescriptor.result,
                                ResponseSnippets.ApiResponseDescriptor.message,
                                EntityResponseSnippets.Member.id,
                                EntityResponseSnippets.Member.username,
                                EntityResponseSnippets.Member.alias,
                                EntityResponseSnippets.Member.password,
                                EntityResponseSnippets.Member.locked)));

        assertDoesNotThrow(() -> memberService.authenticateMember(newMember.getUsername(), request.getPassword()));
    }

    @Test
    @DisplayName("Delete user")
    void deleteUser() throws Exception {
        // request without token
        mockMvc.perform(delete("/api/member"))
                .andExpect(status().isBadRequest());

        MemberDTO newMember = memberTestMaster.createNewMember();
        String requestToken = memberTestMaster.getRequestToken(newMember.getUsername());

        // normal request.
        mockMvc.perform(delete("/api/member")
                .header(HttpHeaders.AUTHORIZATION, requestToken))
                .andExpect(status().isOk())
                .andDo(document("MemberController/deleteMember",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        requestHeaders(
                                RequestSnippets.authorization)));

        // check member information
        String username = newMember.getUsername();
        assertThrows(NoMemberFoundException.class, () -> memberService.getMemberDetails(username));
    }

    @Test
    @DisplayName("Get teams of member.")
    void getTeams() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUsername());

        MemberDTO anotherMember = memberTestMaster.createNewMember();
        String anotherRequestToken = memberTestMaster.getRequestToken(anotherMember.getUsername());
        teamService.joinMember(newTeam.getId(), anotherMember.getUsername());

        // request without token
        mockMvc.perform(get("/api/member/teams"))
                .andExpect(status().isBadRequest());

        // normal request.
        MvcResult mvcResult = mockMvc.perform(get("/api/member/teams")
                .header(HttpHeaders.AUTHORIZATION, anotherRequestToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").isArray())
                .andDo(document("MemberController/getMemberTeams",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        requestHeaders(
                                RequestSnippets.authorization
                        ),
                        responseFields(
                                ResponseSnippets.ApiResponseDescriptor.success,
                                ResponseSnippets.ApiResponseDescriptor.result,
                                ResponseSnippets.ApiResponseDescriptor.message,
                                EntityResponseSnippets.Team.teams
                        )))
                .andReturn();

        ApiResponse<List<TestTeamDTO>> teams = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(1, teams.getResult().size());
        assertEquals(newTeam.getTeamName(), teams.getResult().get(0).getTeamName());
        assertEquals(newTeam.getLeaderUsername(), teams.getResult().get(0).getLeaderUsername());
        assertEquals(newTeam.getLeaderAlias(), teams.getResult().get(0).getAlias());
        assertTrue(teams.getResult().get(0).getTodoLists().isEmpty());
        assertFalse(teams.getResult().get(0).isLocked());
    }

    @Test
    @DisplayName("Join Team")
    void joinTeam() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        String requestToken = memberTestMaster.getRequestToken(newMember.getUsername());
        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUsername());

        MemberDTO anotherMember = memberTestMaster.createNewMember();
        String anotherRequestToken = memberTestMaster.getRequestToken(anotherMember.getUsername());

        // request without token
        mockMvc.perform(put("/api/member/teams/{teamId}", newTeam.getId()))
                .andExpect(status().isBadRequest());


        // try to join already joined team.
        mockMvc.perform(put("/api/member/teams/{teamId}", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, requestToken))
                .andExpect(status().isBadRequest());

        TeamInformationRequest request = new TeamInformationRequest();
        request.setTeamName(newTeam.getTeamName());
        request.setLocked(true);
        teamService.updateTeam(newTeam.getId(), request);

        // try to join locked team.
        mockMvc.perform(put("/api/member/teams/{teamId}", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, anotherRequestToken))
                .andExpect(status().isForbidden());

        request.setLocked(false);
        teamService.updateTeam(newTeam.getId(), request);

        // normal request.
        mockMvc.perform(put("/api/member/teams/{teamId}", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, anotherRequestToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(newTeam.getId()))
                .andDo(document("MemberController/joinTeam",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        requestHeaders(
                                RequestSnippets.authorization
                        ),
                        pathParameters(
                                RequestSnippets.teamIdPath
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
                                EntityResponseSnippets.Team.locked
                        )));

        // check member is joined.
        assertTrue(teamService.getMembers(newTeam.getId()).stream().anyMatch(response -> response.getUsername().equals(anotherMember.getUsername())));
    }

    @Test
    @DisplayName("Quit Team")
    void quitTeam() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        String requestToken = memberTestMaster.getRequestToken(newMember.getUsername());
        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUsername());

        MemberDTO anotherMember = memberTestMaster.createNewMember();
        String anotherRequestToken = memberTestMaster.getRequestToken(anotherMember.getUsername());
        teamService.joinMember(newTeam.getId(), anotherMember.getUsername());

        TeamDTO anotherTeam = teamTestMaster.createNewTeam(anotherMember.getUsername());

        // request without token.
        mockMvc.perform(delete("/api/member/teams/{teamId}", newTeam.getId()))
                .andExpect(status().isBadRequest());

        // withdraw from not joined team.
        mockMvc.perform(delete("/api/member/teams/{teamId}", anotherTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, requestToken))
                .andExpect(status().isForbidden());

        // normal request.
        mockMvc.perform(delete("/api/member/teams/{teamId}", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, anotherRequestToken))
                .andExpect(status().isOk())
                .andDo(document("MemberController/quitTeam",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        requestHeaders(
                                RequestSnippets.authorization
                        ),
                        pathParameters(
                                RequestSnippets.teamIdPath)))
                .andReturn();


        // check member is quit.
        assertTrue(
                teamService.getMembers(newTeam.getId()).stream().noneMatch(
                        dto -> dto.getUsername().equals(anotherMember.getUsername())));
    }
}
