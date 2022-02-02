package com.simpletodolist.todolist.domains.team.domain;

import com.simpletodolist.todolist.domains.todolist.domain.TodoListEntity;
import com.simpletodolist.todolist.domains.user.domain.UserEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

/**
 * Persistence layer presentation of team.
 */
@Entity
@Getter
@NoArgsConstructor
@Table(name = "team")
public class TeamEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@OneToOne
	@JoinColumn(referencedColumnName = "id", name = "leader_id")
	private UserEntity leader;

	@Column(name = "name", nullable = false, length = 64)
	private String teamName;

	@OneToMany(mappedBy = "team", cascade = CascadeType.PERSIST, orphanRemoval = true)
	private final List<MemberEntity> members = new ArrayList<>();

	@OneToMany(mappedBy = "team", cascade = CascadeType.PERSIST, orphanRemoval = true)
	private final List<TodoListEntity> todoLists = new ArrayList<>();

	// MEMO: when team is locked
	// - not-joined user can't read team's information.
	// - not-joined user can't read team's to-do lists.
	// - not-joined user can't read team's to-dos.
	@Column(name = "locked", nullable = false)
	private boolean locked = false;

	@Builder
	public TeamEntity(UserEntity leader, String teamName, boolean locked) {
		this.leader = leader;
		changeTeamName(teamName);
		this.locked = locked;
	}

	public boolean hasMember(UserEntity userEntity) {
		return members.stream()
					  .map(MemberEntity::getUser)
					  .anyMatch(userEntity::equals);
	}

	public void addMember(UserEntity user) {
		if (hasMember(user)) {
			throw new IllegalStateException("User already joined team.");
		}

		MemberEntity assoc = MemberEntity.of(user, this);
		members.add(assoc);
		user.addTeamRegistration(assoc);
	}

	public void removeMember(UserEntity user) {
		MemberEntity member = this.members
			.stream()
			.filter(memberEntity -> memberEntity.getUser()
												.equals(user))
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("Member not joined team."));
		members.remove(member);
		user.deleteTeamRegistration(member);
	}

	public List<UserEntity> getMembersReadOnly() {
		return members.stream()
					  .map(MemberEntity::getUser)
					  .collect(Collectors.toList());
	}

	public void changeTeamName(String teamName) {
		// REFACTOR: validation util.
		if (teamName.isBlank()) {
			throw new IllegalArgumentException("Changed team name cannot be blank.");
		}
		this.teamName = teamName;
	}

	public void changeLeader(UserEntity member) {
		if (!hasMember(member)) {
			throw new IllegalStateException("Leader must be member of team.");
		}
		leader = member;
	}

	public void lock() {
		locked = true;
	}

	public void unlock() {
		locked = false;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(
			o)) {
			return false;
		}
		TeamEntity that = (TeamEntity) o;
		return id != null && Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
