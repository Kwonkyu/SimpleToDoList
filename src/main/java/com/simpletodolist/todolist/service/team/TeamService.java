package com.simpletodolist.todolist.service.team;

import com.simpletodolist.todolist.domain.bind.*;
import com.simpletodolist.todolist.exception.member.NotJoinedMemberException;
import com.simpletodolist.todolist.exception.team.DuplicatedMemberJoinException;
import com.simpletodolist.todolist.exception.member.InvalidTeamWithdrawException;
import com.simpletodolist.todolist.exception.member.NoMemberFoundException;
import com.simpletodolist.todolist.exception.team.NoTeamFoundException;

import java.util.List;

import static com.simpletodolist.todolist.domain.bind.TeamDTO.*;

public interface TeamService {

    /**
     * Check whether team is locked or not.
     * @param teamId Team's id.
     * @return Boolean value indicating team is locked or not.
     * @throws NoTeamFoundException when team with given id not found.
     */
    boolean isTeamLocked(long teamId) throws NoTeamFoundException;

    /**
     * Search teams with given value of field.
     * @param field Type to find team(team's name, owner's user id, etc...)
     * @param value Value of field.
     * @param searcherUserId Searcher's user id.
     * @param includeJoined whether except joined team or not.
     * @return TeamsDTO object with teams.
     */
    List<BasicWithJoined> searchTeams(SearchRequest.SearchTeamField field, Object value, String searcherUserId, boolean includeJoined);

    /**
     * Create new team.
     * @param memberUserId Team leader's user id.
     * @param teamDTO Registering team's information.
     * @return TeamDTO object filled with registered team's information.
     * @throws NoMemberFoundException when member with given id doesn't exists.
     */
    Response createTeam(String memberUserId, RegisterRequest teamDTO) throws NoMemberFoundException;

    /**
     * Update team's information.
     * @param teamId Id of team to update.
     * @param field Updating field of team.
     * @param value Updated value of field.
     * @return TeamDTO object filled with updated team's information.
     * @throws NoTeamFoundException when team with given id doesn't exists.
     */
    Basic updateTeam(long teamId, UpdateRequest.UpdatableTeamInformation field, Object value) throws NoTeamFoundException;

    /**
     * Delete team.
     * @param teamId Deleting team's id.
     * @throws NoTeamFoundException when team does not exists.
     */
    void deleteTeam(long teamId) throws NoTeamFoundException;

    /**
     * Get detailed information of team.
     * @param teamId Team's id.
     * @return TeamDTO object filled with found team's information.
     * @throws NoTeamFoundException when team with given id doesn't exists.
     */
    Response getTeamDetails(long teamId) throws NoTeamFoundException;

    /**
     * Get general information of team.
     * @param teamId Team's id.
     * @return TeamDTO.Basic object filled with team's information.
     * @throws NoTeamFoundException when team with given id not found.
     */
    Basic getTeamInformation(long teamId) throws NoTeamFoundException;

    /**
     * Get members information of team.
     * @param teamId Team's id to find members.
     * @return MembersDTO object filled with member's information.
     * @throws NoTeamFoundException when team with given id doesn't exists.
     */
    List<MemberDTO.BasicWithTodoLists> getTeamMembers(long teamId) throws NoTeamFoundException;

    /**
     * Get member information(id, name, to-do list ids, names).
     * @param teamId Team's id.
     * @param userId Member's user id.
     * @return MemberDTO.BasicWithTodoLists object filled with member's id, name and to-do lists.
     * @throws NoTeamFoundException when team with given id not found.
     * @throws NoMemberFoundException when member with given id not found.
     * @throws NotJoinedMemberException when member is not joined to team.
     */
    MemberDTO.BasicWithTodoLists getTeamMemberInformation(long teamId, String userId) throws NoTeamFoundException, NoMemberFoundException, NotJoinedMemberException;

    /**
     * Get to-do lists of team.
     * @param teamId Team's id.
     * @return TodoListsDTO filled with team's to-do lists.
     * @throws NoTeamFoundException when team with given id not found.
     */
    List<TodoListDTO.Response> getTeamTodoLists(long teamId) throws NoTeamFoundException;

    /**
     * Join member to team.
     * @param teamId Team to join new member.
     * @param memberUserId Member to join team.
     * @return MembersDTO object filled with team member's information.
     * @throws NoMemberFoundException when joining member doesn't exist.
     * @throws NoTeamFoundException when joining team doesn't exist.
     * @throws DuplicatedMemberJoinException when member already joined team.
     */
    List<MemberDTO.Basic> joinMember(long teamId, String memberUserId) throws NoMemberFoundException, NoTeamFoundException, DuplicatedMemberJoinException;

    /**
     * Withdraw member from team.
     * @param teamId Team to withdraw existing member.
     * @param memberUserId Member to withdraw.
     * @return MembersDTO object filled with team member's information.
     */
    List<MemberDTO.Basic> withdrawMember(long teamId, String memberUserId) throws NoTeamFoundException, NoMemberFoundException, InvalidTeamWithdrawException;

    /**
     * Change leader of team.
     * @param teamId Id of team to change leader.
     * @param memberUserId New leader of team.
     * @throws NoTeamFoundException when team of given id doesn't exists.
     * @throws NoMemberFoundException when member of given id doesn't exists.
     * @throws NotJoinedMemberException when new leader member is not joined team.
     */
    Basic changeLeader(long teamId, String memberUserId) throws NoTeamFoundException, NoMemberFoundException, NotJoinedMemberException;
}
