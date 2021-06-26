package com.simpletodolist.todolist.service.team;

import com.simpletodolist.todolist.domain.dto.MembersDTO;
import com.simpletodolist.todolist.domain.dto.TeamDTO;
import com.simpletodolist.todolist.exception.team.DuplicatedMemberJoinException;
import com.simpletodolist.todolist.exception.member.InvalidTeamWithdrawException;
import com.simpletodolist.todolist.exception.member.NoMemberFoundException;
import com.simpletodolist.todolist.exception.team.NoTeamFoundException;

public interface TeamService {

    /**
     * Authorize team access as member.
     * @param memberUserId Member's user id.
     * @param teamId Team's id.
     * @return boolean value meaning whether member is in the team or not.
     * @throws NoMemberFoundException when member with given user id doesn't exists.
     * @throws NoTeamFoundException when team with given id doesn't exists.
     */
    boolean authorizeTeamMember(String memberUserId, long teamId) throws NoMemberFoundException, NoTeamFoundException;

    /**
     * Authorize team access as leader.
     * @param memberUserId Member's user id.
     * @param teamId Team's id.
     * @return boolean value meaning whether member is leader of the team or not.
     * @throws NoMemberFoundException when member with given user id doesn't exists.
     * @throws NoTeamFoundException when team with given id doesn't exists.
     */
    boolean authorizeTeamLeader(String memberUserId, long teamId) throws NoMemberFoundException, NoTeamFoundException;

    /**
     * Create new team.
     * @param teamDTO Registering team's information.
     * @return TeamDTO object filled with registered team's information.
     */
    TeamDTO createTeam(TeamDTO teamDTO);

    /**
     * Delete team.
     * @param teamId Deleting team's id.
     * @throws NoTeamFoundException when team does not exists.
     */
    void deleteTeam(long teamId) throws NoTeamFoundException;

    /**
     * Get information of team.
     * @param teamId Team's id.
     * @return TeamDTO object filled with found team's information.
     * @throws NoTeamFoundException when team with given id doesn't exists.
     */
    TeamDTO getTeamDetails(long teamId) throws NoTeamFoundException;

    /**
     * Get members information of team.
     * @param teamId Team's id to find members.
     * @return MembersDTO object filled with member's information.
     * @throws NoTeamFoundException when team with given id doesn't exists.
     */
    MembersDTO getTeamMembers(long teamId) throws NoTeamFoundException;

    /**
     * Join member to team.
     * @param teamId Team to join new member.
     * @param memberUserId Member to join team.
     * @return MembersDTO object filled with team member's information.
     * @throws NoMemberFoundException when joining member doesn't exist.
     * @throws NoTeamFoundException when joining team doesn't exist.
     * @throws DuplicatedMemberJoinException when member already joined team.
     */
    MembersDTO joinMember(long teamId, String memberUserId) throws NoMemberFoundException, NoTeamFoundException, DuplicatedMemberJoinException;

    /**
     * Withdraw member from team.
     * @param teamId Team to withdraw existing member.
     * @param memberUserId Member to withdraw.
     * @return MembersDTO object filled with team member's information.
     */
    MembersDTO withdrawMember(long teamId, String memberUserId) throws NoTeamFoundException, NoMemberFoundException, InvalidTeamWithdrawException;
}
