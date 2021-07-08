package com.simpletodolist.todolist;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpletodolist.todolist.domain.UpdatableTeamInformation;
import com.simpletodolist.todolist.domain.UpdatableTodoListInformation;
import com.simpletodolist.todolist.domain.dto.*;
import com.simpletodolist.todolist.dto.request.TodoListCreateDTO;
import com.simpletodolist.todolist.service.member.MemberService;
import com.simpletodolist.todolist.service.team.TeamService;
import com.simpletodolist.todolist.service.todolist.TodoListService;
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

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TeamTodoListControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    MemberService memberService;
    @Autowired
    TeamService teamService;
    @Autowired
    TodoListService todoListService;
    @Autowired
    ObjectMapper objectMapper;

    String testUserId = "testuserid";
    String anotherUserId = "anotheruserid";

    String testToken;
    String anotherToken;

    long testTeamId;
    long anotherTeamId;

    String testTodoListName = "testtodolist";
    String anotherTodoListName = "anothertodolist";

    long testTodoListId;
    long anotherTodoListId;

    @BeforeAll
    public void init() {
        memberService.registerMember(new MemberDTO(testUserId, "name", "pw"));
        memberService.registerMember(new MemberDTO(anotherUserId, "name2", "pw"));
        testToken = String.format("Bearer %s", memberService.loginMember(testUserId, "pw").getToken());
        anotherToken = String.format("Bearer %s", memberService.loginMember(anotherUserId, "pw").getToken());

        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setTeamName("teamA");
        testTeamId = teamService.createTeam(testUserId, teamDTO).getId();

        teamDTO.setTeamName("teamB");
        anotherTeamId = teamService.createTeam(anotherUserId, teamDTO).getId();

        TodoListDTO todoListDTO = new TodoListDTO();
        todoListDTO.setTodoListName(testTodoListName);
        testTodoListId = todoListService.createTodoList(testTeamId, testUserId, todoListDTO).getTodoListId();

    }

    @Test
    @DisplayName("Get to-do lists of team.")
    public void getTodoLists() throws Exception {
        // request without token.
        mockMvc.perform(get("/api/team/{teamId}/todolist", testTeamId))
                .andExpect(status().isUnauthorized());

        // request not exist team's to-do lists.
        mockMvc.perform(get("/api/team/{teamId}/todolist", 123456789)
                .header(HttpHeaders.AUTHORIZATION, testToken))
                .andExpect(status().isNotFound());

        // request not joined team's to-do lists.
        mockMvc.perform(get("/api/team/{teamId}/todolist", anotherTeamId)
                .header(HttpHeaders.AUTHORIZATION, testToken))
                .andExpect(status().isForbidden());

        // normal request.
        MvcResult mvcResult = mockMvc.perform(get("/api/team/{teamId}/todolist", testTeamId)
                .header(HttpHeaders.AUTHORIZATION, testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.todoLists").isArray())
                .andDo(document("TeamTodoListController/getTodolists",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        RequestSnippets.authorization,
                        RequestSnippets.teamIdPath,
                        ResponseSnippets.todoLists))
                .andReturn();

        // check to-do list is in team.
        TodoListsDTO todoListsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TodoListsDTO.class);
        assertTrue(todoListsDTO.getTodoLists().stream().anyMatch(dto -> dto.getTodoListId() == testTodoListId));
    }

    @Test
    @DisplayName("Create new to-do list.")
    public void createTodolist() throws Exception {
        // request without token.
        mockMvc.perform(post("/api/team/{teamId}/todolist", testTeamId)).andExpect(status().isUnauthorized());

        // add to-do list to not exist team.
        mockMvc.perform(post("/api/team/{teamId}/todolist", 123456789L)
                .header(HttpHeaders.AUTHORIZATION, testToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TodoListCreateDTO("createme"))))
                .andExpect(status().isNotFound());

        // add to-do list to not joined team.
        mockMvc.perform(post("/api/team/{teamId}/todolist", anotherTeamId)
                .header(HttpHeaders.AUTHORIZATION, testToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TodoListCreateDTO("createme"))))
                .andExpect(status().isForbidden());

        // TODO: body로 데이터를 전송하는(POST 등)의 경우 invalid한 경우까지 테스트. 다른 곳도 마찬가지.
        mockMvc.perform(post("/api/team/{teamId}/todolist", testTeamId)
                .header(HttpHeaders.AUTHORIZATION, testToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"field\":\"asdf\"}"))
                .andExpect(status().isBadRequest());

        // normal request.
        mockMvc.perform(post("/api/team/{teamId}/todolist", testTeamId)
                .header(HttpHeaders.AUTHORIZATION, testToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TodoListCreateDTO(anotherTodoListName))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(anotherTodoListName))
                .andExpect(jsonPath("$.ownerUserId").value(testUserId))
                .andDo(document("TeamTodoListController/createTodoList",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        RequestSnippets.teamIdPath,
                        RequestSnippets.authorization,
                        RequestSnippets.todoListName,
                        ResponseSnippets.todoList));

        // check to-do list is created.
        assertTrue(teamService.getTeamTodoLists(testTeamId).getTodoLists().stream()
                .anyMatch(dto -> dto.getTodoListName().equals(anotherTodoListName)));
    }

    @Test
    @DisplayName("Get specific to-do list.")
    public void getSpecificTodoList() throws Exception {
        String specificTodoListName = "todolistname";
        TodoListDTO dto = new TodoListDTO();
        dto.setTodoListName(specificTodoListName);
        TodoListDTO todoList = todoListService.createTodoList(anotherTeamId, anotherUserId, dto);
        long specificTodoListId = todoList.getTodoListId();

        // request without token.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}", anotherTeamId, specificTodoListId))
                .andExpect(status().isUnauthorized());

        // get to-do list of not exist team.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}", 123456789, 1)
                .header(HttpHeaders.AUTHORIZATION, anotherToken))
                .andExpect(status().isNotFound());

        // get not exist to-do list.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}", anotherTeamId, 123456789)
                .header(HttpHeaders.AUTHORIZATION, anotherToken))
                .andExpect(status().isNotFound());

        // get to-do list of not joined team.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}", anotherTeamId, specificTodoListId)
                .header(HttpHeaders.AUTHORIZATION, testToken))
                .andExpect(status().isForbidden());

        // normal request.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}", anotherTeamId, specificTodoListId)
                .header(HttpHeaders.AUTHORIZATION, anotherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(specificTodoListName))
                .andExpect(jsonPath("$.ownerUserId").value(anotherUserId))
                .andDo(document("TeamTodoListController/getTodoList",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        RequestSnippets.teamIdAndTodoListIdPath,
                        RequestSnippets.authorization,
                        ResponseSnippets.todoList));
    }

    @Test
    @DisplayName("Update to-do list.")
    public void updateTodoList() throws Exception {
        String specificTodoListName = "todolistname";
        String updatedName = "updatedname";
        // TODO: Builder 패턴을 고려해보는 것도 좋겠군.
        TodoListDTO dto = new TodoListDTO();
        dto.setTodoListName(specificTodoListName);
        TodoListDTO todoList = todoListService.createTodoList(anotherTeamId, anotherUserId, dto);
        long specificTodoListId = todoList.getTodoListId();

        // request without token.
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}", anotherTeamId, specificTodoListId))
                .andExpect(status().isUnauthorized());

        // update to-do list of not exist team.
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}", 123456789, specificTodoListId)
                .header(HttpHeaders.AUTHORIZATION, anotherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TeamInformationUpdateRequestDTO(UpdatableTeamInformation.NAME, updatedName))))
                .andExpect(status().isNotFound());

        // update not exist to-do list.
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}", anotherTeamId, 123456789)
                .header(HttpHeaders.AUTHORIZATION, anotherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TeamInformationUpdateRequestDTO(UpdatableTeamInformation.NAME, updatedName))))
                .andExpect(status().isNotFound());

        // update to-do list of not joined team.
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}", anotherTeamId, specificTodoListId)
                .header(HttpHeaders.AUTHORIZATION, testToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TeamInformationUpdateRequestDTO(UpdatableTeamInformation.NAME, updatedName))))
                .andExpect(status().isForbidden());

        // normal request.
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}", anotherTeamId, specificTodoListId)
                .header(HttpHeaders.AUTHORIZATION, anotherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TeamInformationUpdateRequestDTO(UpdatableTeamInformation.NAME, updatedName))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(updatedName))
                .andExpect(jsonPath("$.ownerUserId").value(anotherUserId))
                .andDo(document("TeamTodoListController/updateTodoList",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        RequestSnippets.teamIdAndTodoListIdPath,
                        RequestSnippets.authorization,
                        RequestSnippets.todoListUpdate,
                        ResponseSnippets.todoList));
    }

    @Test
    @DisplayName("Delete to-do list")
    public void deleteTodolist() throws Exception {
        String specificTodoListName = "todolistname";
        TodoListDTO dto = new TodoListDTO();
        dto.setTodoListName(specificTodoListName);
        TodoListDTO todoList = todoListService.createTodoList(anotherTeamId, anotherUserId, dto);
        long specificTodoListId = todoList.getTodoListId();

        // request without token.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}", anotherTeamId, specificTodoListId))
                .andExpect(status().isUnauthorized());

        // delete to-do list of not exist team.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}", 123456789, specificTodoListId)
                .header(HttpHeaders.AUTHORIZATION, anotherToken))
                .andExpect(status().isNotFound());

        // delete not exist to-do list.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}", anotherTeamId, 123456789)
                .header(HttpHeaders.AUTHORIZATION, anotherToken))
                .andExpect(status().isNotFound());

        // delete to-do list of not joined team.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}", anotherTeamId, specificTodoListId)
                .header(HttpHeaders.AUTHORIZATION, testToken))
                .andExpect(status().isForbidden());

        // normal request.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}", anotherTeamId, specificTodoListId)
                .header(HttpHeaders.AUTHORIZATION, anotherToken))
                .andExpect(status().isOk())
                .andDo(document("TeamTodoListController/deleteTodoList",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        RequestSnippets.teamIdAndTodoListIdPath,
                        RequestSnippets.authorization));
    }


    @Test
    @DisplayName("Deny locked to-do list.")
    public void denyOthers() throws Exception {
        String deniedUserId = "denieduserid";
        memberService.registerMember(new MemberDTO(deniedUserId, "denied", "pw"));
        teamService.joinMember(anotherTeamId, deniedUserId);
        String deniedToken = String.format("Bearer %s", memberService.loginMember(deniedUserId, "pw").getToken());

        TodoListDTO dto = new TodoListDTO();
        dto.setTodoListName("lockedtodolist");
        long lockedTodoListId = todoListService.createTodoList(anotherTeamId, anotherUserId, dto).getTodoListId();
        todoListService.updateTodoList(lockedTodoListId, UpdatableTodoListInformation.LOCKED, true);

        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}", anotherTeamId, lockedTodoListId)
                .header(HttpHeaders.AUTHORIZATION, anotherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TodoListInformationUpdateRequestDTO(
                        UpdatableTodoListInformation.LOCKED,
                        true))))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}", anotherTeamId, lockedTodoListId)
                .header(HttpHeaders.AUTHORIZATION, deniedToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TodoListInformationUpdateRequestDTO(
                        UpdatableTodoListInformation.LOCKED,
                        false))))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}", anotherTeamId, lockedTodoListId)
                .header(HttpHeaders.AUTHORIZATION, deniedToken))
                .andExpect(status().isForbidden());
    }
}
