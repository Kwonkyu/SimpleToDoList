package com.simpletodolist.todolist.domain.entity;

import lombok.*;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
public class Todo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "title", nullable = false, length = 64)
    private String title;

    @Column(name = "content", nullable = false, length = 1024)
    private String content;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member writer;

    @ManyToOne
    @JoinColumn(name = "todolist_id", referencedColumnName = "id")
    private TodoList todoList;

    @Column(name = "locked")
    private boolean locked;


    @Builder
    public Todo(String title, String content, Member writer, TodoList todoList, boolean locked) {
        changeTitle(title);
        changeContent(content);
        changeWriter(writer);
        changeTodoList(todoList);
        this.locked = locked;
    }

    public void changeTitle(@NonNull String title){
        if(title.isBlank()) throw new IllegalArgumentException("Changed title cannot be blank.");
        this.title = title;
    }

    public void changeContent(@NonNull String content){
        if(title.isBlank()) throw new IllegalArgumentException("Changed content cannot be blank.");
        this.content = content;
    }

    public void changeTodoList(@NonNull TodoList todoList) {
        if(this.todoList != null) this.todoList.getTodos().remove(this);
        this.todoList = todoList;
        this.todoList.getTodos().add(this);
    }

    public void changeWriter(@NonNull Member writer) { this.writer = writer; }

    public void lock() { locked = true; }

    public void unlock() { locked = false; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Todo todo = (Todo) o;
        return id == todo.id && locked == todo.locked && title.equals(todo.title) && content.equals(todo.content) && writer.equals(todo.writer) && todoList.equals(todo.todoList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, content, writer, todoList, locked);
    }
}
