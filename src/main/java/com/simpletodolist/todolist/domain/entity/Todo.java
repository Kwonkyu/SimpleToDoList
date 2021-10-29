package com.simpletodolist.todolist.domain.entity;

import lombok.*;
import org.springframework.lang.NonNull;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Todo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "title", nullable = false, length = 64)
    private String title;

    @Column(name = "content", nullable = false, length = 1024)
    private String content;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member writer;

    @ManyToOne
    @JoinColumn(name = "todolist_id")
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
        this.todoList = todoList;
    }

    public void changeWriter(@NonNull Member writer) { this.writer = writer; }

    public void toggleLock() { locked = !locked; }

    public void lock() { locked = true; }

    public void unlock() { locked = false; }
}
