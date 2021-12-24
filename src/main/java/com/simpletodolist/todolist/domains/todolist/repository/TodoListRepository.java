package com.simpletodolist.todolist.domains.todolist.repository;

import com.simpletodolist.todolist.domains.member.entity.Member;
import com.simpletodolist.todolist.domains.team.entity.Team;
import com.simpletodolist.todolist.domains.todolist.entity.TodoList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TodoListRepository extends JpaRepository<TodoList, Long> {

    List<TodoList> findAllByOwner(Member owner);
    List<TodoList> findAllByTeam(Team team);
    List<TodoList> findAllByOwnerAndTeam(Member owner, Team team);

    Optional<TodoList> findByIdAndTeam(long id, Team team);
    Optional<TodoList> findByIdAndOwnerAndTeam(long id, Member owner, Team team);

    boolean existsByIdAndTeam(long id, Team team);
    boolean existsByIdAndTeamAndOwner(long id, Team team, Member owner);

}
