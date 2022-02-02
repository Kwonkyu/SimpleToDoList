package com.simpletodolist.todolist.domains.team.adapter.repository;

import com.simpletodolist.todolist.domains.team.domain.MemberEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends CrudRepository<MemberEntity, Long> {

}
