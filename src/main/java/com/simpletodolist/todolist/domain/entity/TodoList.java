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
    @Column(name = "NAME", nullable = false, length = 64)
    private String name;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member owner;

    @Column(name = "LOCKED")
    private boolean locked = false;

    @OneToMany(mappedBy = "todoList")
    private final List<Todo> todos = new ArrayList<>();


    public void changeName(String name){
        this.name = name;
    }

    public void changeOwner(Member member) {
        owner = member;
    }

    public void toggleLock(){
        locked = !locked;
    }

    public void unlock() {
        locked = false;
    }

    public void lock() {
        locked = true;
    }
}
