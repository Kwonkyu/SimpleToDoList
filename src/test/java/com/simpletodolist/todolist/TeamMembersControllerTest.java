package com.simpletodolist.todolist;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpletodolist.todolist.Snippets.EntityDescriptor;
import com.simpletodolist.todolist.Snippets.RequestSnippets;
import com.simpletodolist.todolist.domain.bind.MemberDTO;
import com.simpletodolist.todolist.domain.bind.TeamDTO;
import com.simpletodolist.todolist.service.member.MemberService;
import com.simpletodolist.todolist.service.team.TeamService;
import com.simpletodolist.todolist.util.MemberTestMaster;
import com.simpletodolist.todolist.util.TeamTestMaster;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TeamMembersControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    MemberService memberService;
    @Autowired
    TeamService teamService;
    @Autowired
    ObjectMapper objectMapper;

    MemberTestMaster memberTestMaster;
    TeamTestMaster teamTestMaster;

    @BeforeAll
    public void init() {
        memberTestMaster = new MemberTestMaster(memberService);
        teamTestMaster = new TeamTestMaster(teamService);
    }

    @Test
    @DisplayName("Get members of team")
    public void getMembersOfTeam() throws Exception {
        MemberDTO.Response newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());
        MemberDTO.Response anotherMember = memberTestMaster.createNewMember();
        String anotherToken = memberTestMaster.getRequestToken(anotherMember.getUserId(), anotherMember.getPassword());
        TeamDTO.Response newTeam = teamTestMaster.createNewTeam(newMember.getUserId());

        // request without token
        mockMvc.perform(get("/api/team/{teamId}/members", newTeam.getId()))
                .andExpect(status().isUnauthorized());

        // request not joined team.
        mockMvc.perform(get("/api/team/{teamId}/members", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, anotherToken))
                .andExpect(status().isForbidden());

        // normal request.
        MvcResult mvcResult = mockMvc.perform(get("/api/team/{teamId}/members", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
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
                            EntityDescriptor.Team.members)))
                .andReturn();

        // check result contains member.
        List<MemberDTO.Response> membersDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});
        assertTrue(membersDTO.stream().anyMatch(dto -> dto.getUserId().equals(newMember.getUserId())));
    }

    @Test
    @DisplayName("Get member of team")
    public void getMember() throws Exception {
        MemberDTO.Response newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());
        MemberDTO.Response anotherMember = memberTestMaster.createNewMember();
        String anotherToken = memberTestMaster.getRequestToken(anotherMember.getUserId(), anotherMember.getPassword());
        TeamDTO.Response newTeam = teamTestMaster.createNewTeam(newMember.getUserId());

        // request without token
        mockMvc.perform(get("/api/team/{teamId}/members/{userId}", newTeam.getId(), newMember.getUserId()))
                .andExpect(status().isUnauthorized());

        // request not joined team.
        mockMvc.perform(get("/api/team/{teamId}/members/{userId}", newTeam.getId(), newMember.getUserId())
                .header(HttpHeaders.AUTHORIZATION, anotherToken))
                .andExpect(status().isForbidden());

        // request not joined member.
        mockMvc.perform(get("/api/team/{teamId}/members/{userId}", newTeam.getId(), anotherMember.getUserId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isForbidden());

        // normal request.
        mockMvc.perform(get("/api/team/{teamId}/members/{userId}", newTeam.getId(), newMember.getUserId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(newMember.getUserId()))
                .andDo(document("TeamMembersController/getMember",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        pathParameters(
                                RequestSnippets.teamIdPath,
                                RequestSnippets.userIdPath
                        ),
                        requestHeaders(
                                RequestSnippets.authorization
                        ),
                        responseFields(
                                EntityDescriptor.Member.teamMemberInformation)))
                .andReturn();
    }

    @Test
    @DisplayName("Join member to team")
    public void joinMember() throws Exception {
        MemberDTO.Response newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());
        MemberDTO.Response anotherMember = memberTestMaster.createNewMember();
        String anotherToken = memberTestMaster.getRequestToken(anotherMember.getUserId(), anotherMember.getPassword());
        TeamDTO.Response newTeam = teamTestMaster.createNewTeam(newMember.getUserId());
        teamService.joinMember(newTeam.getId(), anotherMember.getUserId());

        MemberDTO.Response joiningMember = memberTestMaster.createNewMember();
        String joiningToken = memberTestMaster.getRequestToken(joiningMember.getUserId(), joiningMember.getPassword());

        // request without token
        mockMvc.perform(put("/api/team/{teamId}/members/{userId}", newTeam.getId(), joiningMember.getUserId()))
                .andExpect(status().isUnauthorized());

        // request by not joined member
        mockMvc.perform(put("/api/team/{teamId}/members/{userId}", newTeam.getId(), joiningMember.getUserId())
                .header(HttpHeaders.AUTHORIZATION, joiningToken))
                .andExpect(status().isForbidden());

        // request by not leader
        mockMvc.perform(put("/api/team/{teamId}/members/{userId}", newTeam.getId(), joiningMember.getUserId())
                .header(HttpHeaders.AUTHORIZATION, anotherToken))
                .andExpect(status().isForbidden());

        // normal request.
        MvcResult mvcResult = mockMvc.perform(put("/api/team/{teamId}/members/{userId}", newTeam.getId(), joiningMember.getUserId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("$").isArray())
                .andDo(document("TeamMembersController/joinMember",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        requestHeaders(
                                RequestSnippets.authorization
                        ),
                        pathParameters(
                                RequestSnippets.teamIdPath,
                                RequestSnippets.userIdPath
                        ),
                        responseFields(
                                EntityDescriptor.Team.members)))
                .andReturn();

        // check result contains member.
        List<MemberDTO.Response> membersDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});
        assertTrue(membersDTO.stream().anyMatch(dto -> dto.getUserId().equals(joiningMember.getUserId())));
    }

    @Test
    @DisplayName("Withdraw member from team")
    public void withdrawMember() throws Exception {
        MemberDTO.Response newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());
        MemberDTO.Response anotherMember = memberTestMaster.createNewMember();
        String anotherToken = memberTestMaster.getRequestToken(anotherMember.getUserId(), anotherMember.getPassword());
        TeamDTO.Response newTeam = teamTestMaster.createNewTeam(newMember.getUserId());
        teamService.joinMember(newTeam.getId(), anotherMember.getUserId());

        MemberDTO.Response otherMember = memberTestMaster.createNewMember();
        String otherToken = memberTestMaster.getRequestToken(otherMember.getUserId(), otherMember.getPassword());

        // request without token
        mockMvc.perform(delete("/api/team/{teamId}/members/{userId}", newTeam.getId(), anotherMember.getUserId()))
                .andExpect(status().isUnauthorized());

        // withdraw not joined member.
        mockMvc.perform(delete("/api/team/{teamId}/members/{userId}", newTeam.getId(), anotherMember.getUserId())
                .header(HttpHeaders.AUTHORIZATION, otherToken))
                .andExpect(status().isForbidden());

        // withdraw by not leader.
        mockMvc.perform(delete("/api/team/{teamId}/members/{userId}", newTeam.getId(), anotherMember.getUserId())
                .header(HttpHeaders.AUTHORIZATION, anotherToken))
                .andExpect(status().isForbidden());

        // normal request.
        MvcResult mvcResult = mockMvc.perform(delete("/api/team/{teamId}/members/{userId}", newTeam.getId(), anotherMember.getUserId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isOk())
                // https://support.smartbear.com/alertsite/docs/monitors/api/endpoint/jsonpath.html
                .andExpect(jsonPath("$").isArray())
                .andDo(document("TeamMembersController/deleteMember",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
// https://github.com/spring-projects/spring-restdocs/issues/285 multiple path parameters can't be separated.
                        pathParameters(
                                RequestSnippets.teamIdPath,
                                RequestSnippets.userIdPath
                        ),
                        requestHeaders(
                                RequestSnippets.authorization
                        ),
                        responseFields(
                                EntityDescriptor.Team.members)))
                .andReturn();

        // check result contains member.
        List<MemberDTO.Response> membersDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});
        assertTrue(membersDTO.stream().noneMatch(dto -> dto.getUserId().equals(anotherMember.getUserId())));
    }

    @Test
    @DisplayName("Change team leader")
    public void changeLeader() throws Exception {
        MemberDTO.Response newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());
        MemberDTO.Response anotherMember = memberTestMaster.createNewMember();
        String anotherToken = memberTestMaster.getRequestToken(anotherMember.getUserId(), anotherMember.getPassword());
        TeamDTO.Response newTeam = teamTestMaster.createNewTeam(newMember.getUserId());
        teamService.joinMember(newTeam.getId(), anotherMember.getUserId());

        MemberDTO.Response otherMember = memberTestMaster.createNewMember();
        String otherToken = memberTestMaster.getRequestToken(otherMember.getUserId(), otherMember.getPassword());


        // request without token
        mockMvc.perform(put("/api/team/{teamId}/leader/{userId}", newTeam.getId(), anotherMember.getUserId()))
                .andExpect(status().isUnauthorized());

        // try making not existing member as leader.
        mockMvc.perform(put("/api/team/{teamId}/leader/{userId}", newTeam.getId(), "notexist")
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isNotFound());

        // try change leader by not leader.
        mockMvc.perform(put("/api/team/{teamId}/leader/{userId}", newTeam.getId(), anotherMember.getUserId())
                .header(HttpHeaders.AUTHORIZATION, anotherToken))
                .andExpect(status().isForbidden());

        // try change leader by not joined member.
        mockMvc.perform(put("/api/team/{teamId}/leader/{userId}", newTeam.getId(), anotherMember.getUserId())
                .header(HttpHeaders.AUTHORIZATION, otherToken))
                .andExpect(status().isForbidden());

        // normal request.
        mockMvc.perform(put("/api/team/{teamId}/leader/{userId}", newTeam.getId(), anotherMember.getUserId())
                .header(HttpHeaders.AUTHORIZATION, newToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamLeaderUserId").value(anotherMember.getUserId()))
                .andDo(document("TeamMembersController/changeLeader",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        pathParameters(
                                RequestSnippets.teamIdPath,
                                RequestSnippets.userIdPath
                        ),
                        requestHeaders(
                                RequestSnippets.authorization
                        ),
                        responseFields(
                                EntityDescriptor.Team.teamInformation)));
    }
}
