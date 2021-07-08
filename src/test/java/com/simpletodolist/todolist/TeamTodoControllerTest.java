package com.simpletodolist.todolist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpletodolist.todolist.domain.UpdatableTeamInformation;
import com.simpletodolist.todolist.domain.UpdatableTodoInformation;
import com.simpletodolist.todolist.domain.UpdatableTodoListInformation;
import com.simpletodolist.todolist.domain.dto.*;
import com.simpletodolist.todolist.domain.entity.Todo;
import com.simpletodolist.todolist.dto.request.TodoCreateDTO;
import com.simpletodolist.todolist.dto.request.TodoListCreateDTO;
import com.simpletodolist.todolist.service.member.MemberService;
import com.simpletodolist.todolist.service.team.TeamService;
import com.simpletodolist.todolist.service.todo.TodoService;
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
public class TeamTodoControllerTest {

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

    String userId = "writeruserid";
    String userToken;

    long joinedTeamId;
    long notJoinedTeamId;


    @BeforeAll
    public void init() {
        memberService.registerMember(new MemberDTO(userId, "username", "pw"));
        userToken = String.format("Bearer %s", memberService.loginMember(userId, "pw").getToken());

        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setTeamName("myTeam");
        joinedTeamId = teamService.createTeam(userId, teamDTO).getId();

        memberService.registerMember(new MemberDTO("others", "username", "pw"));
        teamDTO.setTeamName("othersTeam");
        notJoinedTeamId = teamService.createTeam("others",teamDTO).getId();
    }

    @Test
    @DisplayName("Get todos of to-do list.")
    public void getTodos() throws Exception {
        TodoListDTO todoListDTO = new TodoListDTO();
        todoListDTO.setTodoListName("mytodolist");
        long todoListId = todoListService.createTodoList(joinedTeamId, userId, todoListDTO).getTodoListId();
        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle("title");
        todoDTO.setContent("content");
        todoService.writeTodo(userId, todoListId, todoDTO);

        // request without token.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo", joinedTeamId, todoListId))
                .andExpect(status().isUnauthorized());

        // request not existing team.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo", 123456789, todoListId)
                .header(HttpHeaders.AUTHORIZATION, userToken))
                .andExpect(status().isNotFound());

