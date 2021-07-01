package com.simpletodolist.todolist.repository;

import com.simpletodolist.todolist.domain.entity.Member;
import com.simpletodolist.todolist.domain.entity.MemberTeamAssociation;
import com.simpletodolist.todolist.domain.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberTeamAssocRepository extends JpaRepository<MemberTeamAssociation, Long> {

    Optional<MemberTeamAssociation> findByTeamAndMember(Team team, Member member);
    Optional<MemberTeamAssociation> findByMember(Member member);
    Optional<MemberTeamAssociation> findByTeam(Team team);

    boolean existsByTeamAndMember(Team team, Member member);
    boolean existsByMember(Member member);
    boolean existsByTeam(Team team);

    void deleteByTeamAndMember(Team team, Member member);
}
