package com.simpletodolist.todolist.domains.team.service.port;

import org.springframework.security.access.AccessDeniedException;

public interface TeamAuthorizationService {

	void checkPublicAccess(Long teamId, String joinedUsername) throws AccessDeniedException;

	void checkLeaderAccess(Long teamId, String leaderUsername) throws AccessDeniedException;

	void checkMemberAccess(Long teamId, String memberUsername) throws AccessDeniedException;
}
