package com.simpletodolist.todolist.util;

import com.simpletodolist.todolist.domains.team.bind.request.TeamInformationRequest;
import com.simpletodolist.todolist.domains.team.bind.TeamDTO;
import com.simpletodolist.todolist.domains.team.service.BasicTeamService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeamTestMaster {
    private final BasicTeamService teamService;

    private final static Map<Long, TeamDTO> teams = new HashMap<>();

    public TeamTestMaster(BasicTeamService teamService) {
        this.teamService = teamService;
    }

    private String randomString(int length) {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, length % 32);
    }

    public TeamDTO createNewTeam(String userId) {
        return createNewTeam(userId, randomString(31));
    }
    public TeamDTO createNewTeam(String userId, String teamName) {
        TeamInformationRequest request = new TeamInformationRequest();
        request.setTeamName(teamName);
        request.setLocked(false);
        TeamDTO result = teamService.createTeam(userId, request);
        teams.put(result.getId(), result);
        return result;
    }

    public TeamDTO getTeamInfo(long teamId) {
        return teams.get(teamId);
    }
}
