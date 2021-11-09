package com.simpletodolist.todolist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpletodolist.todolist.Snippets.EntityResponseSnippets;
import com.simpletodolist.todolist.Snippets.RequestSnippets;
import com.simpletodolist.todolist.Snippets.ResponseSnippets;
import com.simpletodolist.todolist.controller.bind.todo.TodoInformationRequest;
import com.simpletodolist.todolist.domain.bind.MemberDTO;
import com.simpletodolist.todolist.domain.bind.TeamDTO;
import com.simpletodolist.todolist.domain.bind.TodoDTO;
import com.simpletodolist.todolist.domain.bind.TodoListDTO;
import com.simpletodolist.todolist.exception.todo.NoTodoFoundException;
import com.simpletodolist.todolist.security.JwtTokenUtil;
import com.simpletodolist.todolist.service.member.BasicMemberService;
import com.simpletodolist.todolist.service.team.BasicTeamService;
import com.simpletodolist.todolist.service.todo.BasicTodoService;
import com.simpletodolist.todolist.service.todolist.BasicTodoListService;
import com.simpletodolist.todolist.util.MemberTestMaster;
import com.simpletodolist.todolist.util.TeamTestMaster;
import com.simpletodolist.todolist.util.TodoListTestMaster;
import com.simpletodolist.todolist.util.TodoTestMaster;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.simpletodolist.todolist.util.DocumentUtil.commonRequestPreprocessor;
import static com.simpletodolist.todolist.util.DocumentUtil.commonResponsePreprocessor;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
class TeamTodoControllerTest {
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
    JwtTokenUtil jwtTokenUtil;

    MemberTestMaster memberTestMaster;
    TeamTestMaster teamTestMaster;
    TodoListTestMaster todoListTestMaster;
    TodoTestMaster todoTestMaster;


    @BeforeEach
    void init() {
        memberTestMaster = new MemberTestMaster(memberService, jwtTokenUtil);
        teamTestMaster = new TeamTestMaster(teamService);
        todoListTestMaster = new TodoListTestMaster(todoListService);
        todoTestMaster = new TodoTestMaster(todoService);
    }

