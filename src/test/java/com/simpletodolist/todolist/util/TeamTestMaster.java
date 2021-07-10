package com.simpletodolist.todolist.util;

import com.simpletodolist.todolist.domain.dto.MemberDTO;
import com.simpletodolist.todolist.domain.dto.TeamDTO;
import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.domain.entity.Team;
import com.simpletodolist.todolist.service.team.TeamService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeamTestMaster {

    private final static Map<Long, TeamDTO> teams = new HashMap<>();

    private final TeamService teamService;

    public TeamTestMaster(TeamService teamService) {
        this.teamService = teamService;
    }

    private String randomString(int length) {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, length % 32);
    }

    public TeamDTO createNewTeam(String userId) {
        return createNewTeam(userId, randomString(31));
    }
    public TeamDTO createNewTeam(String userId, String teamName) {
        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setTeamName(teamName);
        TeamDTO result = teamService.createTeam(userId, teamDTO);
        teams.put(result.getId(), result);
        return result;
    }

    public TeamDTO getTeamInfo(long teamId) {
        return teams.get(teamId);
    }
}
