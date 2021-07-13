package com.simpletodolist.todolist;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpletodolist.todolist.Snippets.EntityDescriptor;
import com.simpletodolist.todolist.Snippets.RequestSnippets;
import com.simpletodolist.todolist.controller.bind.MemberDTO;
import com.simpletodolist.todolist.controller.bind.MembersDTO;
import com.simpletodolist.todolist.controller.bind.TeamDTO;
import com.simpletodolist.todolist.controller.bind.TeamsDTO;
import com.simpletodolist.todolist.controller.bind.request.MemberInformationUpdateRequest;
import com.simpletodolist.todolist.controller.bind.request.field.UpdatableMemberInformation;
import com.simpletodolist.todolist.controller.bind.request.field.UpdatableTeamInformation;
import com.simpletodolist.todolist.exception.member.NoMemberFoundException;
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

import static com.simpletodolist.todolist.util.DocumentUtil.commonRequestPreprocessor;
import static com.simpletodolist.todolist.util.DocumentUtil.commonResponsePreprocessor;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
public class MemberControllerTest {

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
    @DisplayName("Get user information")
    public void getUserInfo() throws Exception {
        // request without token
        mockMvc.perform(get("/api/member"))
                .andExpect(status().isUnauthorized()); // by spring security, token-less request is not authorized.

        MemberDTO newMember = memberTestMaster.createNewMember();
        String requestToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());

