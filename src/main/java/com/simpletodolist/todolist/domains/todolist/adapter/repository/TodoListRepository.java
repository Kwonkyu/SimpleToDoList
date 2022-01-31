package com.simpletodolist.todolist.domains.todolist.adapter.repository;

import com.simpletodolist.todolist.domains.team.domain.TeamEntity;
import com.simpletodolist.todolist.domains.todolist.adapter.repository.NoTodoListFoundException;
import com.simpletodolist.todolist.domains.todolist.domain.TodoListEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoListRepository extends JpaRepository<TodoListEntity, Long> {

    Optional<TodoListEntity> findByIdAndTeam(Long id, TeamEntity teamEntity);

    default TodoListEntity findTodoListByIdAndTeam(Long id, TeamEntity teamEntity) {
        return findByIdAndTeam(id, teamEntity).orElseThrow(() -> new NoTodoListFoundException(id));
    }

}
