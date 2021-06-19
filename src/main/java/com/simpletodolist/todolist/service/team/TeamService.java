package com.simpletodolist.todolist.service.team;

import com.simpletodolist.todolist.domain.dto.MembersDTO;
import com.simpletodolist.todolist.domain.dto.TeamDTO;
import com.simpletodolist.todolist.exception.DuplicatedMemberJoinException;
import com.simpletodolist.todolist.exception.InvalidTeamWithdrawException;
import com.simpletodolist.todolist.exception.NoMemberFoundException;
import com.simpletodolist.todolist.exception.NoTeamFoundException;

public interface TeamService {

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
