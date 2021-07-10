package com.simpletodolist.todolist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpletodolist.todolist.domain.UpdatableTeamInformation;
import com.simpletodolist.todolist.domain.dto.MemberDTO;
import com.simpletodolist.todolist.domain.dto.MemberJoinTeamDTO;
import com.simpletodolist.todolist.domain.dto.MembersDTO;
import com.simpletodolist.todolist.domain.dto.TeamDTO;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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
        MemberDTO newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());
        MemberDTO anotherMember = memberTestMaster.createNewMember();
        String anotherToken = memberTestMaster.getRequestToken(anotherMember.getUserId(), anotherMember.getPassword());
        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUserId());

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
                .andExpect(jsonPath("$.members").isArray())
                .andDo(document("TeamMembersController/getMembers",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("teamId").description("팀의 식별자입니다.")),
                        RequestSnippets.authorization,
                        ResponseSnippets.membersInformation))
                .andReturn();

        // check result contains member.
        MembersDTO membersDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), MembersDTO.class);
        assertTrue(membersDTO.getMembers().stream().anyMatch(dto -> dto.getUserId().equals(newMember.getUserId())));
    }

    @Test
    @DisplayName("Join member to team")
    public void joinMember() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());
        MemberDTO anotherMember = memberTestMaster.createNewMember();
        String anotherToken = memberTestMaster.getRequestToken(anotherMember.getUserId(), anotherMember.getPassword());
        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUserId());
        teamService.joinMember(newTeam.getId(), anotherMember.getUserId());

        MemberDTO joiningMember = memberTestMaster.createNewMember();
        String joiningToken = memberTestMaster.getRequestToken(joiningMember.getUserId(), joiningMember.getPassword());

        // request without token
        mockMvc.perform(post("/api/team/{teamId}/members", newTeam.getId()))
                .andExpect(status().isUnauthorized());

        // request by not joined member
        mockMvc.perform(post("/api/team/{teamId}/members", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, joiningToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new MemberJoinTeamDTO(joiningMember.getUserId()))))
                .andExpect(status().isForbidden());

        // request by not leader
        mockMvc.perform(post("/api/team/{teamId}/members", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, anotherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new MemberJoinTeamDTO(joiningMember.getUserId()))))
                .andExpect(status().isForbidden());

        // normal request.
        MvcResult mvcResult = mockMvc.perform(post("/api/team/{teamId}/members", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, newToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new MemberJoinTeamDTO(joiningMember.getUserId()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.members").isArray())
                .andDo(document("TeamMembersController/joinMember",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(parameterWithName("teamId").description("팀의 식별자입니다.")),
                        RequestSnippets.authorization,
                        RequestSnippets.userId,
                        ResponseSnippets.membersInformation))
                .andReturn();

        // check result contains member.
        MembersDTO membersDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), MembersDTO.class);
        assertTrue(membersDTO.getMembers().stream().anyMatch(dto -> dto.getUserId().equals(joiningMember.getUserId())));
    }

    @Test
    @DisplayName("Withdraw member from team")
    public void withdrawMember() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());
        MemberDTO anotherMember = memberTestMaster.createNewMember();
        String anotherToken = memberTestMaster.getRequestToken(anotherMember.getUserId(), anotherMember.getPassword());
        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUserId());
        teamService.joinMember(newTeam.getId(), anotherMember.getUserId());

        MemberDTO otherMember = memberTestMaster.createNewMember();
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
                .andExpect(jsonPath("$.members").isArray())
                .andDo(document("TeamMembersController/deleteMember",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
// https://github.com/spring-projects/spring-restdocs/issues/285 multiple path parameters can't be separated.
                        pathParameters(
                                parameterWithName("userId").description("사용자의 아이디입니다."),
                                parameterWithName("teamId").description("팀의 식별자입니다.")),
                        RequestSnippets.authorization,
                        ResponseSnippets.membersInformation))
                .andReturn();

        // check result contains member.
        MembersDTO membersDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), MembersDTO.class);
        assertTrue(membersDTO.getMembers().stream().noneMatch(dto -> dto.getUserId().equals(anotherMember.getUserId())));
    }

    @Test
    @DisplayName("Change team leader")
    public void changeLeader() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        String newToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());
        MemberDTO anotherMember = memberTestMaster.createNewMember();
        String anotherToken = memberTestMaster.getRequestToken(anotherMember.getUserId(), anotherMember.getPassword());
        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUserId());
        teamService.joinMember(newTeam.getId(), anotherMember.getUserId());

        MemberDTO otherMember = memberTestMaster.createNewMember();
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
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("userId").description("사용자의 아이디입니다."),
                                parameterWithName("teamId").description("팀의 식별자입니다.")),
                        RequestSnippets.authorization,
                        ResponseSnippets.teamInformation))
                .andReturn();
    }
}
