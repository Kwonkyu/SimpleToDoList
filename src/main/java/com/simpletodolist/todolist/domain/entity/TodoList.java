package com.simpletodolist.todolist.domain.entity;

import lombok.*;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class TodoList {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member owner;

    @Column(name = "locked")
    private boolean locked = false;

    @OneToMany(mappedBy = "todoList")
    private final List<Todo> todos = new ArrayList<>();


    @Builder
    public TodoList(String name, Team team, Member owner, boolean locked) {
        changeName(name);
        changeTeam(team);
        changeOwner(owner);
        this.locked = locked;
    }

    public void changeName(@NonNull String name){
        if(name.isBlank()) throw new IllegalArgumentException("Changed name cannot be blank.");
        this.name = name;
    }

    public void changeTeam(@NonNull Team team) { this.team = team; }

    public void changeOwner(@NonNull Member member) {
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