        // normal request.
        mockMvc.perform(get("/api/member")
                .header(HttpHeaders.AUTHORIZATION, requestToken))
                .andExpect(status().isOk())
                // expression from https://goessner.net/articles/JsonPath/
                .andExpect(jsonPath("$.userId").value(newMember.getUserId()))
                .andExpect(jsonPath("$.username").value(newMember.getUsername()))
                .andDo(document("MemberController/readMemberInfo",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        requestHeaders(
                                RequestSnippets.authorization
                        ),
                        responseFields(
                                EntityDescriptor.Member.memberInformation
                        )));
    }

    @Test
    @DisplayName("Update user information")
    public void updateInformation() throws Exception{
        // request without token
        mockMvc.perform(patch("/api/member"))
                .andExpect(status().isUnauthorized());

        MemberDTO newMember = memberTestMaster.createNewMember();
        String requestToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());
        String updatedUsername = "newUsername";

        // normal request.
        mockMvc.perform(patch("/api/member")
                .header(HttpHeaders.AUTHORIZATION, requestToken)
                .content(objectMapper.writeValueAsString(new MemberInformationUpdateRequest(UpdatableMemberInformation.USERNAME, updatedUsername)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(newMember.getUserId()))
                .andExpect(jsonPath("$.username").value(updatedUsername))
                .andDo(document("MemberController/updateMemberInfo",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        requestHeaders(
                                RequestSnippets.authorization
                        ),
                        requestFields(
                                RequestSnippets.Member.UpdateUser.updateField,
                                RequestSnippets.Member.UpdateUser.updateValue
                        ),
                        responseFields(
                                EntityDescriptor.Member.memberInformation
                        )));
    }

    @Test
    @DisplayName("Delete user")
    public void deleteUser() throws Exception {
        // request without token
        mockMvc.perform(delete("/api/member"))
                .andExpect(status().isUnauthorized());

        MemberDTO newMember = memberTestMaster.createNewMember();
        String requestToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());

        // normal request.
        mockMvc.perform(delete("/api/member")
                .header(HttpHeaders.AUTHORIZATION, requestToken))
                .andExpect(status().isOk())
                .andDo(document("MemberController/deleteMember",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        requestHeaders(
                                RequestSnippets.authorization)));

        // check member information
        assertThrows(NoMemberFoundException.class, () -> memberService.getMemberDetails(newMember.getUserId()));
    }

    @Test
    @DisplayName("Get teams of member.")
    public void getTeams() throws Exception {
        MemberDTO newMember = memberTestMaster.createNewMember();
        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUserId());

        MemberDTO anotherMember = memberTestMaster.createNewMember();
        String anotherRequestToken = memberTestMaster.getRequestToken(anotherMember.getUserId(), anotherMember.getPassword());
        teamService.joinMember(newTeam.getId(), anotherMember.getUserId());

        // request without token
        mockMvc.perform(get("/api/member/teams"))
                .andExpect(status().isUnauthorized());

        // normal request.
        MvcResult mvcResult = mockMvc.perform(get("/api/member/teams")
                .header(HttpHeaders.AUTHORIZATION, anotherRequestToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teams").isArray())
                .andDo(document("MemberController/getMemberTeams",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        requestHeaders(
                                RequestSnippets.authorization
                        ),
                        responseFields(
                            EntityDescriptor.Team.teams
                        )))
                .andReturn();

        // check member information
        TeamsDTO teamsDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TeamsDTO.class);
        assertTrue(teamsDTO.getTeams().stream().anyMatch(dto -> dto.getTeamName().equals(newTeam.getTeamName())));
    }

    @Test
    @DisplayName("Join Team")
    public void joinTeam() throws Exception{
        MemberDTO newMember = memberTestMaster.createNewMember();
        String requestToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());
        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUserId());

        MemberDTO anotherMember = memberTestMaster.createNewMember();
        String anotherRequestToken = memberTestMaster.getRequestToken(anotherMember.getUserId(), anotherMember.getPassword());

        // request without token
        mockMvc.perform(put("/api/member/teams/{teamId}", newTeam.getId()))
                .andExpect(status().isUnauthorized());


        // try to join already joined team.
        mockMvc.perform(put("/api/member/teams/{teamId}", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, requestToken))
                .andExpect(status().isBadRequest());

        teamService.updateTeam(newTeam.getId(), UpdatableTeamInformation.LOCKED, true);

        // try to join locked team.
        mockMvc.perform(put("/api/member/teams/{teamId}", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, anotherRequestToken))
                .andExpect(status().isForbidden());


        teamService.updateTeam(newTeam.getId(), UpdatableTeamInformation.LOCKED, false);

        // normal request.
        MvcResult mvcResult = mockMvc.perform(put("/api/member/teams/{teamId}", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, anotherRequestToken))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("$.members").isArray())
                .andDo(document("MemberController/joinTeam",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        requestHeaders(
                                RequestSnippets.authorization
                        ),
                        pathParameters(
                                RequestSnippets.teamIdPath
                        ),
                        responseFields(
                                EntityDescriptor.Member.members
                        )))
                .andReturn();

        // check member is joined.
        MembersDTO membersDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), MembersDTO.class);
        assertTrue(membersDTO.getMembers().stream().anyMatch(dto -> dto.getUserId().equals(anotherMember.getUserId())));
    }

    @Test
    @DisplayName("Quit Team")
    public void quitTeam() throws Exception{
        MemberDTO newMember = memberTestMaster.createNewMember();
        String requestToken = memberTestMaster.getRequestToken(newMember.getUserId(), newMember.getPassword());
        TeamDTO newTeam = teamTestMaster.createNewTeam(newMember.getUserId());

        MemberDTO anotherMember = memberTestMaster.createNewMember();
        String anotherRequestToken = memberTestMaster.getRequestToken(anotherMember.getUserId(), anotherMember.getPassword());
        teamService.joinMember(newTeam.getId(), anotherMember.getUserId());

        TeamDTO anotherTeam = teamTestMaster.createNewTeam(anotherMember.getUserId());

        // TODO: 마지막 멤버가 탈퇴하면 팀을 아예 삭제.

        // request without token.
        mockMvc.perform(delete("/api/member/teams/{teamId}", newTeam.getId()))
                .andExpect(status().isUnauthorized());

        // withdraw from not joined team.
        mockMvc.perform(delete("/api/member/teams/{teamId}", anotherTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, requestToken))
                .andExpect(status().isBadRequest());

        // normal request.
        mockMvc.perform(delete("/api/member/teams/{teamId}", newTeam.getId())
                .header(HttpHeaders.AUTHORIZATION, anotherRequestToken))
                .andExpect(status().isOk())
                .andDo(document("MemberController/quitTeam",
                        commonRequestPreprocessor,
                        commonResponsePreprocessor,
                        requestHeaders(
                                RequestSnippets.authorization
                        ),
                        pathParameters(
                                RequestSnippets.teamIdPath)));

        // check member is quit.
        assertTrue(
                teamService.getTeamMembers(newTeam.getId()).getMembers().stream()
                        .noneMatch(dto -> dto.getUserId().equals(anotherMember.getUserId())));
    }
}
