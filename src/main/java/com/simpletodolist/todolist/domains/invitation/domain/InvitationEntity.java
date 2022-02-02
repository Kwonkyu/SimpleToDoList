package com.simpletodolist.todolist.domains.invitation.domain;

import com.simpletodolist.todolist.domains.team.domain.TeamEntity;
import com.simpletodolist.todolist.domains.user.domain.UserEntity;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Persistence layer presentation of member invitation.
 */
@Getter
@Entity
@Table(name = "invitation")
@NoArgsConstructor
public class InvitationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "team_id", referencedColumnName = "id", nullable = false, updatable = false)
	private TeamEntity team;

	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, updatable = false)
	private UserEntity user;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private InvitationStatus status;

	@Column(name = "invited_at", nullable = false)
	private LocalDateTime invitedAt;

	@Builder
	public InvitationEntity(
		TeamEntity team,
		UserEntity user
	) {
		this.team = team;
		this.user = user;
		this.status = InvitationStatus.REQUESTED;
		this.invitedAt = LocalDateTime.now();
	}

	public void accept() {
		// TODO: status validation later.
		this.status = InvitationStatus.ACCEPTED;
	}

	public void refuse() {
		this.status = InvitationStatus.REFUSED;
	}

	public void cancel() {
		this.status = InvitationStatus.CANCELLED;
	}
}
