package com.simpletodolist.todolist.controller;

import com.simpletodolist.todolist.domain.dto.MemberDTO;
import com.simpletodolist.todolist.domain.dto.MemberInformationUpdateRequestDTO;
import com.simpletodolist.todolist.domain.dto.TeamsDTO;
import com.simpletodolist.todolist.domain.dto.TodoListsDTO;
import com.simpletodolist.todolist.security.JwtTokenUtil;
import com.simpletodolist.todolist.service.member.MemberService;
import com.simpletodolist.todolist.service.todolist.TodoListService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final TodoListService todoListService;
    private final MemberService memberService;
    private final JwtTokenUtil jwtTokenUtil;
    private final MessageSource messageSource;


    private String constructMessage(String messageCode) {
        return messageSource.getMessage(messageCode, null, Locale.KOREAN);
    }


    /**
     * Get information of user based on given user id.
     * @param memberUserId User's user id.
     * @return 200 OK with body filled with user information.
     */
    @GetMapping("/{memberUserId}")
    public ResponseEntity<MemberDTO> memberInfo(@PathVariable(name = "memberUserId") String memberUserId,
                                                @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        jwtTokenUtil.validateRequestedUserIdWithJwt(memberUserId, jwt, constructMessage("unauthorized.member"));
        return ResponseEntity.ok(memberService.getMemberDetails(memberUserId));
    }



    @PutMapping("/{memberUserId}")
    public ResponseEntity<MemberDTO> updateMemberInfo(@PathVariable(name = "memberUserId") String memberUserId,
                                                      @RequestBody MemberInformationUpdateRequestDTO requestDTO,
                                                      @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        jwtTokenUtil.validateRequestedUserIdWithJwt(memberUserId, jwt, constructMessage("unauthorized.member"));
        return ResponseEntity.ok(memberService.updateMember(memberUserId, requestDTO.getField(), requestDTO.getValue()));
    }



    /**
     * Delete user.
     * @param memberUserId User's user id.
     */
    @DeleteMapping("/{memberId}")
    @ResponseStatus(HttpStatus.OK)
    public void withdrawMember(@PathVariable(name = "memberId") String memberUserId,
                               @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt){
        jwtTokenUtil.validateRequestedUserIdWithJwt(memberUserId, jwt, constructMessage("unauthorized.member"));
        memberService.withdrawMember(memberUserId);
    }


    /**
     * Get teams member joined.
     * @param memberUserId Member's user id.
     * @param jwt JWT
     * @return TeamsDTO object filled with member's teams.
     */
    @GetMapping("/{memberId}/teams")
    public ResponseEntity<TeamsDTO> getTeamsOfMember(@PathVariable(name = "memberId") String memberUserId,
                                                     @RequestHeader(HttpHeaders.AUTHORIZATION) String jwt) {
        jwtTokenUtil.validateRequestedUserIdWithJwt(memberUserId, jwt, constructMessage("unauthorized.member"));
        return ResponseEntity.ok(memberService.getTeamsOfMember(memberUserId));
    }
}
