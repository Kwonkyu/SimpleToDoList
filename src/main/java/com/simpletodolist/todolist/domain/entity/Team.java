package com.simpletodolist.todolist.domain.entity;

import lombok.*;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Team {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @OneToOne
    @JoinColumn(name = "leader_id")
    private Member leader;

    @Column(name = "name", nullable = false, length = 64)
    private String teamName;

    @OneToMany(mappedBy = "team")
    private final List<MemberTeamAssociation> members = new ArrayList<>();

    @OneToMany(mappedBy = "team")
    private final List<TodoList> todoLists = new ArrayList<>();

    @Column(name = "locked")
    private boolean locked = false;


    @Builder
    public Team(Member leader, String teamName, boolean locked) {
        this.leader = leader;
        changeTeamName(teamName);
        this.locked = locked;
    }

    public List<Member> getMembers() {
        return members.stream().map(MemberTeamAssociation::getMember).collect(Collectors.toList());
    }

    public void changeTeamName(@NonNull String teamName) {
        if(teamName.isBlank()) throw new IllegalArgumentException("Changed team name cannot be blank.");
        this.teamName = teamName;
    }

    public void changeLeader(@NonNull Member member) {
        leader = member;
    }

    public void toggleLock() { locked = !locked; }

    public void lock() { locked = true; }

    public void unlock() { locked = false; }
}
