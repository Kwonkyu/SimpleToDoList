package com.simpletodolist.todolist.domains.team.repository;

import com.simpletodolist.todolist.domains.team.entity.MemberTeamAssociation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberTeamAssocRepository extends JpaRepository<MemberTeamAssociation, Long> {
}
