package com.simpletodolist.todolist;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpletodolist.todolist.controller.bind.MemberDTO;
import com.simpletodolist.todolist.controller.bind.TeamDTO;
import com.simpletodolist.todolist.controller.bind.TodoListDTO;
import com.simpletodolist.todolist.controller.bind.TodoListsDTO;
import com.simpletodolist.todolist.controller.bind.request.TeamInformationUpdateRequest;
import com.simpletodolist.todolist.controller.bind.request.field.UpdatableTeamInformation;
import com.simpletodolist.todolist.controller.bind.request.field.UpdatableTodoListInformation;
import com.simpletodolist.todolist.dto.request.TodoListCreateDTO;
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

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;


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
        MemberDTO newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());
        MemberDTO anotherMember = memberTestMaster.createNewMember();
        String anotherToken = memberTestMaster.getRequestToken(anotherMember.getUserId(), anotherMember.getPassword());

        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUserId());

        TodoListDTO newTodoList = todoListTestMaster.createNewTodoList(newMember.getUserId(), newTeam.getId());

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
        assertTrue(todoListsDTO.getTodoLists().stream().anyMatch(dto -> dto.getTodoListId() == newTodoList.getTodoListId()));
    }

    @Test
    @DisplayName("Create new to-do list.")
    public void createTodolist() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());
        MemberDTO anotherMember = memberTestMaster.createNewMember();
        String anotherToken = memberTestMaster.getRequestToken(anotherMember.getUserId(), anotherMember.getPassword());

        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUserId());

        // request without token.
        mockMvc.perform(post("/api/team/{teamId}/todolist", newTeam.getId())).andExpect(status().isUnauthorized());

        // add to-do list to not exist team.
        mockMvc.perform(post("/api/team/{teamId}/todolist", 123456789L)
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TodoListCreateDTO("createme"))))
                .andExpect(status().isNotFound());

        // add to-do list to not joined team.
        mockMvc.perform(post("/api/team/{teamId}/todolist", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, anotherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TodoListCreateDTO("createme"))))
                .andExpect(status().isForbidden());

        // normal request.
        String todoListName = "todoListName";
        mockMvc.perform(post("/api/team/{teamId}/todolist", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TodoListCreateDTO(todoListName))))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(todoListName))
                .andExpect(jsonPath("$.ownerUserId").value(newMember.getUserId()))
                .andDo(document("TeamTodoListController/createTodoList",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        RequestSnippets.teamIdPath,
                        RequestSnippets.authorization,
                        RequestSnippets.todoListName,
                        ResponseSnippets.todoList));

        // check to-do list is created.
        assertTrue(teamService.getTeamTodoLists(newTeam.getId()).getTodoLists().stream()
                .anyMatch(dto -> dto.getTodoListName().equals(todoListName)));
    }

    @Test
    @DisplayName("Get specific to-do list.")
    public void getSpecificTodoList() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());
        MemberDTO anotherMember = memberTestMaster.createNewMember();
        String anotherToken = memberTestMaster.getRequestToken(anotherMember.getUserId(), anotherMember.getPassword());

        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUserId());
        TeamDTO anotherTeam = teamTestMaster.createNewTeam(anotherMember.getUserId());

        TodoListDTO newTodoList = todoListTestMaster.createNewTodoList(newMember.getUserId(), newTeam.getId());

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
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        RequestSnippets.teamIdAndTodoListIdPath,
                        RequestSnippets.authorization,
                        ResponseSnippets.todoList));
    }

    @Test
    @DisplayName("Update to-do list.")
    public void updateTodoList() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());

        MemberDTO lockMember = memberTestMaster.createNewMember();
        String lockToken = memberTestMaster.getRequestToken(lockMember.getUserId(), lockMember.getPassword());

        MemberDTO anotherMember = memberTestMaster.createNewMember();
        String anotherToken = memberTestMaster.getRequestToken(anotherMember.getUserId(), anotherMember.getPassword());

        MemberDTO notJoinedMember = memberTestMaster.createNewMember();
        String notJoinedToken = memberTestMaster.getRequestToken(notJoinedMember.getUserId(), notJoinedMember.getPassword());

        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUserId());
        teamService.joinMember(newTeam.getId(), lockMember.getUserId());
        teamService.joinMember(newTeam.getId(), anotherMember.getUserId());

        TodoListDTO newTodoList = todoListTestMaster.createNewTodoList(newMember.getUserId(), newTeam.getId());
        TodoListDTO lockedTodoList = todoListTestMaster.createNewTodoList(lockMember.getUserId(), newTeam.getId());
        todoListService.updateTodoList(lockedTodoList.getTodoListId(), UpdatableTodoListInformation.LOCKED, true);

        // request without token.
        mockMvc.perform(patch("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), newTodoList.getTodoListId()))
                .andExpect(status().isUnauthorized());

        // update to-do list of not exist team.
        mockMvc.perform(patch("/api/team/{teamId}/todolist/{todoListId}", 123456789, newTodoList.getTodoListId())
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TeamInformationUpdateRequest(UpdatableTeamInformation.NAME, "na"))))
                .andExpect(status().isNotFound());

        // update not exist to-do list.
        mockMvc.perform(patch("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), 123456789)
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TeamInformationUpdateRequest(UpdatableTeamInformation.NAME, "na"))))
                .andExpect(status().isNotFound());

        // update to-do list of not joined team.
        mockMvc.perform(patch("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), newTodoList.getTodoListId())
                .header(HttpHeaders.AUTHORIZATION, notJoinedToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TeamInformationUpdateRequest(UpdatableTeamInformation.NAME, "na"))))
                .andExpect(status().isForbidden());

        // update locked to-do list by not owner.
        mockMvc.perform(patch("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), lockedTodoList.getTodoListId())
                .header(HttpHeaders.AUTHORIZATION, anotherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TeamInformationUpdateRequest(UpdatableTeamInformation.NAME, "na"))))
                .andExpect(status().isForbidden());

        // update locked to-do list by owner.
        mockMvc.perform(patch("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), lockedTodoList.getTodoListId())
                .header(HttpHeaders.AUTHORIZATION, lockToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TeamInformationUpdateRequest(UpdatableTeamInformation.NAME, "Unlocked"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Unlocked"));

        // update locked to-do list by team leader.
        mockMvc.perform(patch("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), lockedTodoList.getTodoListId())
                .header(HttpHeaders.AUTHORIZATION, lockToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TeamInformationUpdateRequest(UpdatableTeamInformation.LOCKED, false))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerUserId").value(lockMember.getUserId()))
                .andExpect(jsonPath("$.locked").value(false));

        // only owner can lock to-do list.
        mockMvc.perform(patch("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), lockedTodoList.getTodoListId())
                .header(HttpHeaders.AUTHORIZATION, anotherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TeamInformationUpdateRequest(UpdatableTeamInformation.LOCKED, true))))
                .andExpect(status().isForbidden());

        // normal request.
        mockMvc.perform(patch("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), newTodoList.getTodoListId())
                .header(HttpHeaders.AUTHORIZATION, anotherToken) // if it's not locked every can update it.
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TeamInformationUpdateRequest(UpdatableTeamInformation.NAME, "Updated by owner."))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Updated by owner."))
                .andExpect(jsonPath("$.ownerUserId").value(newMember.getUserId()))
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
        MemberDTO newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());

        MemberDTO lockMember = memberTestMaster.createNewMember();
        String lockToken = memberTestMaster.getRequestToken(lockMember.getUserId(), lockMember.getPassword());

        MemberDTO anotherMember = memberTestMaster.createNewMember();
        String anotherToken = memberTestMaster.getRequestToken(anotherMember.getUserId(), anotherMember.getPassword());

        MemberDTO notJoinedMember = memberTestMaster.createNewMember();
        String notJoinedToken = memberTestMaster.getRequestToken(notJoinedMember.getUserId(), notJoinedMember.getPassword());

        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUserId());
        teamService.joinMember(newTeam.getId(), lockMember.getUserId());
        teamService.joinMember(newTeam.getId(), anotherMember.getUserId());

        TodoListDTO newTodoList = todoListTestMaster.createNewTodoList(newMember.getUserId(), newTeam.getId());
        TodoListDTO lockedTodoList = todoListTestMaster.createNewTodoList(lockMember.getUserId(), newTeam.getId());
        todoListService.updateTodoList(lockedTodoList.getTodoListId(), UpdatableTodoListInformation.LOCKED, true);


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
        TodoListDTO anotherLockedTodoList = todoListTestMaster.createNewTodoList(lockMember.getUserId(), newTeam.getId());
        todoListService.updateTodoList(anotherLockedTodoList.getTodoListId(), UpdatableTodoListInformation.LOCKED, true);

        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), anotherLockedTodoList.getTodoListId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isOk());

        assertThrows(NoTodoListFoundException.class, () -> todoListService.getTodoListDetail(anotherLockedTodoList.getTodoListId()));

        // normal request.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), newTodoList.getTodoListId())
                .header(HttpHeaders.AUTHORIZATION, anotherToken)) // if it's not locked, every can remove it.
                .andExpect(status().isOk())
                .andDo(document("TeamTodoListController/deleteTodoList",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        RequestSnippets.teamIdAndTodoListIdPath,
                        RequestSnippets.authorization));

        assertThrows(NoTodoListFoundException.class, () -> todoListService.getTodoListDetail(newTodoList.getTodoListId()));

    }
}