    @Test
    @DisplayName("Get todos of to-do list.")
    void getTodos() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        MemberDTO otherMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUsername(), newMember.getPassword());
        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUsername());
        TeamDTO otherTeam = teamTestMaster.createNewTeam(otherMember.getUsername());
        TodoListDTO newTodoList = todoListTestMaster.createNewTodoList(newMember.getUsername(), newTeam.getId());
        todoTestMaster.createNewTodo(newMember.getUsername(), newTeam.getId(), newTodoList.getId(), false);
        todoTestMaster.createNewTodo(newMember.getUsername(), newTeam.getId(), newTodoList.getId(), false);
        todoTestMaster.createNewTodo(newMember.getUsername(), newTeam.getId(), newTodoList.getId(), false);

        // request without token.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo", newTeam.getId(), newTodoList.getId()))
                .andExpect(status().isBadRequest());

        // request not existing team.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo", 123456789, newTodoList.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isNotFound());

        // request not joined team.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo", otherTeam.getId(), newTodoList.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isForbidden());

        // request not existing todolist.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo", newTeam.getId(), 123456789)
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isNotFound());

        // normal request.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo", newTeam.getId(), newTodoList.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").isArray())
                .andDo(document("TeamTodoController/getTodos",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        requestHeaders(
                                RequestSnippets.authorization
                        ),
                        pathParameters(
                                RequestSnippets.teamIdPath,
                                RequestSnippets.todoListIdPath),
                        responseFields(
                                ResponseSnippets.ApiResponseDescriptor.success,
                                ResponseSnippets.ApiResponseDescriptor.result,
                                ResponseSnippets.ApiResponseDescriptor.message,
                                EntityResponseSnippets.Todo.todos)));
    }

    @Test
    @DisplayName("Get specific to-do.")
    void getSpecificTodo() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        MemberDTO otherMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUsername(), newMember.getPassword());
        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUsername());
        TeamDTO otherTeam = teamTestMaster.createNewTeam(otherMember.getUsername());
        teamService.joinMember(newTeam.getId(), otherMember.getUsername());
        TodoListDTO newTodoList = todoListTestMaster.createNewTodoList(newMember.getUsername(), newTeam.getId());
        TodoListDTO otherTodoList = todoListTestMaster.createNewTodoList(otherMember.getUsername(), newTeam.getId());
        TodoDTO newTodo = todoTestMaster.createNewTodo(newMember.getUsername(), newTeam.getId(), newTodoList.getId(), false);

        // request without token.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), newTodoList.getId(), newTodo.getId()))
                .andExpect(status().isBadRequest());

        // request not existing team.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", 123456789, newTodoList.getId(), newTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isNotFound());

        // request not joined team.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", otherTeam.getId(), newTodoList.getId(), newTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isForbidden());

        // request not existing todolist.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), 123456789, newTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isNotFound());

        // request different todolist.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), otherTodoList.getId(), newTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isNotFound());

        // request not existing to-do.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), newTodoList.getId(), 123456789)
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isNotFound());

        // normal request.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), newTodoList.getId(), newTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(newTodo.getId()))
                .andExpect(jsonPath("$.result.title").value(newTodo.getTitle()))
                .andExpect(jsonPath("$.result.content").value(newTodo.getContent()))
                .andDo(document("TeamTodoController/getTodo",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        requestHeaders(
                                RequestSnippets.authorization
                        ),
                        pathParameters(
                                RequestSnippets.teamIdPath,
                                RequestSnippets.todoListIdPath,
                                RequestSnippets.todoIdPath
                        ),
                        responseFields(
                                ResponseSnippets.ApiResponseDescriptor.success,
                                ResponseSnippets.ApiResponseDescriptor.result,
                                ResponseSnippets.ApiResponseDescriptor.message,
                                EntityResponseSnippets.Todo.id,
                                EntityResponseSnippets.Todo.title,
                                EntityResponseSnippets.Todo.content,
                                EntityResponseSnippets.Todo.username,
                                EntityResponseSnippets.Todo.alias,
                                EntityResponseSnippets.Todo.locked
                        )));
    }

    @Test
    @DisplayName("Create to-do")
    void createTodo() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        MemberDTO otherMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUsername(), newMember.getPassword());
        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUsername());
        TeamDTO otherTeam = teamTestMaster.createNewTeam(otherMember.getUsername());
        TodoListDTO newTodoList = todoListTestMaster.createNewTodoList(newMember.getUsername(), newTeam.getId());

        TodoInformationRequest request = new TodoInformationRequest();
        request.setTitle("title");
        request.setContent("content");
        request.setLocked(false);

        // request without token.
        mockMvc.perform(post("/api/team/{teamId}/todolist/{todoListId}/todo", newTeam.getId(), newTodoList.getId()))
                .andExpect(status().isBadRequest());

        // request not existing team.
        mockMvc.perform(post("/api/team/{teamId}/todolist/{todoListId}/todo", 123456789, newTodoList.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        // request not joined team.
        mockMvc.perform(post("/api/team/{teamId}/todolist/{todoListId}/todo", otherTeam.getId(), newTodoList.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        // request not existing todolist.
        mockMvc.perform(post("/api/team/{teamId}/todolist/{todoListId}/todo", newTeam.getId(), 123456789)
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        // normal request.
        mockMvc.perform(post("/api/team/{teamId}/todolist/{todoListId}/todo", newTeam.getId(), newTodoList.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("$.result.id").isNotEmpty())
                .andExpect(jsonPath("$.result.title").value("title"))
                .andExpect(jsonPath("$.result.content").value("content"))
                .andDo(document("TeamTodoController/createTodo",
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
                                RequestSnippets.Todo.title,
                                RequestSnippets.Todo.content,
                                RequestSnippets.Todo.locked
                        ),
                        responseFields(
                                ResponseSnippets.ApiResponseDescriptor.success,
                                ResponseSnippets.ApiResponseDescriptor.result,
                                ResponseSnippets.ApiResponseDescriptor.message,
                                EntityResponseSnippets.Todo.id,
                                EntityResponseSnippets.Todo.title,
                                EntityResponseSnippets.Todo.content,
                                EntityResponseSnippets.Todo.username,
                                EntityResponseSnippets.Todo.alias,
                                EntityResponseSnippets.Todo.locked)));
    }

    @Test
    @DisplayName("Update to-do.")
    void updateTodo() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        MemberDTO otherMember = memberTestMaster.createNewMember();
        MemberDTO lockMember = memberTestMaster.createNewMember();

        String newToken = memberTestMaster.getRequestToken(newMember.getUsername(), newMember.getPassword());
        String lockToken = memberTestMaster.getRequestToken(lockMember.getUsername(), lockMember.getPassword());
        String otherToken = memberTestMaster.getRequestToken(otherMember.getUsername(), otherMember.getPassword());

        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUsername());
        TeamDTO otherTeam = teamTestMaster.createNewTeam(otherMember.getUsername());
        teamService.joinMember(newTeam.getId(), otherMember.getUsername());
        teamService.joinMember(newTeam.getId(), lockMember.getUsername());

        TodoListDTO newTodoList = todoListTestMaster.createNewTodoList(newMember.getUsername(), newTeam.getId());
        TodoListDTO otherTodoList = todoListTestMaster.createNewTodoList(newMember.getUsername(), newTeam.getId());

        TodoDTO newTodo = todoTestMaster.createNewTodo(newMember.getUsername(), newTeam.getId(), newTodoList.getId(), false);
        TodoDTO lockedTodo = todoTestMaster.createNewTodo(lockMember.getUsername(), newTeam.getId(), newTodoList.getId(), true);

        TodoInformationRequest request = new TodoInformationRequest();
        request.setTitle("Updated Title");
        request.setContent("Updated Content");
        request.setLocked(false);


        // request without token.
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), newTodoList.getId(), newTodo.getId()))
                .andExpect(status().isBadRequest());

        // request not existing team.
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", 123456789, newTodoList.getId(), newTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        // request not joined team.
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", otherTeam.getId(), newTodoList.getId(), newTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        // request not existing todolist.
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}",  newTeam.getId(), 123456789, newTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        // request different todolist.
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}",newTeam.getId(), otherTodoList.getId(), newTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        // request not existing to-do.
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}",  newTeam.getId(), newTodoList.getId(), 123456789)
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        // try locking, updating locked to-do not by writer.
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), newTodoList.getId(), lockedTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, otherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        // update locked to-do by writer.
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), newTodoList.getId(), lockedTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, lockToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(lockedTodo.getId()))
                .andExpect(jsonPath("$.result.content").value(request.getContent()));

        // override locked to-do by team leader.
        request.setContent("OVERRIDDEN");
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), newTodoList.getId(), lockedTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(lockedTodo.getId()))
                .andExpect(jsonPath("$.result.content").value("OVERRIDDEN"));

        // normal request.
        request.setContent("UPDATED_CONTENT");
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), newTodoList.getId(), newTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(newTodo.getId()))
                .andExpect(jsonPath("$.result.title").value(request.getTitle()))
                .andExpect(jsonPath("$.result.content").value(request.getContent()))
                .andDo(document("TeamTodoController/updateTodo",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        pathParameters(
                                RequestSnippets.teamIdPath,
                                RequestSnippets.todoListIdPath,
                                RequestSnippets.todoIdPath
                        ),
                        requestHeaders(
                                RequestSnippets.authorization
                        ),
                        requestFields(
                                RequestSnippets.Todo.title,
                                RequestSnippets.Todo.content,
                                RequestSnippets.Todo.locked
                        ),
                        responseFields(
                                ResponseSnippets.ApiResponseDescriptor.success,
                                ResponseSnippets.ApiResponseDescriptor.result,
                                ResponseSnippets.ApiResponseDescriptor.message,
                                EntityResponseSnippets.Todo.id,
                                EntityResponseSnippets.Todo.title,
                                EntityResponseSnippets.Todo.content,
                                EntityResponseSnippets.Todo.username,
                                EntityResponseSnippets.Todo.alias,
                                EntityResponseSnippets.Todo.locked)));

        // content update.
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), newTodoList.getId(), newTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(newTodo.getId()))
                .andExpect(jsonPath("$.result.title").value(request.getTitle()))
                .andExpect(jsonPath("$.result.content").value(request.getContent()));
    }

    @Test
    @DisplayName("Delete to-do.")
    void deleteTodo() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        MemberDTO otherMember = memberTestMaster.createNewMember();
        MemberDTO lockMember = memberTestMaster.createNewMember();

        String newToken = memberTestMaster.getRequestToken(newMember.getUsername(), newMember.getPassword());
        String lockToken = memberTestMaster.getRequestToken(lockMember.getUsername(), lockMember.getPassword());
        String otherToken = memberTestMaster.getRequestToken(otherMember.getUsername(), otherMember.getPassword());

        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUsername());
        TeamDTO otherTeam = teamTestMaster.createNewTeam(otherMember.getUsername());
        teamService.joinMember(newTeam.getId(), otherMember.getUsername());
        teamService.joinMember(newTeam.getId(), lockMember.getUsername());

        TodoListDTO newTodoList = todoListTestMaster.createNewTodoList(newMember.getUsername(), newTeam.getId());
        TodoListDTO otherTodoList = todoListTestMaster.createNewTodoList(newMember.getUsername(), newTeam.getId());

        TodoDTO newTodo = todoTestMaster.createNewTodo(newMember.getUsername(), newTeam.getId(), newTodoList.getId(), false);
        TodoDTO lockedTodo = todoTestMaster.createNewTodo(lockMember.getUsername(), newTeam.getId(), newTodoList.getId(), true);

        // request without token.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), newTodoList.getId(), newTodo.getId()))
                .andExpect(status().isBadRequest());

        // request not existing team.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", 123456789, newTodoList.getId(), newTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isNotFound());

        // request not joined team.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", otherTeam.getId(), newTodoList.getId(), newTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isForbidden());

        // request not existing todolist.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), 123456789, newTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isNotFound());

        // request different todolist.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), otherTodoList.getId(), newTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isNotFound());

        // request not existing to-do.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), newTodoList.getId(),123456789)
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isNotFound());

        TodoInformationRequest request = new TodoInformationRequest();
        request.setTitle(lockedTodo.getTitle());
        request.setContent(lockedTodo.getContent());
        request.setLocked(true);
        todoService.updateTodo(lockedTodo.getId(), request);
        // delete locked to-do by not writer.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), newTodoList.getId(), lockedTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, otherToken))
                .andExpect(status().isForbidden());

        // delete locked to-do by writer.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), newTodoList.getId(), lockedTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, lockToken))
                .andExpect(status().isOk());

        // override locked to-do by team leader.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), newTodoList.getId(), lockedTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isOk());


        // normal request.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), newTodoList.getId(), newTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isOk())
                .andDo(document("TeamTodoController/deleteTodo",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        pathParameters(
                                RequestSnippets.teamIdPath,
                                RequestSnippets.todoListIdPath,
                                RequestSnippets.todoIdPath
                        ),
                        requestHeaders(
                                RequestSnippets.authorization
                        )));
        long id = newTodo.getId();
        assertThrows(NoTodoFoundException.class, () -> todoService.readTodo(id));
    }
}
