package com.simpletodolist.todolist.domains.user.service;

import com.simpletodolist.todolist.domains.user.adapter.controller.command.UserRegisterRequest;
import com.simpletodolist.todolist.domains.team.domain.MemberEntity;
import com.simpletodolist.todolist.domains.user.adapter.controller.command.UserUpdateRequest;
import com.simpletodolist.todolist.domains.user.adapter.repository.UserRepository;
import com.simpletodolist.todolist.domains.user.domain.JoinedTeam;
import com.simpletodolist.todolist.domains.user.domain.JoinedTeams;
import com.simpletodolist.todolist.domains.user.domain.User;
import com.simpletodolist.todolist.domains.user.domain.UserEntity;
import com.simpletodolist.todolist.domains.user.service.port.JoinedTeamManageService;
import com.simpletodolist.todolist.domains.user.service.port.UserCrudService;
import com.simpletodolist.todolist.domains.team.domain.TeamEntity;
import com.simpletodolist.todolist.domains.team.adapter.repository.TeamRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserCrudService, JoinedTeamManageService {

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final TeamRepository teamRepository;

	@Override
	@Transactional(readOnly = true)
	public User getMemberDetails(String username) {
		return new User(userRepository.findUserByUsername(username));
	}

	@Override
	public User registerMember(UserRegisterRequest request) {
		if (userRepository.existsByUsername(request.getUsername())) {
			throw new IllegalArgumentException(
				String.format("Username %s already exists.", request.getUsername()));
		}

		String encryptedPassword = passwordEncoder.encode(request.getPassword());
		UserEntity member = userRepository.save(
			UserEntity.builder()
					  .username(request.getUsername())
					  .alias(request.getAlias())
					  .password(encryptedPassword)
					  .locked(false)
					  .build());
		return new User(member);
	}

	@Override
	public User updateMember(String username, UserUpdateRequest request) {
		UserEntity member = userRepository.findUserByUsername(username);
		member.changeAlias(request.getAlias());
		member.changePassword(passwordEncoder.encode(request.getPassword()));
		return new User(member);
	}

	@Override
	public void withdrawMember(String username) {
		UserEntity member = userRepository.findUserByUsername(username);
		// TODO: query check.
		List<TeamEntity> joinedTeams = member.getTeams()
											 .stream()
											 .map(MemberEntity::getTeam)
											 .collect(Collectors.toList());
		joinedTeams.forEach(team -> team.removeMember(member));
		userRepository.delete(member);
	}

	@Override
	public JoinedTeam joinTeam(Long teamId, String username) {
		TeamEntity team = teamRepository.findTeamById(teamId);
		UserEntity member = userRepository.findUserByUsername(username);
		if (team.isLocked()) {
			throw new AccessDeniedException("Unable to join team.");
		}

		team.addMember(member);
		return new JoinedTeam(team);
	}

	@Override
	public void withdrawTeam(Long teamId, String username) {
		TeamEntity team = teamRepository.findTeamById(teamId);
		UserEntity member = userRepository.findUserByUsername(username);
		team.removeMember(member);
	}

	@Override
	@Transactional(readOnly = true)
	public JoinedTeams getJoinedTeams(String username) {
		return new JoinedTeams(userRepository
								   .findUserByUsername(username)
								   .getTeamsReadOnly());
	}
}
