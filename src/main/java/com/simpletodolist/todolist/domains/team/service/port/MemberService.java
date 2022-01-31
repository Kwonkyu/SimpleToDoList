package com.simpletodolist.todolist.domains.team.service.port;

import com.simpletodolist.todolist.domains.team.domain.Members;

public interface MemberService {

	/**
	 * Get members of team.
	 * @param teamId Id of team.
	 * @return Member list.
	 */
	Members getJoinedMembers(Long teamId);

	/**
	 * Invite user to team.
	 * @param teamId Id of team.
	 * @param invitedUsername Username of user.
	 * @return Member list with invited user.
	 */
	Members inviteMember(Long teamId, String invitedUsername);

	/**
	 * Withdraw member from team.
	 * @param teamId Id of team.
	 * @param withdrawnUsername Username of user.
	 * @return Member list without withdrawn user.
	 */
	Members withdrawMember(Long teamId, String withdrawnUsername);

}
