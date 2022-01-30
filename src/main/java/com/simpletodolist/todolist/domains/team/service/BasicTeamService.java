package com.simpletodolist.todolist.domains.team.service;

import com.simpletodolist.todolist.domains.team.adapter.controller.command.TeamCreateRequest;
import com.simpletodolist.todolist.domains.team.adapter.controller.command.TeamSearchRequest;
import com.simpletodolist.todolist.domains.team.adapter.controller.command.TeamUpdateRequest;
import com.simpletodolist.todolist.domains.team.adapter.repository.TeamRepository;
import com.simpletodolist.todolist.domains.team.domain.Members;
import com.simpletodolist.todolist.domains.team.domain.SearchedTeams;
import com.simpletodolist.todolist.domains.team.domain.Team;
import com.simpletodolist.todolist.domains.team.domain.TeamEntity;
import com.simpletodolist.todolist.domains.team.service.port.MemberService;
import com.simpletodolist.todolist.domains.team.service.port.TeamAuthorizationService;
import com.simpletodolist.todolist.domains.team.service.port.TeamCrudService;
import com.simpletodolist.todolist.domains.team.service.port.TeamLeaderService;
import com.simpletodolist.todolist.domains.user.adapter.repository.UserRepository;
import com.simpletodolist.todolist.domains.user.domain.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicTeamService implements
	MemberService,
	TeamAuthorizationService,
	TeamCrudService,
	TeamLeaderService {

	private final UserRepository userRepository;
	private final TeamRepository teamRepository;

	@Override
	public Members getJoinedMembers(
		Long teamId
	) {
		return new Members(teamRepository.findByIdUnwrapped(teamId)
										 .getMembersReadOnly());
	}

	@Override
	public Members inviteMember(
		Long teamId, String invitedUsername
	) {
		TeamEntity team = teamRepository.findByIdUnwrapped(teamId);
		UserEntity newMember = userRepository.findByUsernameUnwrapped(invitedUsername);
		team.addMember(newMember);
		return new Members(team.getMembersReadOnly());
	}

	@Override
	public Members withdrawMember(
		Long teamId, String withdrawUsername
	) {
		TeamEntity team = teamRepository.findByIdUnwrapped(teamId);
		UserEntity withdrawMember = userRepository.findByUsernameUnwrapped(withdrawUsername);
		team.removeMember(withdrawMember);
		return new Members(team.getMembersReadOnly());
	}

	@Override
	public void checkPublicAccess(Long teamId, String joinedUsername) throws AccessDeniedException {
		TeamEntity team = teamRepository.findByIdUnwrapped(teamId);
		UserEntity member = userRepository.findByUsernameUnwrapped(joinedUsername);
		if (team.isLocked() && !team.getMembersReadOnly()
									.contains(member)) {
			throw new AccessDeniedException(String.format(
				"Team %s is not public. Contact leader %s.",
				team.getTeamName(), member.getUsername()
			));
		}
	}

	@Override
	public void checkLeaderAccess(Long teamId, String leaderUsername) throws AccessDeniedException {
		TeamEntity team = teamRepository.findByIdUnwrapped(teamId);
		UserEntity member = userRepository.findByUsernameUnwrapped(leaderUsername);
		if (!team.getLeader()
				 .equals(member)) {
			throw new AccessDeniedException(String.format(
				"Member %s is not leader of team %s",
				member.getUsername(), team.getTeamName()
			));
		}
	}

	@Override
	public void checkMemberAccess(Long teamId, String memberUsername) throws AccessDeniedException {
		TeamEntity team = teamRepository.findByIdUnwrapped(teamId);
		UserEntity member = userRepository.findByUsernameUnwrapped(memberUsername);
		if (!team.getMembersReadOnly()
				 .contains(member)) {
			throw new AccessDeniedException(String.format(
				"Member %s is not authorized to team %s",
				member.getUsername(), team.getTeamName()
			));
		}
	}

	@Override
	public Team getTeamInformation(Long teamId) {
		return new Team(teamRepository.findByIdUnwrapped(teamId));
	}

	@Override
	public SearchedTeams searchTeams(
		TeamSearchRequest request, String username
	) {
		Page<TeamEntity> pageResult;
		PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize());
		// REFACTOR: dynamic query.
		if (request.isIncludeJoined()) {
			switch (request.getSearchField()) {
				case LEADER:
					UserEntity leader = userRepository.findByUsernameUnwrapped(
						request.getSearchValue());
					pageResult = teamRepository.findAllByLeader(leader, pageRequest);
					break;

				case NAME:
					pageResult = teamRepository.findAllByTeamNameContaining(
						request.getSearchValue(), pageRequest);
					break;

				default:
					pageResult = Page.empty();
			}
		} else {
			UserEntity currentUser = userRepository.findByUsernameUnwrapped(username);
			switch (request.getSearchField()) {
				case LEADER:
					UserEntity leader = userRepository.findByUsernameUnwrapped(
						request.getSearchValue());
					pageResult = teamRepository.findAllByLeaderAndNotJoined(
						leader, currentUser, pageRequest);
					break;

				case NAME:
					pageResult = teamRepository.findAllByTeamNameLikeAndNotJoined(
						request.getSearchValue(), currentUser, pageRequest);
					break;

				default:
					pageResult = Page.empty();
			}
		}

		return new SearchedTeams(pageResult);
	}

	@Override
	public Team createTeam(
		TeamCreateRequest request, String leaderUsername
	) {
		UserEntity leader = userRepository.findByUsernameUnwrapped(leaderUsername);
		TeamEntity createdTeam = teamRepository.save(
			TeamEntity.builder()
					  .teamName(request.getTeamName())
					  .locked(false)
					  .leader(leader)
					  .build());
		return new Team(createdTeam);
	}

	@Override
	public Team updateTeam(
		Long teamId, TeamUpdateRequest request
	) {
		TeamEntity team = teamRepository.findByIdUnwrapped(teamId);
		request.setTeamName(request.getTeamName());
		request.setLocked(request.isLocked());
		return new Team(team);
	}

	@Override
	public void deleteTeam(Long teamId) {
		TeamEntity team = teamRepository.findByIdUnwrapped(teamId);
		team.getTodoLists()
			.clear();
		team.getMembers()
			.clear();
	}

	@Override
	public Team changeLeader(Long teamId, String newLeaderUsername) {
		TeamEntity team = teamRepository.findByIdUnwrapped(teamId);
		UserEntity newLeader = userRepository.findByUsernameUnwrapped(newLeaderUsername);
		team.changeLeader(newLeader);
		return new Team(team);
	}
}
