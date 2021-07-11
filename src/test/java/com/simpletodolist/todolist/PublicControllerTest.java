package com.simpletodolist.todolist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpletodolist.todolist.controller.bind.MemberDTO;
import com.simpletodolist.todolist.dto.request.LoginRequestDTO;
import com.simpletodolist.todolist.dto.request.RegisterRequestDTO;
import com.simpletodolist.todolist.service.member.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs // default output directory is build/generated-snippets
@ActiveProfiles("test")
public class PublicControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    MemberService memberService;
    @Autowired
    ObjectMapper objectMapper;


    @Test
    @DisplayName("Login Account")
    public void loginTest() throws Exception {
        String testUserId = "loginTestUserId";
        String testUsername = "loginTestUsername";
        String testPassword = "loginTestPassword";
        memberService.registerMember(new MemberDTO(testUserId, testUsername, testPassword));

        // not available http methods.
        mockMvc.perform(get("/api/public/login"))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(put("/api/public/login"))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(delete("/api/public/login"))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(patch("/api/public/login"))
                .andExpect(status().isMethodNotAllowed());

        // normal http request.
        MvcResult mvcResult = mockMvc.perform(
                post("/api/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequestDTO(testUserId, testPassword))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.userId").value(testUserId))
                .andExpect(jsonPath("$.username").value(testUsername))
                .andDo(document("PublicController/Login",
                        // TODO: pick these preprocess to configuration.
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        // relaxedRequestFields( // relaxed for not throwing errors for undocumented fields.
                        RequestSnippets.login,
                        ResponseSnippets.tokenInformation))
                .andReturn();
    }

    @Test
    @DisplayName("Register Account")
    public void registerTest() throws Exception {
        String testUserId = "testuserid";
        String testUsername = "testusername";
        String testPassword = "testpassword";

        // not available http methods.
        mockMvc.perform(get("/api/public/register"))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(put("/api/public/register"))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(delete("/api/public/register"))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(patch("/api/public/register"))
                .andExpect(status().isMethodNotAllowed());

        // normal http request.
        MvcResult mvcResult = mockMvc.perform(post("/api/public/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RegisterRequestDTO(testUserId, testUsername, testPassword))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(testUserId))
                .andExpect(jsonPath("$.username").value(testUsername))
                .andDo(document("PublicController/Register",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        // relaxedRequestFields( // relaxed for not throwing errors for undocumented fields.
                        RequestSnippets.register,
                        ResponseSnippets.memberInformation))
                .andReturn();
    }
}
