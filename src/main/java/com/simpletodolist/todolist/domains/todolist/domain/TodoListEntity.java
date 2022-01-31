package com.simpletodolist.todolist.domains.todolist.domain;

import com.simpletodolist.todolist.domains.team.domain.TeamEntity;
import com.simpletodolist.todolist.domains.todo.domain.TodoEntity;
import com.simpletodolist.todolist.domains.user.domain.UserEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.lang.NonNull;

/**
 * Persistence layer presentation of to-do list.
 */
@Entity
@Getter
@NoArgsConstructor
@Table(name = "todo_list")
public class TodoListEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "name", nullable = false, length = 64)
	private String name;

	@ManyToOne
	@JoinColumn(name = "team_id", referencedColumnName = "id")
	private TeamEntity team;

	@ManyToOne
	@JoinColumn(name = "member_id", referencedColumnName = "id")
	private UserEntity owner;

	@Column(name = "locked")
	private boolean locked = false;

	@OneToMany(mappedBy = "todoList", cascade = CascadeType.PERSIST, orphanRemoval = true)
	private final List<TodoEntity> todos = new ArrayList<>();

	@Builder
	public TodoListEntity(String name, TeamEntity team, UserEntity owner, boolean locked) {
		this.name = name;
		this.locked = locked;
	}

	public void changeName(String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("Changed name cannot be blank.");
        }
		this.name = name;
	}

	public void changeLocked(boolean locked) {
		this.locked = locked;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(
            o)) {
            return false;
        }
        TodoListEntity that = (TodoListEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
