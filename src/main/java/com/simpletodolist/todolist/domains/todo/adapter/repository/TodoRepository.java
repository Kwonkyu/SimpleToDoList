package com.simpletodolist.todolist.domains.todo.adapter.repository;

import com.simpletodolist.todolist.domains.todo.domain.TodoEntity;
import com.simpletodolist.todolist.domains.todolist.domain.TodoListEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<TodoEntity, Long> {

	Optional<TodoEntity> findByIdAndTodoList(Long id, TodoListEntity todoListEntity);

	default TodoEntity findTodoById(Long id) {
		return findById(id).orElseThrow(() -> new NoTodoFoundException(id));
	}

	default TodoEntity findTodoByIdAndTodoList(Long id, TodoListEntity todoListEntity) {
		return findByIdAndTodoList(id, todoListEntity).orElseThrow(
			() -> new NoTodoFoundException(id));
	}

	Page<TodoEntity> findAllByTodoList(TodoListEntity todoList, Pageable pageable);

	boolean existsByIdAndTodoList(long id, TodoListEntity todoList);
}