        // request not joined team.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo", notJoinedTeamId, todoListId)
                .header(HttpHeaders.AUTHORIZATION, userToken))
                .andExpect(status().isForbidden());

        // request not existing todolist.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo", joinedTeamId, 123456789)
                .header(HttpHeaders.AUTHORIZATION, userToken))
                .andExpect(status().isNotFound());

        // normal request.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo", joinedTeamId, todoListId)
                .header(HttpHeaders.AUTHORIZATION, userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.todos").isArray())
                .andDo(document("TeamTodoController/getTodos",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        RequestSnippets.teamIdAndTodoListIdPath,
                        RequestSnippets.authorization,
                        ResponseSnippets.todos));
    }

    @Test
    @DisplayName("Get specific to-do.")
    public void getSpecificTodo() throws Exception {
        TodoListDTO todoListDTO = new TodoListDTO();
        todoListDTO.setTodoListName("mytodolist");
        long todoListId = todoListService.createTodoList(joinedTeamId, userId, todoListDTO).getTodoListId();
        long otherTodoListId = todoListService.createTodoList(joinedTeamId, userId, todoListDTO).getTodoListId();
        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle("title");
        todoDTO.setContent("content");
        long todoId = todoService.writeTodo(userId, todoListId, todoDTO).getId();

        // request without token.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", joinedTeamId, todoListId, todoId))
                .andExpect(status().isUnauthorized());

        // request not existing team.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", 123456789, todoListId, todoId)
                .header(HttpHeaders.AUTHORIZATION, userToken))
                .andExpect(status().isNotFound());

        // request not joined team.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", notJoinedTeamId, todoListId, todoId)
                .header(HttpHeaders.AUTHORIZATION, userToken))
                .andExpect(status().isForbidden());

        // request not existing todolist.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", joinedTeamId, 123456789, todoId)
                .header(HttpHeaders.AUTHORIZATION, userToken))
                .andExpect(status().isNotFound());

        // request different todolist.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", joinedTeamId, otherTodoListId, todoId)
                .header(HttpHeaders.AUTHORIZATION, userToken))
                .andExpect(status().isNotFound());

        // request not existing to-do.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", joinedTeamId, todoListId, 123456789)
                .header(HttpHeaders.AUTHORIZATION, userToken))
                .andExpect(status().isNotFound());

        // normal request.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", joinedTeamId, todoListId, todoId)
                .header(HttpHeaders.AUTHORIZATION, userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(todoId))
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.content").value("content"))
                .andDo(document("TeamTodoController/getTodo",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        RequestSnippets.teamIdAndTodoListIdAndTodoIdPath,
                        RequestSnippets.authorization,
                        ResponseSnippets.todo));
    }

    @Test
    @DisplayName("Create to-do")
    public void createTodo() throws Exception {
        TodoListDTO todoListDTO = new TodoListDTO();
        todoListDTO.setTodoListName("mytodolist");
        long todoListId = todoListService.createTodoList(joinedTeamId, userId, todoListDTO).getTodoListId();

        TodoCreateDTO dto = new TodoCreateDTO("title", "content");

        // request without token.
        mockMvc.perform(post("/api/team/{teamId}/todolist/{todoListId}/todo", joinedTeamId, todoListId))
                .andExpect(status().isUnauthorized());

        // request not existing team.
        mockMvc.perform(post("/api/team/{teamId}/todolist/{todoListId}/todo", 123456789, todoListId)
                .header(HttpHeaders.AUTHORIZATION, userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());

        // request not joined team.
        mockMvc.perform(post("/api/team/{teamId}/todolist/{todoListId}/todo", notJoinedTeamId, todoListId)
                .header(HttpHeaders.AUTHORIZATION, userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());

        // request not existing todolist.
        mockMvc.perform(post("/api/team/{teamId}/todolist/{todoListId}/todo", joinedTeamId, 123456789)
                .header(HttpHeaders.AUTHORIZATION, userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());

        // normal request.
        mockMvc.perform(post("/api/team/{teamId}/todolist/{todoListId}/todo", joinedTeamId, todoListId)
                .header(HttpHeaders.AUTHORIZATION, userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.content").value("content"))
                .andDo(document("TeamTodoController/createTodo",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        RequestSnippets.teamIdAndTodoListIdPath,
                        RequestSnippets.authorization,
                        RequestSnippets.todo,
                        ResponseSnippets.todo));
    }

    @Test
    @DisplayName("Update to-do.")
    public void updateTodo() throws Exception {
        TodoListDTO todoListDTO = new TodoListDTO();
        todoListDTO.setTodoListName("mytodolist");
        long todoListId = todoListService.createTodoList(joinedTeamId, userId, todoListDTO).getTodoListId();
        long otherTodoListId = todoListService.createTodoList(joinedTeamId, userId, todoListDTO).getTodoListId();
        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle("title");
        todoDTO.setContent("content");
        long todoId = todoService.writeTodo(userId, todoListId, todoDTO).getId();

        String updatedTitle = "updatedTitle";
        String updatedContent = "updatedContent";

        TodoInformationUpdateRequestDTO titleDTO = new TodoInformationUpdateRequestDTO(
                UpdatableTodoInformation.TITLE, updatedTitle
        );
        TodoInformationUpdateRequestDTO contentDTO = new TodoInformationUpdateRequestDTO(
                UpdatableTodoInformation.CONTENT, updatedContent
        );

        // request without token.
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", joinedTeamId, todoListId, todoId))
                .andExpect(status().isUnauthorized());

        // request not existing team.
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", 123456789, todoListId, todoId)
                .header(HttpHeaders.AUTHORIZATION, userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(titleDTO)))
                .andExpect(status().isNotFound());

        // request not joined team.
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", notJoinedTeamId, todoListId, todoId)
                .header(HttpHeaders.AUTHORIZATION, userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(titleDTO)))
                .andExpect(status().isForbidden());

        // request not existing todolist.
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", joinedTeamId, 123456789, todoId)
                .header(HttpHeaders.AUTHORIZATION, userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(titleDTO)))
                .andExpect(status().isNotFound());

        // request different todolist.
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", joinedTeamId, otherTodoListId, todoId)
                .header(HttpHeaders.AUTHORIZATION, userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(titleDTO)))
                .andExpect(status().isNotFound());

        // request not existing to-do.
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", joinedTeamId, todoListId, 123456789)
                .header(HttpHeaders.AUTHORIZATION, userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(titleDTO)))
                .andExpect(status().isNotFound());

        // normal request.
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", joinedTeamId, todoListId, todoId)
                .header(HttpHeaders.AUTHORIZATION, userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(titleDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(todoId))
                .andExpect(jsonPath("$.title").value(updatedTitle))
                .andExpect(jsonPath("$.content").value("content"))
                .andDo(document("TeamTodoController/updateTodo",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        RequestSnippets.teamIdAndTodoListIdAndTodoIdPath,
                        RequestSnippets.authorization,
                        RequestSnippets.updateTodo,
                        ResponseSnippets.todo));

        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", joinedTeamId, todoListId, todoId)
                .header(HttpHeaders.AUTHORIZATION, userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contentDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(todoId))
                .andExpect(jsonPath("$.title").value(updatedTitle))
                .andExpect(jsonPath("$.content").value(updatedContent));
    }

    @Test
    @DisplayName("Delete to-do.")
    public void deleteTodo() throws Exception {
        TodoListDTO todoListDTO = new TodoListDTO();
        todoListDTO.setTodoListName("mytodolist");
        long todoListId = todoListService.createTodoList(joinedTeamId, userId, todoListDTO).getTodoListId();
        long otherTodoListId = todoListService.createTodoList(joinedTeamId, userId, todoListDTO).getTodoListId();
        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle("title");
        todoDTO.setContent("content");
        long todoId = todoService.writeTodo(userId, todoListId, todoDTO).getId();

        // request without token.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", joinedTeamId, todoListId, todoId))
                .andExpect(status().isUnauthorized());

        // request not existing team.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", 123456789, todoListId, todoId)
                .header(HttpHeaders.AUTHORIZATION, userToken))
                .andExpect(status().isNotFound());

        // request not joined team.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", notJoinedTeamId, todoListId, todoId)
                .header(HttpHeaders.AUTHORIZATION, userToken))
                .andExpect(status().isForbidden());

        // request not existing todolist.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", joinedTeamId, 123456789, todoId)
                .header(HttpHeaders.AUTHORIZATION, userToken))
                .andExpect(status().isNotFound());

        // request different todolist.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", joinedTeamId, otherTodoListId, todoId)
                .header(HttpHeaders.AUTHORIZATION, userToken))
                .andExpect(status().isNotFound());

        // request not existing to-do.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", joinedTeamId, todoListId, 123456789)
                .header(HttpHeaders.AUTHORIZATION, userToken))
                .andExpect(status().isNotFound());

        // normal request.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", joinedTeamId, todoListId, todoId)
                .header(HttpHeaders.AUTHORIZATION, userToken))
                .andExpect(status().isOk())
                .andDo(document("TeamTodoController/deleteTodo",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        RequestSnippets.teamIdAndTodoListIdAndTodoIdPath,
                        RequestSnippets.authorization));
    }

    @Test
    @DisplayName("Update or delete locked to-do.")
    public void denyLocked() throws Exception {
        // TODO: 이런것도 계속 새로 생성하기 귀찮으니 계정이나 팀, 할 일 리스트를 생성해주고 비밀번호도 내부적으로 맵으로 관리해주는 클래스를 구축.
        String otherMemberId = "otherMember";
        memberService.registerMember(new MemberDTO(otherMemberId, "name", "pw"));
        String otherToken = String.format("Bearer %s", memberService.loginMember(otherMemberId, "pw").getToken());

        TodoListDTO todoListDTO = new TodoListDTO();
        todoListDTO.setTodoListName("mytodolist");
        long todoListId = todoListService.createTodoList(joinedTeamId, userId, todoListDTO).getTodoListId();
        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setTitle("title");
        todoDTO.setContent("content");
        long todoId = todoService.writeTodo(userId, todoListId, todoDTO).getId();
        todoService.updateTodo(todoId, UpdatableTodoInformation.LOCKED, true);

        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", joinedTeamId, todoListId, todoId)
                .header(HttpHeaders.AUTHORIZATION, otherToken))
                .andExpect(status().isForbidden());

        TodoInformationUpdateRequestDTO dto = new TodoInformationUpdateRequestDTO(UpdatableTodoInformation.LOCKED, false);
        mockMvc.perform(put("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", joinedTeamId, todoListId, todoId)
                .header(HttpHeaders.AUTHORIZATION, otherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }
}
