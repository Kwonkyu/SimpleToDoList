package com.simpletodolist.todolist.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class TodoList {

    public static final String NO_TODOLIST_FOUND = "No TodoList Found";


    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TODOLIST_ID")
    private long id;

    @NonNull
    @Column(name = "NAME")
    private String todoListName;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member owner;

    @OneToMany(mappedBy = "todoList")
    private List<Todo> todos = new ArrayList<>();
}
