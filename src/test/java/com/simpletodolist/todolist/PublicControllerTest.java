package com.simpletodolist.todolist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpletodolist.todolist.Snippets.EntityDescriptor;
import com.simpletodolist.todolist.Snippets.RequestSnippets;
import com.simpletodolist.todolist.controller.bind.MemberDTO;
import com.simpletodolist.todolist.dto.request.LoginRequestDTO;
import com.simpletodolist.todolist.dto.request.RegisterRequestDTO;
import com.simpletodolist.todolist.service.member.MemberService;
import com.simpletodolist.todolist.util.MemberTestMaster;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.simpletodolist.todolist.util.DocumentUtil.commonRequestPreprocessor;
import static com.simpletodolist.todolist.util.DocumentUtil.commonResponsePreprocessor;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs // default output directory is build/generated-snippets
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PublicControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    MemberService memberService;
    @Autowired
    ObjectMapper objectMapper;

    MemberTestMaster memberTestMaster;

    @BeforeAll
    public void init() {
        memberTestMaster = new MemberTestMaster(memberService);
    }

    @Test
    @DisplayName("Login Account")
    public void loginTest() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();

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
        mockMvc.perform(
                post("/api/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequestDTO(newMember.getUserId(), newMember.getPassword()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.userId").value(newMember.getUserId()))
                .andExpect(jsonPath("$.username").value(newMember.getUsername()))
                .andDo(document("PublicController/Login",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        requestFields(
                                RequestSnippets.Member.LoginUser.userId,
                                RequestSnippets.Member.LoginUser.password),
                        responseFields(
                                EntityDescriptor.Member.loginMemberInformation)));
    }

    @Test
    @DisplayName("Register Account")
    public void registerTest() throws Exception {
        String testUserId = "testUserId";
        String testUsername = "testUsername";
        String testPassword = "testPassword";

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
        mockMvc.perform(post("/api/public/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RegisterRequestDTO(testUserId, testUsername, testPassword))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(testUserId))
                .andExpect(jsonPath("$.username").value(testUsername))
                .andDo(document("PublicController/Register",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        requestFields(
                                RequestSnippets.Member.CreateUser.userId,
                                RequestSnippets.Member.CreateUser.username,
                                RequestSnippets.Member.CreateUser.password
                        ),
                        responseFields(
                                EntityDescriptor.Member.memberInformation
                        )));
    }
}
