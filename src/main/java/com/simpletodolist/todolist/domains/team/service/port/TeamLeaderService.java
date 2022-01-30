package com.simpletodolist.todolist.domains.team.service.port;

import com.simpletodolist.todolist.domains.team.domain.Team;

public interface TeamLeaderService {

	Team changeLeader(Long teamId, String newLeaderUsername);

}
