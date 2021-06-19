package com.simpletodolist.todolist.domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Todo {

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
    @JoinColumn(name = "TODOLIST_ID")
    private TodoList todoList;


    public void changeTitle(String title){
        this.title = title;
    }

    public void changeContent(String content){
        this.content = content;
    }

}
