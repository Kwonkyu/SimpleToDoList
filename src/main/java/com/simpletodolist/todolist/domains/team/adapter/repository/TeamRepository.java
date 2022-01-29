package com.simpletodolist.todolist.domains.team.adapter.repository;

import com.simpletodolist.todolist.domains.user.domain.UserEntity;
import com.simpletodolist.todolist.domains.team.domain.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeamRepository extends JpaRepository<TeamEntity, Long> {

    default TeamEntity findByIdUnwrapped(Long id) {
        return findById(id)
            .orElseThrow(() -> new TeamNotFoundException(id));
    }

    List<TeamEntity> findAllByTeamNameContaining(String teamName);
    List<TeamEntity> findAllByLeader(UserEntity leader);

//    @Query("SELECT team FROM Team team JOIN MemberTeamAssociation assoc ON assoc.team=team AND assoc.member<>?2 WHERE team.teamName LIKE %?1%")
    @Query("SELECT assoc1.team FROM MemberEntity assoc1 WHERE ?2 NOT IN (" +
            "SELECT assoc.user FROM MemberEntity assoc WHERE assoc.team=assoc1.team" +
            ") AND assoc1.team.teamName LIKE %?1%")
    List<TeamEntity> findAllByTeamNameLikeAndNotJoined(String teamName, UserEntity member);

    // https://stackoverflow.com/questions/1578644/jpql-how-to-not-select-something
    @Query("SELECT assoc1.team FROM MemberEntity assoc1 WHERE ?2 NOT IN (" +
            "SELECT assoc.user FROM MemberEntity assoc WHERE assoc.team=assoc1.team" +
            ") AND assoc1.team.leader=?1")
    List<TeamEntity> findAllByLeaderAndNotJoined(UserEntity leader, UserEntity member);
}
