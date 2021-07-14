package com.simpletodolist.todolist;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpletodolist.todolist.Snippets.EntityDescriptor;
import com.simpletodolist.todolist.Snippets.RequestSnippets;
import com.simpletodolist.todolist.domain.bind.MemberDTO;
import com.simpletodolist.todolist.domain.bind.TeamDTO;
import com.simpletodolist.todolist.domain.bind.TodoListDTO;
import com.simpletodolist.todolist.exception.todolist.NoTodoListFoundException;
import com.simpletodolist.todolist.service.member.MemberService;
import com.simpletodolist.todolist.service.team.TeamService;
import com.simpletodolist.todolist.service.todo.TodoService;
import com.simpletodolist.todolist.service.todolist.TodoListService;
import com.simpletodolist.todolist.util.MemberTestMaster;
import com.simpletodolist.todolist.util.TeamTestMaster;
import com.simpletodolist.todolist.util.TodoListTestMaster;
import com.simpletodolist.todolist.util.TodoTestMaster;
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

import static com.simpletodolist.todolist.util.DocumentUtil.commonRequestPreprocessor;
import static com.simpletodolist.todolist.util.DocumentUtil.commonResponsePreprocessor;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    TodoService todoService;
    @Autowired
    ObjectMapper objectMapper;

    MemberTestMaster memberTestMaster;
    TeamTestMaster teamTestMaster;
    TodoListTestMaster todoListTestMaster;
    TodoTestMaster todoTestMaster;

    @BeforeAll
    public void init() {
        memberTestMaster = new MemberTestMaster(memberService);
        teamTestMaster = new TeamTestMaster(teamService);
        todoListTestMaster = new TodoListTestMaster(todoListService);
        todoTestMaster = new TodoTestMaster(todoService);
    }

    @Test
    @DisplayName("Get to-do lists of team.")
    public void getTodoLists() throws Exception {
        MemberDTO.Response newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());
        MemberDTO.Response anotherMember = memberTestMaster.createNewMember();
        String anotherToken = memberTestMaster.getRequestToken(anotherMember.getUserId(), anotherMember.getPassword());

        TeamDTO.Response newTeam = teamTestMaster.createNewTeam(newMember.getUserId());

        TodoListDTO.Response newTodoList = todoListTestMaster.createNewTodoList(newMember.getUserId(), newTeam.getId());

        // request without token.
        mockMvc.perform(get("/api/team/{teamId}/todolist", newTeam.getId()))
                .andExpect(status().isUnauthorized());

        // request not exist team's to-do lists.
        mockMvc.perform(get("/api/team/{teamId}/todolist", 123456789)
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isNotFound());

        // request not joined team's to-do lists.
        mockMvc.perform(get("/api/team/{teamId}/todolist", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, anotherToken))
                .andExpect(status().isForbidden());

        // normal request.
        MvcResult mvcResult = mockMvc.perform(get("/api/team/{teamId}/todolist", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andDo(document("TeamTodoListController/getTodolists",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        pathParameters(
                                RequestSnippets.teamIdPath
                        ),
                        requestHeaders(
                                RequestSnippets.authorization
                        ),
                        responseFields(
                                EntityDescriptor.TodoList.todoLists)))
                .andReturn();

        // check to-do list is in team.
        List<TodoListDTO.Response> todoListsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertTrue(todoListsDTO.stream().anyMatch(dto -> dto.getTodoListId() == newTodoList.getTodoListId()));
    }

    @Test
    @DisplayName("Create new to-do list.")
    public void createTodolist() throws Exception {
        MemberDTO.Response newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());
        MemberDTO.Response anotherMember = memberTestMaster.createNewMember();
        String anotherToken = memberTestMaster.getRequestToken(anotherMember.getUserId(), anotherMember.getPassword());

        TeamDTO.Response newTeam = teamTestMaster.createNewTeam(newMember.getUserId());
        String todoListName = "todoListName";

        // request without token.
        mockMvc.perform(post("/api/team/{teamId}/todolist", newTeam.getId())).andExpect(status().isUnauthorized());

        // add to-do list to not exist team.
        mockMvc.perform(post("/api/team/{teamId}/todolist", 123456789L)
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TodoListDTO.RegisterRequest.builder().todoListName(todoListName).build())))
                .andExpect(status().isNotFound());

        // add to-do list to not joined team.
        mockMvc.perform(post("/api/team/{teamId}/todolist", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, anotherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TodoListDTO.RegisterRequest.builder().todoListName(todoListName).build())))
                .andExpect(status().isForbidden());

        // normal request.
        mockMvc.perform(post("/api/team/{teamId}/todolist", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TodoListDTO.RegisterRequest.builder().todoListName(todoListName).build())))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(todoListName))
                .andExpect(jsonPath("$.ownerUserId").value(newMember.getUserId()))
                .andDo(document("TeamTodoListController/createTodoList",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        pathParameters(
                                RequestSnippets.teamIdPath
                        ),
                        requestHeaders(
                                RequestSnippets.authorization
                        ),
                        requestFields(
                                RequestSnippets.TodoList.CreateTodoList.todoListName
                        ),
                        responseFields(
                                EntityDescriptor.TodoList.todoListInformation
                        )));

        // check to-do list is created.
        assertTrue(teamService.getTeamTodoLists(newTeam.getId()).stream()
                .anyMatch(dto -> dto.getTodoListName().equals(todoListName)));
    }

    @Test
    @DisplayName("Get specific to-do list.")
    public void getSpecificTodoList() throws Exception {
        MemberDTO.Response newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());
        MemberDTO.Response anotherMember = memberTestMaster.createNewMember();

        TeamDTO.Response newTeam = teamTestMaster.createNewTeam(newMember.getUserId());
        TeamDTO.Response anotherTeam = teamTestMaster.createNewTeam(anotherMember.getUserId());

        TodoListDTO.Response newTodoList = todoListTestMaster.createNewTodoList(newMember.getUserId(), newTeam.getId());

        // request without token.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), newTodoList.getTodoListId()))
                .andExpect(status().isUnauthorized());

        // get to-do list of not exist team.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}", 123456789, newTodoList.getTodoListId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isNotFound());

        // get not exist to-do list.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), 123456789)
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isNotFound());

        // get to-do list of not joined team.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}", anotherTeam.getId(), newTodoList.getTodoListId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isForbidden());

        // normal request.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), newTodoList.getTodoListId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(newTodoList.getTodoListName()))
                .andExpect(jsonPath("$.ownerUserId").value(newMember.getUserId()))
                .andDo(document("TeamTodoListController/getTodoList",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        pathParameters(
                                RequestSnippets.teamIdPath,
                                RequestSnippets.todoListIdPath
                        ),
                        requestHeaders(
                                RequestSnippets.authorization
                        ),
                        responseFields(
                                EntityDescriptor.TodoList.todoListInformation
                        )));
    }

    @Test
    @DisplayName("Update to-do list.")
    public void updateTodoList() throws Exception {
        MemberDTO.Response newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());

        MemberDTO.Response lockMember = memberTestMaster.createNewMember();
        String lockToken = memberTestMaster.getRequestToken(lockMember.getUserId(), lockMember.getPassword());

        MemberDTO.Response anotherMember = memberTestMaster.createNewMember();
        String anotherToken = memberTestMaster.getRequestToken(anotherMember.getUserId(), anotherMember.getPassword());

        MemberDTO.Response notJoinedMember = memberTestMaster.createNewMember();
        String notJoinedToken = memberTestMaster.getRequestToken(notJoinedMember.getUserId(), notJoinedMember.getPassword());

        TeamDTO.Response newTeam = teamTestMaster.createNewTeam(newMember.getUserId());
        teamService.joinMember(newTeam.getId(), lockMember.getUserId());
        teamService.joinMember(newTeam.getId(), anotherMember.getUserId());

        TodoListDTO.Response newTodoList = todoListTestMaster.createNewTodoList(newMember.getUserId(), newTeam.getId());
        TodoListDTO.Response lockedTodoList = todoListTestMaster.createNewTodoList(lockMember.getUserId(), newTeam.getId());
        todoListService.updateTodoList(lockedTodoList.getTodoListId(), TodoListDTO.UpdateRequest.UpdatableTodoListInformation.LOCKED, true);

        // request without token.
        mockMvc.perform(patch("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), newTodoList.getTodoListId()))
                .andExpect(status().isUnauthorized());


        String requestContent = objectMapper.writeValueAsString(TeamDTO.UpdateRequest.builder()
                .field(TeamDTO.UpdateRequest.UpdatableTeamInformation.NAME)
                .value("na")
                .build());
        // update to-do list of not exist team.
        mockMvc.perform(patch("/api/team/{teamId}/todolist/{todoListId}", 123456789, newTodoList.getTodoListId())
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent))
                .andExpect(status().isNotFound());

        // update not exist to-do list.
        mockMvc.perform(patch("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), 123456789)
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent))
                .andExpect(status().isNotFound());

        // update to-do list of not joined team.
        mockMvc.perform(patch("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), newTodoList.getTodoListId())
                .header(HttpHeaders.AUTHORIZATION, notJoinedToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent))
                .andExpect(status().isForbidden());

        // update locked to-do list by not owner.
        mockMvc.perform(patch("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), lockedTodoList.getTodoListId())
                .header(HttpHeaders.AUTHORIZATION, anotherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent))
                .andExpect(status().isForbidden());

        // update locked to-do list by owner.
        String unlocked = "unlocked";
        mockMvc.perform(patch("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), lockedTodoList.getTodoListId())
                .header(HttpHeaders.AUTHORIZATION, lockToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TeamDTO.UpdateRequest.builder()
                        .field(TeamDTO.UpdateRequest.UpdatableTeamInformation.NAME)
                        .value(unlocked)
                        .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(unlocked));

        // update locked to-do list by team leader.
        mockMvc.perform(patch("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), lockedTodoList.getTodoListId())
                .header(HttpHeaders.AUTHORIZATION, lockToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TeamDTO.UpdateRequest.builder()
                        .field(TeamDTO.UpdateRequest.UpdatableTeamInformation.LOCKED)
                        .value(false)
                        .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerUserId").value(lockMember.getUserId()))
                .andExpect(jsonPath("$.locked").value(false));

        // only owner can lock to-do list.
        mockMvc.perform(patch("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), lockedTodoList.getTodoListId())
                .header(HttpHeaders.AUTHORIZATION, anotherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TeamDTO.UpdateRequest.builder()
                        .field(TeamDTO.UpdateRequest.UpdatableTeamInformation.LOCKED)
                        .value(true)
                        .build())))
                .andExpect(status().isForbidden());

        // normal request.
        mockMvc.perform(patch("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), newTodoList.getTodoListId())
                .header(HttpHeaders.AUTHORIZATION, anotherToken) // if it's not locked everyone can update it.
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TeamDTO.UpdateRequest.builder()
                        .field(TeamDTO.UpdateRequest.UpdatableTeamInformation.NAME)
                        .value("Updated by owner.")
                        .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Updated by owner."))
                .andExpect(jsonPath("$.ownerUserId").value(newMember.getUserId()))
                .andDo(document("TeamTodoListController/updateTodoList",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        pathParameters(
                                RequestSnippets.teamIdPath,
                                RequestSnippets.todoListIdPath
                        ),
                        requestHeaders(
                                RequestSnippets.authorization
                        ),
                        requestFields(
                                RequestSnippets.TodoList.UpdateTodoList.field,
                                RequestSnippets.TodoList.UpdateTodoList.value
                        ),
                        responseFields(
                                EntityDescriptor.TodoList.todoListInformation
                        )));
    }

    @Test
    @DisplayName("Delete to-do list")
    public void deleteTodolist() throws Exception {
        MemberDTO.Response newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());

        MemberDTO.Response lockMember = memberTestMaster.createNewMember();
        String lockToken = memberTestMaster.getRequestToken(lockMember.getUserId(), lockMember.getPassword());

        MemberDTO.Response anotherMember = memberTestMaster.createNewMember();
        String anotherToken = memberTestMaster.getRequestToken(anotherMember.getUserId(), anotherMember.getPassword());

        MemberDTO.Response notJoinedMember = memberTestMaster.createNewMember();
        String notJoinedToken = memberTestMaster.getRequestToken(notJoinedMember.getUserId(), notJoinedMember.getPassword());

        TeamDTO.Response newTeam = teamTestMaster.createNewTeam(newMember.getUserId());
        teamService.joinMember(newTeam.getId(), lockMember.getUserId());
        teamService.joinMember(newTeam.getId(), anotherMember.getUserId());

        TodoListDTO.Response newTodoList = todoListTestMaster.createNewTodoList(newMember.getUserId(), newTeam.getId());
        TodoListDTO.Response lockedTodoList = todoListTestMaster.createNewTodoList(lockMember.getUserId(), newTeam.getId());
        todoListService.updateTodoList(lockedTodoList.getTodoListId(), TodoListDTO.UpdateRequest.UpdatableTodoListInformation.LOCKED, true);


        // request without token.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), newTodoList.getTodoListId()))
                .andExpect(status().isUnauthorized());

        // delete to-do list of not exist team.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}", 123456789, newTodoList.getTodoListId())
                .header(HttpHeaders.AUTHORIZATION, anotherToken))
                .andExpect(status().isNotFound());

        // delete not exist to-do list.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), 123456789)
                .header(HttpHeaders.AUTHORIZATION, anotherToken))
                .andExpect(status().isNotFound());

        // delete to-do list of not joined team.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), newTodoList.getTodoListId())
                .header(HttpHeaders.AUTHORIZATION, notJoinedToken))
                .andExpect(status().isForbidden());

        // delete locked to-do list by not owner.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), lockedTodoList.getTodoListId())
                .header(HttpHeaders.AUTHORIZATION, anotherToken))
                .andExpect(status().isForbidden());

        // delete locked to-do list by owner.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), lockedTodoList.getTodoListId())
                .header(HttpHeaders.AUTHORIZATION, lockToken))
                .andExpect(status().isOk());

        assertThrows(NoTodoListFoundException.class, () -> todoListService.getTodoListDetail(lockedTodoList.getTodoListId()));

        // delete locked to-do list overridden by leader.
        TodoListDTO.Response anotherLockedTodoList = todoListTestMaster.createNewTodoList(lockMember.getUserId(), newTeam.getId());
        todoListService.updateTodoList(anotherLockedTodoList.getTodoListId(), TodoListDTO.UpdateRequest.UpdatableTodoListInformation.LOCKED, true);

        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), anotherLockedTodoList.getTodoListId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isOk());

        assertThrows(NoTodoListFoundException.class, () -> todoListService.getTodoListDetail(anotherLockedTodoList.getTodoListId()));

        // normal request.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), newTodoList.getTodoListId())
                .header(HttpHeaders.AUTHORIZATION, anotherToken)) // if it's not locked, every can remove it.
                .andExpect(status().isOk())
                .andDo(document("TeamTodoListController/deleteTodoList",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        pathParameters(
                                RequestSnippets.teamIdPath,
                                RequestSnippets.todoListIdPath
                        ),
                        requestHeaders(
                                RequestSnippets.authorization)));

        assertThrows(NoTodoListFoundException.class, () -> todoListService.getTodoListDetail(newTodoList.getTodoListId()));

    }
}
