package com.simpletodolist.todolist.domains.team.adapter.repository;

import com.simpletodolist.todolist.domains.team.domain.TeamEntity;
import com.simpletodolist.todolist.domains.user.domain.UserEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TeamRepository extends JpaRepository<TeamEntity, Long> {

	default TeamEntity findByIdUnwrapped(Long id) {
		return findById(id)
			.orElseThrow(() -> new TeamNotFoundException(id));
	}

	Page<TeamEntity> findAllByTeamNameContaining(String teamName, Pageable pageable);

	Page<TeamEntity> findAllByLeader(UserEntity leader, Pageable pageable);

	@Query(value = "SELECT assoc1.team FROM MemberEntity assoc1 WHERE ?2 NOT IN (" +
		"SELECT assoc.user FROM MemberEntity assoc WHERE assoc.team=assoc1.team" +
		") AND assoc1.team.teamName LIKE %?1% ORDER BY assoc1.team.id DESC",
        countQuery = "SELECT count(assoc1.team) FROM MemberEntity assoc1 WHERE ?2 NOT IN (" +
            "SELECT assoc.user FROM MemberEntity assoc WHERE assoc.team=assoc1.team" +
            ") AND assoc1.team.teamName LIKE %?1% ORDER BY assoc1.team.id DESC"
    )
	Page<TeamEntity> findAllByTeamNameLikeAndNotJoined(
		String teamName,
		UserEntity member,
		Pageable pageable
	);

	// https://stackoverflow.com/questions/1578644/jpql-how-to-not-select-something
	@Query(value = "SELECT assoc1.team FROM MemberEntity assoc1 WHERE ?2 NOT IN (" +
		"SELECT assoc.user FROM MemberEntity assoc WHERE assoc.team=assoc1.team" +
		") AND assoc1.team.leader=?1 ORDER BY assoc1.team.id DESC",
        countQuery = "SELECT count(assoc1.team) FROM MemberEntity assoc1 WHERE ?2 NOT IN (" +
            "SELECT assoc.user FROM MemberEntity assoc WHERE assoc.team=assoc1.team" +
            ") AND assoc1.team.leader=?1 ORDER BY assoc1.team.id DESC"
    )
	Page<TeamEntity> findAllByLeaderAndNotJoined(
		UserEntity leader,
		UserEntity member,
		Pageable pageable
	);
}
