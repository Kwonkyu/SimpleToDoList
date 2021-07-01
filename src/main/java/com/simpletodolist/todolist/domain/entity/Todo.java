package com.simpletodolist.todolist.domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Todo {

    public static final String NO_TODO_FOUND = "No Todo Found.";


    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TODO_ID")
    private long id;

    @NonNull
    @Column(name = "TITLE", nullable = false, length = 64)
    private String title;

    @NonNull
    @Column(name = "CONTENT")
    private String content;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member writer;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "TODOLIST_ID")
    private TodoList todoList;

    @Column(name = "LOCKED")
    private boolean locked;


    public void changeTitle(String title){ this.title = title; }
    public void changeContent(String content){
        this.content = content;
    }
    public void changeWriter(Member writer) { this.writer = writer; }

    public void toggleLock() { locked = !locked; }
    public void lock() { locked = true; }
    public void unlock() { locked = false; }
}
