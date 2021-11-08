package com.simpletodolist.todolist.repository;

import com.simpletodolist.todolist.domain.entity.MemberTeamAssociation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberTeamAssocRepository extends JpaRepository<MemberTeamAssociation, Long> {
}
