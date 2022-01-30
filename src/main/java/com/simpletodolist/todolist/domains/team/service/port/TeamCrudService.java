package com.simpletodolist.todolist.domains.team.service.port;

import com.simpletodolist.todolist.domains.team.adapter.controller.command.TeamCreateRequest;
import com.simpletodolist.todolist.domains.team.adapter.controller.command.TeamUpdateRequest;
import com.simpletodolist.todolist.domains.team.adapter.controller.command.TeamSearchRequest;
import com.simpletodolist.todolist.domains.team.domain.SearchedTeams;
import com.simpletodolist.todolist.domains.team.domain.Team;

public interface TeamCrudService {

	Team getTeamInformation(Long teamId);

	SearchedTeams searchTeams(TeamSearchRequest request, String username);

	Team createTeam(TeamCreateRequest request, String leaderUsername);

	Team updateTeam(Long teamId, TeamUpdateRequest request);

	void deleteTeam(Long teamId);

}
