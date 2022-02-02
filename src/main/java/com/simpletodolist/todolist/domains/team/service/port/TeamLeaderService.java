package com.simpletodolist.todolist.domains.team.service.port;

import com.simpletodolist.todolist.domains.team.domain.Team;

public interface TeamLeaderService {

	/**
	 * Change leader of team.
	 * @param teamId Id of team.
	 * @param newLeaderUsername Username of leader. User must be member of team.
	 * @return Updated team's information.
	 */
	Team changeLeader(Long teamId, String newLeaderUsername);

}
