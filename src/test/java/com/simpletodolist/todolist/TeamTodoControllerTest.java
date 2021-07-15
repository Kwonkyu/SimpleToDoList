package com.simpletodolist.todolist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpletodolist.todolist.Snippets.EntityDescriptor;
import com.simpletodolist.todolist.Snippets.RequestSnippets;
import com.simpletodolist.todolist.domain.bind.MemberDTO;
import com.simpletodolist.todolist.domain.bind.TeamDTO;
import com.simpletodolist.todolist.domain.bind.TodoDTO;
import com.simpletodolist.todolist.domain.bind.TodoListDTO;
import com.simpletodolist.todolist.exception.todo.NoTodoFoundException;
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
    @DisplayName("Get todos of to-do list.")
    public void getTodos() throws Exception {
        MemberDTO.Response newMember = memberTestMaster.createNewMember();
        MemberDTO.Response otherMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());
        TeamDTO.Response newTeam = teamTestMaster.createNewTeam(newMember.getUserId());
        TeamDTO.Response otherTeam = teamTestMaster.createNewTeam(otherMember.getUserId());
        TodoListDTO.Response newTodoList = todoListTestMaster.createNewTodoList(newMember.getUserId(), newTeam.getId());
        todoTestMaster.createNewTodo(newMember.getUserId(), newTeam.getId(), newTodoList.getTodoListId());
        todoTestMaster.createNewTodo(newMember.getUserId(), newTeam.getId(), newTodoList.getTodoListId());
        todoTestMaster.createNewTodo(newMember.getUserId(), newTeam.getId(), newTodoList.getTodoListId());

        // request without token.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo", newTeam.getId(), newTodoList.getTodoListId()))
                .andExpect(status().isBadRequest());

        // request not existing team.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo", 123456789, newTodoList.getTodoListId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isNotFound());

        // request not joined team.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo", otherTeam.getId(), newTodoList.getTodoListId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isForbidden());

        // request not existing todolist.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo", newTeam.getId(), 123456789)
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isNotFound());

        // normal request.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo", newTeam.getId(), newTodoList.getTodoListId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
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
                                EntityDescriptor.Todo.todos)));
    }

    @Test
    @DisplayName("Get specific to-do.")
    public void getSpecificTodo() throws Exception {
        MemberDTO.Response newMember = memberTestMaster.createNewMember();
        MemberDTO.Response otherMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());
        TeamDTO.Response newTeam = teamTestMaster.createNewTeam(newMember.getUserId());
        TeamDTO.Response otherTeam = teamTestMaster.createNewTeam(otherMember.getUserId());
        TodoListDTO.Response newTodoList = todoListTestMaster.createNewTodoList(newMember.getUserId(), newTeam.getId());
        TodoListDTO.Response otherTodoList = todoListTestMaster.createNewTodoList(otherMember.getUserId(), newTeam.getId());
        TodoDTO.Response newTodo = todoTestMaster.createNewTodo(newMember.getUserId(), newTeam.getId(), newTodoList.getTodoListId());

        // request without token.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), newTodoList.getTodoListId(), newTodo.getId()))
                .andExpect(status().isBadRequest());

        // request not existing team.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", 123456789, newTodoList.getTodoListId(), newTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isNotFound());

        // request not joined team.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", otherTeam.getId(), newTodoList.getTodoListId(), newTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isForbidden());

        // request not existing todolist.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), 123456789, newTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isNotFound());

        // request different todolist.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), otherTodoList.getTodoListId(), newTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isNotFound());

        // request not existing to-do.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), newTodoList.getTodoListId(), 123456789)
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isNotFound());

        // normal request.
        mockMvc.perform(get("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), newTodoList.getTodoListId(), newTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(newTodo.getId()))
                .andExpect(jsonPath("$.title").value(newTodo.getTitle()))
                .andExpect(jsonPath("$.content").value(newTodo.getContent()))
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
                                EntityDescriptor.Todo.todoInformation
                        )));
    }

    @Test
    @DisplayName("Create to-do")
    public void createTodo() throws Exception {
        MemberDTO.Response newMember = memberTestMaster.createNewMember();
        MemberDTO.Response otherMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());
        TeamDTO.Response newTeam = teamTestMaster.createNewTeam(newMember.getUserId());
        TeamDTO.Response otherTeam = teamTestMaster.createNewTeam(otherMember.getUserId());
        TodoListDTO.Response newTodoList = todoListTestMaster.createNewTodoList(newMember.getUserId(), newTeam.getId());

        String requestContent = objectMapper.writeValueAsString(TodoDTO.Create.builder().title("title").content("content").build());

        // request without token.
        mockMvc.perform(post("/api/team/{teamId}/todolist/{todoListId}/todo", newTeam.getId(), newTodoList.getTodoListId()))
                .andExpect(status().isBadRequest());

        // request not existing team.
        mockMvc.perform(post("/api/team/{teamId}/todolist/{todoListId}/todo", 123456789, newTodoList.getTodoListId())
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent))
                .andExpect(status().isNotFound());

        // request not joined team.
        mockMvc.perform(post("/api/team/{teamId}/todolist/{todoListId}/todo", otherTeam.getId(), newTodoList.getTodoListId())
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent))
                .andExpect(status().isForbidden());

        // request not existing todolist.
        mockMvc.perform(post("/api/team/{teamId}/todolist/{todoListId}/todo", newTeam.getId(), 123456789)
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent))
                .andExpect(status().isNotFound());

        // normal request.
        mockMvc.perform(post("/api/team/{teamId}/todolist/{todoListId}/todo", newTeam.getId(), newTodoList.getTodoListId())
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.content").value("content"))
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
                                RequestSnippets.Todo.CreateTodo.title,
                                RequestSnippets.Todo.CreateTodo.content
                        ),
                        responseFields(
                                EntityDescriptor.Todo.todoInformation)));
    }

    @Test
    @DisplayName("Update to-do.")
    public void updateTodo() throws Exception {
        MemberDTO.Response newMember = memberTestMaster.createNewMember();
        MemberDTO.Response otherMember = memberTestMaster.createNewMember();
        MemberDTO.Response lockMember = memberTestMaster.createNewMember();

        String newToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());
        String lockToken = memberTestMaster.getRequestToken(lockMember.getUserId(), lockMember.getPassword());
        String otherToken = memberTestMaster.getRequestToken(otherMember.getUserId(), otherMember.getPassword());

        TeamDTO.Response newTeam = teamTestMaster.createNewTeam(newMember.getUserId());
        TeamDTO.Response otherTeam = teamTestMaster.createNewTeam(otherMember.getUserId());
        teamService.joinMember(newTeam.getId(), otherMember.getUserId());
        teamService.joinMember(newTeam.getId(), lockMember.getUserId());

        TodoListDTO.Response newTodoList = todoListTestMaster.createNewTodoList(newMember.getUserId(), newTeam.getId());
        TodoListDTO.Response otherTodoList = todoListTestMaster.createNewTodoList(newMember.getUserId(), newTeam.getId());

        TodoDTO.Response newTodo = todoTestMaster.createNewTodo(newMember.getUserId(), newTeam.getId(), newTodoList.getTodoListId());
        TodoDTO.Response lockedTodo = todoTestMaster.createNewTodo(lockMember.getUserId(), newTeam.getId(), newTodoList.getTodoListId());

        String updatedTitle = "Updated Title";
        TodoDTO.Update titleDTO = TodoDTO.Update.builder().field(TodoDTO.Update.UpdatableTodoInformation.TITLE).value(updatedTitle).build();

        String updatedContent = "Updated Content";
        TodoDTO.Update contentDTO = TodoDTO.Update.builder().field(TodoDTO.Update.UpdatableTodoInformation.CONTENT).value(updatedContent).build();

        // request without token.
        mockMvc.perform(patch("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), newTodoList.getTodoListId(), newTodo.getId()))
                .andExpect(status().isBadRequest());

        // request not existing team.
        mockMvc.perform(patch("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", 123456789, newTodoList.getTodoListId(), newTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(titleDTO)))
                .andExpect(status().isNotFound());

        // request not joined team.
        mockMvc.perform(patch("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", otherTeam.getId(), newTodoList.getTodoListId(), newTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(titleDTO)))
                .andExpect(status().isForbidden());

        // request not existing todolist.
        mockMvc.perform(patch("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}",  newTeam.getId(), 123456789, newTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(titleDTO)))
                .andExpect(status().isNotFound());

        // request different todolist.
        mockMvc.perform(patch("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}",newTeam.getId(), otherTodoList.getTodoListId(), newTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(titleDTO)))
                .andExpect(status().isNotFound());

        // request not existing to-do.
        mockMvc.perform(patch("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}",  newTeam.getId(), newTodoList.getTodoListId(), 123456789)
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(titleDTO)))
                .andExpect(status().isNotFound());

        // try lock by not writer.
        mockMvc.perform(patch("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), newTodoList.getTodoListId(), lockedTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, otherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TodoDTO.Update.builder()
                        .field(TodoDTO.Update.UpdatableTodoInformation.LOCKED)
                        .value(true)
                        .build())))
                .andExpect(status().isForbidden());

        todoService.updateTodo(lockedTodo.getId(), TodoDTO.Update.UpdatableTodoInformation.LOCKED, true);

        // updated locked to-do by not writer.
        mockMvc.perform(patch("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), newTodoList.getTodoListId(), lockedTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, otherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TodoDTO.Update.builder()
                        .field(TodoDTO.Update.UpdatableTodoInformation.CONTENT)
                        .value("updated content")
                        .build())))
                .andExpect(status().isForbidden());

        // update locked to-do by writer.
        mockMvc.perform(patch("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), newTodoList.getTodoListId(), lockedTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, lockToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TodoDTO.Update.builder()
                        .field(TodoDTO.Update.UpdatableTodoInformation.CONTENT)
                        .value("updated content")
                        .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(lockedTodo.getId()))
                .andExpect(jsonPath("$.content").value("updated content"));

        // override locked to-do by team leader.
        mockMvc.perform(patch("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), newTodoList.getTodoListId(), lockedTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TodoDTO.Update.builder()
                        .field(TodoDTO.Update.UpdatableTodoInformation.CONTENT)
                        .value("OVERRIDDEN")
                .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(lockedTodo.getId()))
                .andExpect(jsonPath("$.content").value("OVERRIDDEN"));

        // normal request.
        mockMvc.perform(patch("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), newTodoList.getTodoListId(), newTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(titleDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(newTodo.getId()))
                .andExpect(jsonPath("$.title").value(updatedTitle))
                .andExpect(jsonPath("$.content").value(newTodo.getContent()))
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
                                RequestSnippets.Todo.UpdateTodo.field,
                                RequestSnippets.Todo.UpdateTodo.value
                        ),
                        responseFields(
                                EntityDescriptor.Todo.todoInformation)));

        // content update.
        mockMvc.perform(patch("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), newTodoList.getTodoListId(), newTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contentDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(newTodo.getId()))
                .andExpect(jsonPath("$.title").value(updatedTitle))
                .andExpect(jsonPath("$.content").value(updatedContent));
    }

    @Test
    @DisplayName("Delete to-do.")
    public void deleteTodo() throws Exception {
        MemberDTO.Response newMember = memberTestMaster.createNewMember();
        MemberDTO.Response otherMember = memberTestMaster.createNewMember();
        MemberDTO.Response lockMember = memberTestMaster.createNewMember();

        String newToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());
        String lockToken = memberTestMaster.getRequestToken(lockMember.getUserId(), lockMember.getPassword());
        String otherToken = memberTestMaster.getRequestToken(otherMember.getUserId(), otherMember.getPassword());

        TeamDTO.Response newTeam = teamTestMaster.createNewTeam(newMember.getUserId());
        TeamDTO.Response otherTeam = teamTestMaster.createNewTeam(otherMember.getUserId());
        teamService.joinMember(newTeam.getId(), otherMember.getUserId());
        teamService.joinMember(newTeam.getId(), lockMember.getUserId());

        TodoListDTO.Response newTodoList = todoListTestMaster.createNewTodoList(newMember.getUserId(), newTeam.getId());
        TodoListDTO.Response otherTodoList = todoListTestMaster.createNewTodoList(newMember.getUserId(), newTeam.getId());

        TodoDTO.Response newTodo = todoTestMaster.createNewTodo(newMember.getUserId(), newTeam.getId(), newTodoList.getTodoListId());
        TodoDTO.Response lockedTodo = todoTestMaster.createNewTodo(lockMember.getUserId(), newTeam.getId(), newTodoList.getTodoListId());

        // request without token.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), newTodoList.getTodoListId(), newTodo.getId()))
                .andExpect(status().isBadRequest());

        // request not existing team.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", 123456789, newTodoList.getTodoListId(), newTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isNotFound());

        // request not joined team.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", otherTeam.getId(), newTodoList.getTodoListId(), newTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isForbidden());

        // request not existing todolist.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), 123456789, newTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isNotFound());

        // request different todolist.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), otherTodoList.getTodoListId(), newTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isNotFound());

        // request not existing to-do.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), newTodoList.getTodoListId(),123456789)
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isNotFound());


        todoService.updateTodo(lockedTodo.getId(), TodoDTO.Update.UpdatableTodoInformation.LOCKED, true);
        // delete locked to-do by not writer.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), newTodoList.getTodoListId(), lockedTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, otherToken))
                .andExpect(status().isForbidden());

        // delete locked to-do by writer.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), newTodoList.getTodoListId(), lockedTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, lockToken))
                .andExpect(status().isOk());

        lockedTodo = todoTestMaster.createNewTodo(lockMember.getUserId(), newTeam.getId(), newTodoList.getTodoListId());
        todoService.updateTodo(lockedTodo.getId(), TodoDTO.Update.UpdatableTodoInformation.LOCKED, true);
        // override locked to-do by team leader.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), newTodoList.getTodoListId(), lockedTodo.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isOk());


        // normal request.
        mockMvc.perform(delete("/api/team/{teamId}/todolist/{todoListId}/todo/{todoId}", newTeam.getId(), newTodoList.getTodoListId(), newTodo.getId())
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

        assertThrows(NoTodoFoundException.class, () -> todoService.readTodo(newTodo.getId()));
    }
}
