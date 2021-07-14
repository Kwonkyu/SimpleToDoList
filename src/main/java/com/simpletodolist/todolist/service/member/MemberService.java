package com.simpletodolist.todolist.service.member;

import com.simpletodolist.todolist.domain.bind.TeamDTO;
import com.simpletodolist.todolist.exception.general.AuthenticationFailedException;
import com.simpletodolist.todolist.exception.member.DuplicatedMemberException;
import com.simpletodolist.todolist.exception.member.NoMemberFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

import static com.simpletodolist.todolist.domain.bind.MemberDTO.*;

public interface MemberService extends UserDetailsService {

    /**
     * Get general information of member.
     * @param memberUserId Member's user id.
     * @return MemberDTO object filled with found member's information.
     * @throws NoMemberFoundException when no member found with given user id.
     */
    Response getMemberDetails(String memberUserId) throws NoMemberFoundException;

    /**
     * Login user account with user id and password.
     * @param memberUserId Member's user id.
     * @param rawPassword Member's raw password.
     * @return LoginDTO object filled with logged in member's information and token.
     * @throws AuthenticationFailedException When password does not matched.
     */
    LoginResponse loginMember(String memberUserId, String rawPassword) throws AuthenticationFailedException;

    /**
     * Register new member.
     * @param memberDTO Registering member's information.
     * @return MemberDTO object filled with registered member's information.
     * @throws DuplicatedMemberException when member with equivalent user id exists.
     */
    Response registerMember(RegisterRequest registerRequest) throws DuplicatedMemberException;

    /**
     * Update information of member.
     * @param memberUserId Member's user id.
     * @param update Updatable field of member's information.
     * @param value Updated value.
     * @return MemberDTO object filled with updated user information.
     * @throws NoMemberFoundException when member with given id doesn't exists.
     */
    Response updateMember(String memberUserId, UpdateRequest.UpdatableMemberInformation update, Object value) throws NoMemberFoundException;

    /**
     * Withdraw member.
     * @param memberUserId Member to withdraw.
     * @throws NoMemberFoundException when given member does not exists.
     */
    void withdrawMember(String memberUserId) throws NoMemberFoundException;

    /**
     * Get list of teams members joined.
     * @param memberUserId Member's user id.
     * @return TeamsDTO object filled with found member's teams.
     * @throws NoMemberFoundException when no member found with given user id.
     */
    List<TeamDTO.Response> getTeamsOfMember(String memberUserId) throws NoMemberFoundException;
}
