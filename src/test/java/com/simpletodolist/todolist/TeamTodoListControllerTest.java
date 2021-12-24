package com.simpletodolist.todolist;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpletodolist.todolist.Snippets.EntityResponseSnippets;
import com.simpletodolist.todolist.Snippets.RequestSnippets;
import com.simpletodolist.todolist.Snippets.ResponseSnippets;
import com.simpletodolist.todolist.common.bind.ApiResponse;
import com.simpletodolist.todolist.domains.todolist.bind.request.TodoListInformationRequest;
import com.simpletodolist.todolist.domains.member.bind.MemberDTO;
import com.simpletodolist.todolist.domains.team.bind.TeamDTO;
import com.simpletodolist.todolist.domains.todolist.bind.TodoListDTO;
import com.simpletodolist.todolist.domains.todolist.exception.NoTodoListFoundException;
import com.simpletodolist.todolist.domains.jwt.service.JwtService;
import com.simpletodolist.todolist.domains.member.service.BasicMemberService;
import com.simpletodolist.todolist.domains.team.service.BasicTeamService;
import com.simpletodolist.todolist.domains.todo.service.BasicTodoService;
import com.simpletodolist.todolist.domains.todolist.service.BasicTodoListService;
import com.simpletodolist.todolist.util.MemberTestMaster;
import com.simpletodolist.todolist.util.TeamTestMaster;
import com.simpletodolist.todolist.util.TodoListTestMaster;
import com.simpletodolist.todolist.util.TodoTestMaster;
import com.simpletodolist.todolist.util.container.TestTodoListDTO;
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
class TeamTodoListControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    BasicMemberService memberService;
    @Autowired
    BasicTeamService teamService;
    @Autowired
    BasicTodoListService todoListService;
    @Autowired
    BasicTodoService todoService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    JwtService jwtService;

    MemberTestMaster memberTestMaster;
    TeamTestMaster teamTestMaster;
    TodoListTestMaster todoListTestMaster;
    TodoTestMaster todoTestMaster;

    @BeforeEach
    void init() {
        memberTestMaster = new MemberTestMaster(memberService, jwtService);
        teamTestMaster = new TeamTestMaster(teamService);
        todoListTestMaster = new TodoListTestMaster(todoListService);
        todoTestMaster = new TodoTestMaster(todoService);
    }

    @Test
    @DisplayName("Get to-do lists of team.")
    void getTodoLists() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUsername());
        MemberDTO anotherMember = memberTestMaster.createNewMember();
        String anotherToken = memberTestMaster.getRequestToken(anotherMember.getUsername());

        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUsername());

        TodoListDTO newTodoList = todoListTestMaster.createNewTodoList(newMember.getUsername(), newTeam.getId());
        todoTestMaster.createNewTodo(newMember.getUsername(), newTeam.getId(), newTodoList.getId(), false);
        todoTestMaster.createNewTodo(newMember.getUsername(), newTeam.getId(), newTodoList.getId(), false);

        // request without token.
        mockMvc.perform(get("/api/team/{teamId}/todolist", newTeam.getId()))
                .andExpect(status().isBadRequest());

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
                .andExpect(jsonPath("$.result").isArray())
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
                                ResponseSnippets.ApiResponseDescriptor.success,
                                ResponseSnippets.ApiResponseDescriptor.result,
                                ResponseSnippets.ApiResponseDescriptor.message,
                                EntityResponseSnippets.TodoList.todoLists)))
                .andReturn();

        // check to-do list is in team.
        ApiResponse<List<TestTodoListDTO>> todoListsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertTrue(todoListsDTO.getResult().stream().anyMatch(dto -> dto.getId() == newTodoList.getId()));
    }

    @Test
    @DisplayName("Create new to-do list.")
    void createTodolist() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUsername());
        MemberDTO anotherMember = memberTestMaster.createNewMember();
        String anotherToken = memberTestMaster.getRequestToken(anotherMember.getUsername());

        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUsername());
        TodoListInformationRequest request = new TodoListInformationRequest();
        request.setTodoListName("todoListName");
        request.setLocked(false);

        // request without token.
        mockMvc.perform(post("/api/team/{teamId}/todolist", newTeam.getId())).andExpect(status().isBadRequest());

        // add to-do list to not exist team.
        mockMvc.perform(post("/api/team/{teamId}/todolist", 123456789L)
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        // add to-do list to not joined team.
        mockMvc.perform(post("/api/team/{teamId}/todolist", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, anotherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        // normal request.
        mockMvc.perform(post("/api/team/{teamId}/todolist", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("$.result.id").isNotEmpty())
                .andExpect(jsonPath("$.result.name").value(request.getTodoListName()))
                .andExpect(jsonPath("$.result.username").value(newMember.getUsername()))
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
                                RequestSnippets.TodoList.todoListName,
                                RequestSnippets.TodoList.locked
                        ),
                        responseFields(
                                ResponseSnippets.ApiResponseDescriptor.success,
                                ResponseSnippets.ApiResponseDescriptor.result,
                                ResponseSnippets.ApiResponseDescriptor.message,
                                EntityResponseSnippets.TodoList.id,
                                EntityResponseSnippets.TodoList.username,
                                EntityResponseSnippets.TodoList.alias,
                                EntityResponseSnippets.TodoList.name,
                                EntityResponseSnippets.TodoList.todos,
                                EntityResponseSnippets.TodoList.locked
                        )));

        // check to-do list is created.
        List<TodoListDTO> todoLists = teamService.readTeam(newTeam.getId()).getTodoLists();
        assertTrue(todoLists.stream()
                .anyMatch(list -> list.getName().equals(request.getTodoListName())));
    }

    @Test
    @DisplayName("Get specific to-do list.")
    void getSpecificTodoList() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUsername());
        MemberDTO anotherMember = memberTestMaster.createNewMember();

        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUsername());
        TeamDTO anotherTeam = teamTestMaster.createNewTeam(anotherMember.getUsername());

        TodoListDTO newTodoList = todoListTestMaster.createNewTodoList(newMember.getUsername(), newTeam.getId());
        todoTestMaster.createNewTodo(newMember.getUsername(), newTeam.getId(), newTodoList.getId(), false);

        // request without token.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), newTodoList.getId()))
                .andExpect(status().isBadRequest());

        // get to-do list of not exist team.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}", 123456789, newTodoList.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isNotFound());

        // get not exist to-do list.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), 123456789)
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isNotFound());

        // get to-do list of not joined team.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}", anotherTeam.getId(), newTodoList.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isForbidden());

        // normal request.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), newTodoList.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").isNotEmpty())
                .andExpect(jsonPath("$.result.name").value(newTodoList.getName()))
                .andExpect(jsonPath("$.result.username").value(newMember.getUsername()))
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
                                ResponseSnippets.ApiResponseDescriptor.success,
                                ResponseSnippets.ApiResponseDescriptor.result,
                                ResponseSnippets.ApiResponseDescriptor.message,
                                EntityResponseSnippets.TodoList.id,
                                EntityResponseSnippets.TodoList.username,
                                EntityResponseSnippets.TodoList.alias,
                                EntityResponseSnippets.TodoList.name,
                                EntityResponseSnippets.TodoList.todos,
                                EntityResponseSnippets.TodoList.locked
                        )));
    }

    @Test
    @DisplayName("Update to-do list.")
    void updateTodoList() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUsername());

        MemberDTO lockMember = memberTestMaster.createNewMember();
        String lockToken = memberTestMaster.getRequestToken(lockMember.getUsername());

        MemberDTO anotherMember = memberTestMaster.createNewMember();
        String anotherToken = memberTestMaster.getRequestToken(anotherMember.getUsername());

        MemberDTO notJoinedMember = memberTestMaster.createNewMember();
        String notJoinedToken = memberTestMaster.getRequestToken(notJoinedMember.getUsername());

        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUsername());
        teamService.joinMember(newTeam.getId(), lockMember.getUsername());
        teamService.joinMember(newTeam.getId(), anotherMember.getUsername());

        TodoListDTO newTodoList = todoListTestMaster.createNewTodoList(newMember.getUsername(), newTeam.getId());
        TodoListDTO lockedTodoList = todoListTestMaster.createNewTodoList(lockMember.getUsername(), newTeam.getId());

        TodoListInformationRequest request = new TodoListInformationRequest();
        request.setTodoListName(lockedTodoList.getName());
        request.setLocked(true);

        todoListService.updateTodoList(lockedTodoList.getId(), request);

        // request without token.
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), newTodoList.getId()))
                .andExpect(status().isBadRequest());

        request.setTodoListName("NA");
        // update to-do list of not exist team.
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}", 123456789, newTodoList.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        // update not exist to-do list.
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), 123456789)
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        // update to-do list of not joined team.
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), newTodoList.getId())
                .header(HttpHeaders.AUTHORIZATION, notJoinedToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        // update locked to-do list by not owner.
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), lockedTodoList.getId())
                .header(HttpHeaders.AUTHORIZATION, anotherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        // update locked to-do list by owner.
        request.setTodoListName("UNLOCKED");
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), lockedTodoList.getId())
                .header(HttpHeaders.AUTHORIZATION, lockToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.name").value(request.getTodoListName()));

        // update locked to-do list by team leader.
        request.setLocked(false);
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), lockedTodoList.getId())
                .header(HttpHeaders.AUTHORIZATION, lockToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.username").value(lockMember.getUsername()))
                .andExpect(jsonPath("$.result.locked").value(false));

        // only owner can lock to-do list.
        request.setLocked(true);
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), lockedTodoList.getId())
                .header(HttpHeaders.AUTHORIZATION, anotherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        // normal request.
        request.setTodoListName("UPDATED_BY_ANOTHER");
        request.setLocked(false);
        // TODO: decouple tests.
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), newTodoList.getId())
                .header(HttpHeaders.AUTHORIZATION, anotherToken) // if it's not locked everyone can update it.
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").isNotEmpty())
                .andExpect(jsonPath("$.result.name").value(request.getTodoListName()))
                .andExpect(jsonPath("$.result.username").value(newMember.getUsername()))
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
                                RequestSnippets.TodoList.todoListName,
                                RequestSnippets.TodoList.locked
                        ),
                        responseFields(
                                ResponseSnippets.ApiResponseDescriptor.success,
                                ResponseSnippets.ApiResponseDescriptor.result,
                                ResponseSnippets.ApiResponseDescriptor.message,
                                EntityResponseSnippets.TodoList.id,
                                EntityResponseSnippets.TodoList.username,
                                EntityResponseSnippets.TodoList.alias,
                                EntityResponseSnippets.TodoList.name,
                                EntityResponseSnippets.TodoList.todos,
                                EntityResponseSnippets.TodoList.locked
                        )));
    }

    @Test
    @DisplayName("Delete to-do list")
    void deleteTodolist() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUsername());

        MemberDTO lockMember = memberTestMaster.createNewMember();
        String lockToken = memberTestMaster.getRequestToken(lockMember.getUsername());

        MemberDTO anotherMember = memberTestMaster.createNewMember();
        String anotherToken = memberTestMaster.getRequestToken(anotherMember.getUsername());

        MemberDTO notJoinedMember = memberTestMaster.createNewMember();
        String notJoinedToken = memberTestMaster.getRequestToken(notJoinedMember.getUsername());

        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUsername());
        teamService.joinMember(newTeam.getId(), lockMember.getUsername());
        teamService.joinMember(newTeam.getId(), anotherMember.getUsername());

        TodoListDTO newTodoList = todoListTestMaster.createNewTodoList(newMember.getUsername(), newTeam.getId());
        TodoListDTO lockedTodoList = todoListTestMaster.createNewTodoList(lockMember.getUsername(), newTeam.getId());

        TodoListInformationRequest request = new TodoListInformationRequest();
        request.setTodoListName(lockedTodoList.getName());
        request.setLocked(true);
        todoListService.updateTodoList(lockedTodoList.getId(), request);


        // request without token.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), newTodoList.getId()))
                .andExpect(status().isBadRequest());

        // delete to-do list of not exist team.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}", 123456789, newTodoList.getId())
                .header(HttpHeaders.AUTHORIZATION, anotherToken))
                .andExpect(status().isNotFound());

        // delete not exist to-do list.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), 123456789)
                .header(HttpHeaders.AUTHORIZATION, anotherToken))
                .andExpect(status().isNotFound());

        // delete to-do list of not joined team.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), newTodoList.getId())
                .header(HttpHeaders.AUTHORIZATION, notJoinedToken))
                .andExpect(status().isForbidden());

        // delete locked to-do list by not owner.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), lockedTodoList.getId())
                .header(HttpHeaders.AUTHORIZATION, anotherToken))
                .andExpect(status().isForbidden());

        // delete locked to-do list by owner.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), lockedTodoList.getId())
                .header(HttpHeaders.AUTHORIZATION, lockToken))
                .andExpect(status().isOk());

        long id = lockedTodoList.getId();
        assertThrows(NoTodoListFoundException.class, () -> todoListService.getTodoListDetail(id));

        // delete locked to-do list overridden by leader.
        TodoListDTO anotherLockedTodoList = todoListTestMaster.createNewTodoList(lockMember.getUsername(), newTeam.getId());
        request.setTodoListName(anotherLockedTodoList.getName());
        request.setLocked(true);
        todoListService.updateTodoList(anotherLockedTodoList.getId(), request);

        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), anotherLockedTodoList.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isOk());

        long lockedId = anotherLockedTodoList.getId();
        assertThrows(NoTodoListFoundException.class, () -> todoListService.getTodoListDetail(lockedId));

        // normal request.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}", newTeam.getId(), newTodoList.getId())
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

        long newId = newTodoList.getId();
        assertThrows(NoTodoListFoundException.class, () -> todoListService.getTodoListDetail(newId));
    }
}
