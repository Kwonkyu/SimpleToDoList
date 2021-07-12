package com.simpletodolist.todolist.repository;

import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.domain.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findAllByTeamNameContaining(String teamName);
    List<Team> findAllByLeader(Member leader);

//    @Query("SELECT team FROM Team team JOIN MemberTeamAssociation assoc ON assoc.team=team AND assoc.member<>?2 WHERE team.teamName LIKE %?1%")
    @Query("SELECT assoc1.team FROM MemberTeamAssociation assoc1 WHERE ?2 NOT IN (" +
            "SELECT assoc.member FROM MemberTeamAssociation assoc WHERE assoc.team=assoc1.team" +
            ") AND assoc1.team.teamName LIKE %?1%")
    List<Team> findAllByTeamNameLikeAndNotJoined(String teamName, Member member);

    // https://stackoverflow.com/questions/1578644/jpql-how-to-not-select-something
    @Query("SELECT assoc1.team FROM MemberTeamAssociation assoc1 WHERE ?2 NOT IN (" +
            "SELECT assoc.member FROM MemberTeamAssociation assoc WHERE assoc.team=assoc1.team" +
            ") AND assoc1.team.leader=?1")
    List<Team> findAllByLeaderAndNotJoined(Member leader, Member member);
}
