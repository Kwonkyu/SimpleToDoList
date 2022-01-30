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
public class BasicUserService implements UserCrudService, JoinedTeamManageService {

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final TeamRepository teamRepository;

	@Transactional(readOnly = true)
	@Override
	public User getMemberDetails(String username) {
		return new User(userRepository.findByUsernameUnwrapped(username));
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
		UserEntity member = userRepository.findByUsernameUnwrapped(username);
		member.changeAlias(request.getAlias());
		member.changePassword(passwordEncoder.encode(request.getPassword()));
		return new User(member);
	}

	@Override
	public void withdrawMember(String username) {
		UserEntity member = userRepository.findByUsernameUnwrapped(username);
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
		TeamEntity team = teamRepository.findByIdUnwrapped(teamId);
		UserEntity member = userRepository.findByUsernameUnwrapped(username);
		if (team.isLocked()) {
			throw new AccessDeniedException("Unable to join team.");
		}

		team.addMember(member);
		return new JoinedTeam(team);
	}

	@Override
	public void withdrawTeam(Long teamId, String username) {
		TeamEntity team = teamRepository.findByIdUnwrapped(teamId);
		UserEntity member = userRepository.findByUsernameUnwrapped(username);
		team.removeMember(member);
	}

	@Override
	public JoinedTeams getJoinedTeams(String username) {
		return new JoinedTeams(userRepository
								   .findByUsernameUnwrapped(username)
								   .getTeamsReadOnly());
	}
}
