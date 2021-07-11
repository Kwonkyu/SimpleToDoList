package com.simpletodolist.todolist.service.team;

import com.simpletodolist.todolist.domain.UpdatableTeamInformation;
import com.simpletodolist.todolist.controller.bind.MembersDTO;
import com.simpletodolist.todolist.controller.bind.TeamDTO;
import com.simpletodolist.todolist.controller.bind.TodoListsDTO;
import com.simpletodolist.todolist.exception.member.NotJoinedMemberException;
import com.simpletodolist.todolist.exception.team.DuplicatedMemberJoinException;
import com.simpletodolist.todolist.exception.member.InvalidTeamWithdrawException;
import com.simpletodolist.todolist.exception.member.NoMemberFoundException;
import com.simpletodolist.todolist.exception.team.NoTeamFoundException;

public interface TeamService {

    /**
     * Check whether team is locked or not.
     * @param teamId Team's id.
     * @return Boolean value indicating team is locked or not.
     * @throws NoTeamFoundException when team with given id not found.
     */
    boolean isTeamLocked(long teamId) throws NoTeamFoundException;

    /**
     * Create new team.
     * @param memberUserId Team leader's user id.
     * @param teamDTO Registering team's information.
     * @return TeamDTO object filled with registered team's information.
     * @throws NoMemberFoundException when member with given id doesn't exists.
     */
    TeamDTO createTeam(String memberUserId, TeamDTO teamDTO) throws NoMemberFoundException;

    /**
     * Update team's information.
     * @param teamId Id of team to update.
     * @param field Updating field of team.
     * @param value Updated value of field.
     * @return TeamDTO object filled with updated team's information.
     * @throws NoTeamFoundException when team with given id doesn't exists.
     */
    TeamDTO updateTeam(long teamId, UpdatableTeamInformation field, Object value) throws NoTeamFoundException;

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
     * Get to-do lists of team.
     * @param teamId Team's id.
     * @return TodoListsDTO filled with team's to-do lists.
     * @throws NoTeamFoundException when team with given id not found.
     */
    TodoListsDTO getTeamTodoLists(long teamId) throws NoTeamFoundException;

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

    /**
     * Change leader of team.
     * @param teamId Id of team to change leader.
     * @param memberUserId New leader of team.
     * @throws NoTeamFoundException when team of given id doesn't exists.
     * @throws NoMemberFoundException when member of given id doesn't exists.
     * @throws NotJoinedMemberException when new leader member is not joined team.
     */
    TeamDTO changeLeader(long teamId, String memberUserId) throws NoTeamFoundException, NoMemberFoundException, NotJoinedMemberException;
}
