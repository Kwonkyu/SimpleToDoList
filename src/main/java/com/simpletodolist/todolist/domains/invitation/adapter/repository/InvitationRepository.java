package com.simpletodolist.todolist.domains.invitation.adapter.repository;

import com.simpletodolist.todolist.domains.invitation.domain.InvitationEntity;
import com.simpletodolist.todolist.domains.invitation.domain.InvitationStatus;
import com.simpletodolist.todolist.domains.team.domain.TeamEntity;
import com.simpletodolist.todolist.domains.user.domain.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitationRepository extends PagingAndSortingRepository<InvitationEntity, Long> {

	default InvitationEntity findInvitationById(Long id) {
		return findById(id).orElseThrow(() -> new NoInvitationFoundException(id));
	}

	Page<InvitationEntity> findAllByTeamAndStatusAndIdGreaterThanEqual(
		TeamEntity teamEntity,
		InvitationStatus status,
		Long cursorInvitationId,
		Pageable pageable
	);

	Page<InvitationEntity> findAllByUserAndStatusAndIdGreaterThanEqual(
		UserEntity userEntity,
		InvitationStatus status,
		Long cursorInvitationId,
		Pageable pageable
	);

	Page<InvitationEntity> findAllByTeamAndIdGreaterThanEqual(
		TeamEntity teamEntity,
		Long cursorInvitationId,
		Pageable pageable
	);

	Page<InvitationEntity> findAllByUserAndIdGreaterThanEqual(
		UserEntity userEntity,
		Long cursorInvitationId,
		Pageable pageable
	);
}
