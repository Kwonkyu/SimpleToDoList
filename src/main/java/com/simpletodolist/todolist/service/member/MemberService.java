package com.simpletodolist.todolist.service.member;

import com.simpletodolist.todolist.domain.bind.MemberDTO;
import com.simpletodolist.todolist.domain.bind.TeamDTO;
import com.simpletodolist.todolist.exception.general.AuthenticationFailedException;
import com.simpletodolist.todolist.exception.member.DuplicatedMemberException;
import com.simpletodolist.todolist.exception.member.DuplicatedTeamJoinException;
import com.simpletodolist.todolist.exception.member.NoMemberFoundException;
import com.simpletodolist.todolist.exception.team.LockedTeamException;
import com.simpletodolist.todolist.exception.team.NoTeamFoundException;
import com.simpletodolist.todolist.exception.team.NotJoinedTeamException;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

import static com.simpletodolist.todolist.domain.bind.MemberDTO.*;

public interface MemberService extends UserDetailsService {

    /**
     * Get detailed information of member.
     * @param memberUserId Member's user id.
     * @return MemberDTO object filled with found member's information.
     * @throws NoMemberFoundException when no member found with given user id.
     */
    Response getMemberDetails(String memberUserId) throws NoMemberFoundException;

    /**
     * Get general information of member.
     * @param memberUserId Member's user id.
     * @return MemberDTO.Basic object filled with member's information.
     * @throws NoMemberFoundException when member with given id not found.
     */
    Basic getMemberInformation(String memberUserId) throws NoMemberFoundException;

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
     * @param registerRequest Registering member's information.
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
    Basic updateMember(String memberUserId, UpdateRequest.UpdatableMemberInformation update, Object value) throws NoMemberFoundException;

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
    List<TeamDTO.Basic> getTeamsOfMember(String memberUserId) throws NoMemberFoundException;

    /**
     * Join team.
     * @param teamId Team's id.
     * @return TeamDTO.TeamMembers object filled with team member's brief information.
     * @throws NoTeamFoundException when team with given id not found.
     * @throws LockedTeamException when team is locked.
     * @throws DuplicatedTeamJoinException when already joined team.
     */
    List<MemberDTO.Basic> joinTeam(long teamId, String userId) throws NoTeamFoundException, LockedTeamException, DuplicatedTeamJoinException;

    /**
     * Withdraw from team.
     * @param teamId Team's id.
     * @throws NoTeamFoundException when team with given id not found.
     * @throws NotJoinedTeamException when not joined team.
     */
    void withdrawTeam(long teamId, String userId) throws NoTeamFoundException, NotJoinedTeamException;
}
