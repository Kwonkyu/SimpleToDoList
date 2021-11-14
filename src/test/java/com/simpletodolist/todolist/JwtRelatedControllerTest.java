package com.simpletodolist.todolist;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpletodolist.todolist.Snippets.EntityResponseSnippets;
import com.simpletodolist.todolist.Snippets.RequestSnippets;
import com.simpletodolist.todolist.Snippets.ResponseSnippets;
import com.simpletodolist.todolist.controller.bind.ApiResponse;
import com.simpletodolist.todolist.controller.bind.JwtRequest;
import com.simpletodolist.todolist.domain.bind.JWT;
import com.simpletodolist.todolist.domain.bind.MemberDTO;
import com.simpletodolist.todolist.security.JwtTokenUtil;
import com.simpletodolist.todolist.service.authorization.JwtService;
import com.simpletodolist.todolist.service.member.BasicMemberService;
import com.simpletodolist.todolist.util.MemberTestMaster;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static com.simpletodolist.todolist.util.DocumentUtil.commonRequestPreprocessor;
import static com.simpletodolist.todolist.util.DocumentUtil.commonResponsePreprocessor;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs // default output directory is build/generated-snippets
@ActiveProfiles("test")
@Transactional
class JwtRelatedControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    JwtService jwtService;
    @Autowired
    BasicMemberService memberService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    MemberTestMaster memberTestMaster;

    @BeforeEach
    void init() {
        memberTestMaster = new MemberTestMaster(memberService, jwtService);
    }

    @Test
    @DisplayName("Try using expired token.")
    void usingInvalidToken() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        JWT jwt = jwtService.issueNewJwt(newMember.getUsername());
        mockMvc.perform(get("/api/member")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt.getAccessToken()))
                .andExpect(status().isOk());

        jwtService.invalidateExistingUserJwt(newMember.getUsername());
        mockMvc.perform(get("/api/member")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt.getAccessToken()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Try using unregistered but valid token.")
    void usingUnregisteredValid() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        JWT jwt = jwtTokenUtil.generateJWT(newMember.getUsername());
        mockMvc.perform(get("/api/member")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt.getAccessToken()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Refresh with expired refresh token.")
    void refreshWithExpired() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        JWT jwt = jwtService.issueNewJwt(newMember.getUsername());

        JwtRequest request = new JwtRequest();
        request.setAccessToken(jwt.getAccessToken());
        request.setRefreshToken(jwt.getRefreshToken());

        mockMvc.perform(post("/api/token/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    ApiResponse<JWT> updatedJwt = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    request.setAccessToken(updatedJwt.getResult().getAccessToken());
                });

        mockMvc.perform(post("/api/token/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Refresh token.")
    void refreshToken() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        JWT jwt = jwtService.issueNewJwt(newMember.getUsername());

        JwtRequest jwtRequest = new JwtRequest();
        jwtRequest.setAccessToken(jwt.getAccessToken());
        jwtRequest.setRefreshToken(jwt.getRefreshToken());

        MvcResult mvcResult = mockMvc.perform(
                post("/api/token/refresh")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jwtRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.result.refreshToken").isNotEmpty())
                .andDo(document("JwtController/Refresh",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        requestFields(
                                RequestSnippets.Token.accessToken,
                                RequestSnippets.Token.refreshToken),
                        responseFields(
                                ResponseSnippets.ApiResponseDescriptor.success,
                                ResponseSnippets.ApiResponseDescriptor.result,
                                ResponseSnippets.ApiResponseDescriptor.message,
                                EntityResponseSnippets.JWT.accessToken,
                                EntityResponseSnippets.JWT.refreshToken)))
                .andReturn();
        JWT refreshedJwt = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), JWT.class);

        assertNotEquals(refreshedJwt.getAccessToken(), jwt.getAccessToken());
        assertNotEquals(refreshedJwt.getRefreshToken(), jwtRequest.getRefreshToken());
    }
}
