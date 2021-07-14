package com.simpletodolist.todolist.util;

import com.simpletodolist.todolist.domain.bind.TeamDTO;
import com.simpletodolist.todolist.service.team.TeamService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeamTestMaster {

    private final static Map<Long, TeamDTO.Response> teams = new HashMap<>();

    private final TeamService teamService;

    public TeamTestMaster(TeamService teamService) {
        this.teamService = teamService;
    }

    private String randomString(int length) {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, length % 32);
    }

    public TeamDTO.Response createNewTeam(String userId) {
        return createNewTeam(userId, randomString(31));
    }
    public TeamDTO.Response createNewTeam(String userId, String teamName) {
        TeamDTO.RegisterRequest teamDTO = TeamDTO.RegisterRequest.builder().teamName(teamName).build();
        TeamDTO.Response result = teamService.createTeam(userId, teamDTO);
        teams.put(result.getId(), result);
        return result;
    }

    public TeamDTO.Response getTeamInfo(long teamId) {
        return teams.get(teamId);
    }
}
