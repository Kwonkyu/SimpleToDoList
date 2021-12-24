package com.simpletodolist.todolist;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpletodolist.todolist.Snippets.EntityResponseSnippets;
import com.simpletodolist.todolist.Snippets.RequestSnippets;
import com.simpletodolist.todolist.Snippets.ResponseSnippets;
import com.simpletodolist.todolist.common.bind.ApiResponse;
import com.simpletodolist.todolist.domains.member.bind.MemberDTO;
import com.simpletodolist.todolist.domains.team.bind.TeamDTO;
import com.simpletodolist.todolist.domains.jwt.service.JwtService;
import com.simpletodolist.todolist.domains.member.service.BasicMemberService;
import com.simpletodolist.todolist.domains.team.service.BasicTeamService;
import com.simpletodolist.todolist.util.MemberTestMaster;
import com.simpletodolist.todolist.util.TeamTestMaster;
import com.simpletodolist.todolist.util.container.TestMemberDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.simpletodolist.todolist.util.DocumentUtil.commonRequestPreprocessor;
import static com.simpletodolist.todolist.util.DocumentUtil.commonResponsePreprocessor;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ActiveProfiles("test")
@Transactional
class TeamMembersControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    BasicMemberService memberService;
    @Autowired
    BasicTeamService teamService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    JwtService jwtService;

    MemberTestMaster memberTestMaster;
    TeamTestMaster teamTestMaster;

    @BeforeEach
    void init() {
        memberTestMaster = new MemberTestMaster(memberService, jwtService);
        teamTestMaster = new TeamTestMaster(teamService);
    }

    @Test
    @DisplayName("Get members of team")
    void getMembersOfTeam() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUsername());
        MemberDTO anotherMember = memberTestMaster.createNewMember();
        String anotherToken = memberTestMaster.getRequestToken(anotherMember.getUsername());
        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUsername());

        // request without token
        mockMvc.perform(get("/api/team/{teamId}/members", newTeam.getId()))
                .andExpect(status().isBadRequest());

        // request not joined team.
        mockMvc.perform(get("/api/team/{teamId}/members", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, anotherToken))
                .andExpect(status().isForbidden());

        // normal request.
        MvcResult mvcResult = mockMvc.perform(get("/api/team/{teamId}/members", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isOk())
                .andDo(document("TeamMembersController/getMembers",
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
                                EntityResponseSnippets.Member.members)))
                .andReturn();

        // check result contains member.
        ApiResponse<List<TestMemberDTO>> membersDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertTrue(membersDTO.getResult().stream().anyMatch(dto -> dto.getUsername().equals(newMember.getUsername())));
    }

    @Test
    @DisplayName("Join member to team")
    void joinMember() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUsername());
        MemberDTO anotherMember = memberTestMaster.createNewMember();
        String anotherToken = memberTestMaster.getRequestToken(anotherMember.getUsername());
        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUsername());
        teamService.joinMember(newTeam.getId(), anotherMember.getUsername());

        MemberDTO joiningMember = memberTestMaster.createNewMember();
        String joiningToken = memberTestMaster.getRequestToken(joiningMember.getUsername());

        // request without token
        mockMvc.perform(put("/api/team/{teamId}/members/{username}", newTeam.getId(), joiningMember.getUsername()))
                .andExpect(status().isBadRequest());

        // request by not joined member
        mockMvc.perform(put("/api/team/{teamId}/members/{username}", newTeam.getId(), joiningMember.getUsername())
                .header(HttpHeaders.AUTHORIZATION, joiningToken))
                .andExpect(status().isForbidden());

        // request by not leader
        mockMvc.perform(put("/api/team/{teamId}/members/{username}", newTeam.getId(), joiningMember.getUsername())
                .header(HttpHeaders.AUTHORIZATION, anotherToken))
                .andExpect(status().isForbidden());

        // normal request.
        MvcResult mvcResult = mockMvc.perform(put("/api/team/{teamId}/members/{username}", newTeam.getId(), joiningMember.getUsername())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("$.result").isArray())
                .andDo(document("TeamMembersController/joinMember",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        requestHeaders(
                                RequestSnippets.authorization
                        ),
                        pathParameters(
                                RequestSnippets.teamIdPath,
                                RequestSnippets.usernamePath
                        ),
                        responseFields(
                                ResponseSnippets.ApiResponseDescriptor.success,
                                ResponseSnippets.ApiResponseDescriptor.result,
                                ResponseSnippets.ApiResponseDescriptor.message,
                                EntityResponseSnippets.Member.members)))
                .andReturn();

        // check result contains member.
        ApiResponse<List<TestMemberDTO>> membersDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertTrue(membersDTO.getResult().stream().anyMatch(dto -> dto.getUsername().equals(joiningMember.getUsername())));
    }

    @Test
    @DisplayName("Withdraw member from team")
    void withdrawMember() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUsername());
        MemberDTO anotherMember = memberTestMaster.createNewMember();
        String anotherToken = memberTestMaster.getRequestToken(anotherMember.getUsername());
        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUsername());
        teamService.joinMember(newTeam.getId(), anotherMember.getUsername());

        MemberDTO otherMember = memberTestMaster.createNewMember();
        String otherToken = memberTestMaster.getRequestToken(otherMember.getUsername());

        // request without token
        mockMvc.perform(delete("/api/team/{teamId}/members/{username}", newTeam.getId(), anotherMember.getUsername()))
                .andExpect(status().isBadRequest());

        // withdraw not joined member.
        mockMvc.perform(delete("/api/team/{teamId}/members/{username}", newTeam.getId(), anotherMember.getUsername())
                .header(HttpHeaders.AUTHORIZATION, otherToken))
                .andExpect(status().isForbidden());

        // withdraw by not leader.
        mockMvc.perform(delete("/api/team/{teamId}/members/{username}", newTeam.getId(), anotherMember.getUsername())
                .header(HttpHeaders.AUTHORIZATION, anotherToken))
                .andExpect(status().isForbidden());

        // normal request.
        MvcResult mvcResult = mockMvc.perform(delete("/api/team/{teamId}/members/{username}", newTeam.getId(), anotherMember.getUsername())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isOk())
                // https://support.smartbear.com/alertsite/docs/monitors/api/endpoint/jsonpath.html
                .andExpect(jsonPath("$.result").isArray())
                .andDo(document("TeamMembersController/deleteMember",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
// https://github.com/spring-projects/spring-restdocs/issues/285 multiple path parameters can't be separated.
                        pathParameters(
                                RequestSnippets.teamIdPath,
                                RequestSnippets.usernamePath
                        ),
                        requestHeaders(
                                RequestSnippets.authorization
                        ),
                        responseFields(
                                ResponseSnippets.ApiResponseDescriptor.success,
                                ResponseSnippets.ApiResponseDescriptor.result,
                                ResponseSnippets.ApiResponseDescriptor.message,
                                EntityResponseSnippets.Member.members)))
                .andReturn();

        // check result contains member.
        ApiResponse<List<TestMemberDTO>> membersDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertTrue(membersDTO.getResult().stream().noneMatch(dto -> dto.getUsername().equals(anotherMember.getUsername())));
    }

    @Test
    @DisplayName("Change team leader")
    void changeLeader() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUsername());
        MemberDTO anotherMember = memberTestMaster.createNewMember();
        String anotherToken = memberTestMaster.getRequestToken(anotherMember.getUsername());
        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUsername());
        teamService.joinMember(newTeam.getId(), anotherMember.getUsername());

        MemberDTO otherMember = memberTestMaster.createNewMember();
        String otherToken = memberTestMaster.getRequestToken(otherMember.getUsername());


        // request without token
        mockMvc.perform(put("/api/team/{teamId}/leader/{username}", newTeam.getId(), anotherMember.getUsername()))
                .andExpect(status().isBadRequest());

        // try making not existing member as leader.
        mockMvc.perform(put("/api/team/{teamId}/leader/{username}", newTeam.getId(), "notexist")
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isNotFound());

        // try change leader by not leader.
        mockMvc.perform(put("/api/team/{teamId}/leader/{username}", newTeam.getId(), anotherMember.getUsername())
                .header(HttpHeaders.AUTHORIZATION, anotherToken))
                .andExpect(status().isForbidden());

        // try change leader by not joined member.
        mockMvc.perform(put("/api/team/{teamId}/leader/{username}", newTeam.getId(), anotherMember.getUsername())
                .header(HttpHeaders.AUTHORIZATION, otherToken))
                .andExpect(status().isForbidden());

        // normal request.
        mockMvc.perform(put("/api/team/{teamId}/leader/{username}", newTeam.getId(), anotherMember.getUsername())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.leaderUsername").value(anotherMember.getUsername()))
                .andDo(document("TeamMembersController/changeLeader",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        pathParameters(
                                RequestSnippets.teamIdPath,
                                RequestSnippets.usernamePath
                        ),
                        requestHeaders(
                                RequestSnippets.authorization
                        ),

                        responseFields(
                                ResponseSnippets.ApiResponseDescriptor.success,
                                ResponseSnippets.ApiResponseDescriptor.result,
                                ResponseSnippets.ApiResponseDescriptor.message,
                                EntityResponseSnippets.Team.id,
                                EntityResponseSnippets.Team.teamName,
                                EntityResponseSnippets.Team.leaderUsername,
                                EntityResponseSnippets.Team.leaderAlias,
                                EntityResponseSnippets.Team.todoLists,
                                EntityResponseSnippets.Team.locked)));
    }
}
