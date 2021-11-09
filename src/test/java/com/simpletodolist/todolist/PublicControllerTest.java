package com.simpletodolist.todolist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpletodolist.todolist.Snippets.EntityResponseSnippets;
import com.simpletodolist.todolist.Snippets.RequestSnippets;
import com.simpletodolist.todolist.Snippets.ResponseSnippets;
import com.simpletodolist.todolist.controller.bind.member.MemberInformationRequest;
import com.simpletodolist.todolist.controller.bind.member.MemberLoginRequest;
import com.simpletodolist.todolist.domain.bind.MemberDTO;
import com.simpletodolist.todolist.security.JwtTokenUtil;
import com.simpletodolist.todolist.service.member.BasicMemberService;
import com.simpletodolist.todolist.util.MemberTestMaster;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.simpletodolist.todolist.util.DocumentUtil.commonRequestPreprocessor;
import static com.simpletodolist.todolist.util.DocumentUtil.commonResponsePreprocessor;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs // default output directory is build/generated-snippets
@ActiveProfiles("test")
@Transactional
class PublicControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    BasicMemberService memberService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    MemberTestMaster memberTestMaster;

    @BeforeEach
    void init() {
        memberTestMaster = new MemberTestMaster(memberService, jwtTokenUtil);
    }

    @Test
    @DisplayName("Login Account")
    void loginTest() throws Exception {
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
        MemberLoginRequest request = new MemberLoginRequest();
        request.setUsername(newMember.getUsername());
        request.setPassword(newMember.getPassword());

        mockMvc.perform(
                post("/api/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.result.username").value(newMember.getUsername()))
                .andExpect(jsonPath("$.result.alias").value(newMember.getAlias()))
                .andDo(document("PublicController/Login",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        requestFields(
                                RequestSnippets.Member.username,
                                RequestSnippets.Member.password),
                        responseFields(
                                ResponseSnippets.ApiResponseDescriptor.success,
                                ResponseSnippets.ApiResponseDescriptor.result,
                                ResponseSnippets.ApiResponseDescriptor.token,
                                EntityResponseSnippets.Member.id,
                                EntityResponseSnippets.Member.username,
                                EntityResponseSnippets.Member.alias,
                                EntityResponseSnippets.Member.password,
                                EntityResponseSnippets.Member.locked)));
    }

    @Test
    @DisplayName("Register Account")
    void registerTest() throws Exception {
        String testUsername = "testUsername";
        String testAlias = "testAlias";
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
        MemberInformationRequest request = new MemberInformationRequest();
        request.setUsername(testUsername);
        request.setAlias(testAlias);
        request.setPassword(testPassword);

        mockMvc.perform(post("/api/public/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.username").value(testUsername))
                .andExpect(jsonPath("$.result.alias").value(testAlias))
                .andDo(document("PublicController/Register",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        requestFields(
                                RequestSnippets.Member.username,
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
    }
}
