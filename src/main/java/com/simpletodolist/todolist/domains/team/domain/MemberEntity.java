package com.simpletodolist.todolist.domains.team.domain;

import com.simpletodolist.todolist.domains.user.domain.UserEntity;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

/**
 * Persistence layer presentation of user registration on team.
 */
@Entity
@Getter
@NoArgsConstructor
@Table(name = "member")
public class MemberEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "user_id")
	private UserEntity user;

	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "team_id")
	private TeamEntity team;

	/**
	 * Factory method to instantiate MemberEntity.
	 *
	 * @param user user of team.
	 * @param team team.
	 * @return the member entity
	 */
	public static MemberEntity of(UserEntity user, TeamEntity team) {
		MemberEntity memberEntity = new MemberEntity();
		memberEntity.user = user;
		memberEntity.team = team;
		return memberEntity;
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
		MemberEntity memberEntity = (MemberEntity) o;
		return id != null && Objects.equals(id, memberEntity.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
