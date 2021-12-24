package com.simpletodolist.todolist.domains.todolist.entity;

import com.simpletodolist.todolist.domains.member.entity.Member;
import com.simpletodolist.todolist.domains.team.entity.Team;
import com.simpletodolist.todolist.domains.todo.entity.Todo;
import lombok.*;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "todo_list")
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

    @OneToMany(mappedBy = "todoList", cascade = CascadeType.PERSIST, orphanRemoval = true)
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

    public void changeTeam(@NonNull Team team) {
        if(this.team != null) team.getTodoLists().remove(this);
        this.team = team;
        team.getTodoLists().add(this);
    }

    public void changeOwner(@NonNull Member member) {
        owner = member;
    }

    public void unlock() {
        locked = false;
    }

    public void lock() {
        locked = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodoList todoList = (TodoList) o;
        return id == todoList.id &&
                locked == todoList.locked &&
                name.equals(todoList.name) &&
                team.equals(todoList.team) &&
                owner.equals(todoList.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, team, owner, locked);
    }
}
