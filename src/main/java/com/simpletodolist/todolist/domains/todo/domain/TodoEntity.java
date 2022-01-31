package com.simpletodolist.todolist.domains.todo.domain;

import com.simpletodolist.todolist.domains.todolist.domain.TodoListEntity;
import com.simpletodolist.todolist.domains.user.domain.UserEntity;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;


/**
 * Persistence layer presentation of to-do.
 */
@Entity
@Getter
@NoArgsConstructor
@Table(name = "todo")
public class TodoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "title", nullable = false, length = 64)
	private String title;

	@Column(name = "content", nullable = false, length = 1024)
	private String content;

	@ManyToOne
	@JoinColumn(name = "member_id", referencedColumnName = "id")
	private UserEntity writer;

	@ManyToOne
	@JoinColumn(name = "todolist_id", referencedColumnName = "id")
	private TodoListEntity todoList;

	// MEMO: when to-do is locked
	// - only writer can update, delete to-do.
	// - team leader, to-do list owner can update, delete to-do.
	@Column(name = "locked", nullable = false)
	private boolean locked;

	@Builder
	public TodoEntity(
		String title,
		String content,
		UserEntity writer,
		TodoListEntity todoList,
		boolean locked
	) {
		changeTitle(title);
		changeContent(content);
		changeWriter(writer);
		changeTodoList(todoList);
		this.locked = locked;
	}

	public void changeTitle(String title) {
        if (title.isBlank()) {
            throw new IllegalArgumentException("Changed title cannot be blank.");
        }
		this.title = title;
	}

	public void changeContent(String content) {
        if (title.isBlank()) {
            throw new IllegalArgumentException("Changed content cannot be blank.");
        }
		this.content = content;
	}

	public void changeTodoList(TodoListEntity todoList) {
        if (this.todoList != null) {
            this.todoList.getTodos()
                         .remove(this);
        }
		this.todoList = todoList;
		this.todoList.getTodos()
					 .add(this);
	}

	public void changeWriter(UserEntity writer) {
		this.writer = writer;
	}

	public void lock() {
		locked = true;
	}

	public void unlock() {
		locked = false;
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
        TodoEntity that = (TodoEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
