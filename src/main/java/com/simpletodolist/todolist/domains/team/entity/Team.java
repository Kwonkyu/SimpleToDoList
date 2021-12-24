package com.simpletodolist.todolist.domains.team.entity;

import com.simpletodolist.todolist.domains.todolist.entity.TodoList;
import com.simpletodolist.todolist.domains.member.entity.Member;
import com.simpletodolist.todolist.domains.member.exception.DuplicatedTeamJoinException;
import com.simpletodolist.todolist.domains.member.exception.NotJoinedTeamException;
import lombok.*;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "team")
public class Team {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @OneToOne
    @JoinColumn(name = "leader_id")
    private Member leader;

    @Column(name = "name", nullable = false, length = 64)
    private String teamName;

    @OneToMany(mappedBy = "team", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private final List<MemberTeamAssociation> members = new ArrayList<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private final List<TodoList> todoLists = new ArrayList<>();

    @Column(name = "locked")
    private boolean locked = false;


    @Builder
    public Team(Member leader, String teamName, boolean locked) {
        this.leader = leader;
        changeTeamName(teamName);
        this.locked = locked;
    }

    public boolean isMemberIncluded(Member member) {
        return members.stream()
                .anyMatch(teamAssociation -> teamAssociation.getMember().equals(member));
    }

    public void addMember(Member member) {
        if(isMemberIncluded(member)) {
            throw new DuplicatedTeamJoinException(member, this);
        }

        MemberTeamAssociation assoc = MemberTeamAssociation.builder()
                .team(this)
                .member(member).build();
        this.members.add(assoc);
        member.getTeams().add(assoc);
    }

    public void removeMember(Member member) {
        MemberTeamAssociation association = members.stream()
                .filter(assoc -> assoc.getMember().equals(member))
                .findAny().orElseThrow(() -> {
                    throw new NotJoinedTeamException(member, this);
                });
        this.members.remove(association);
        member.getTeams().remove(association);
    }

    public List<Member> getMembersReadOnly() {
        return members.stream().map(MemberTeamAssociation::getMember).collect(Collectors.toList());
    }

    public void changeTeamName(@NonNull String teamName) {
        if(teamName.isBlank()) throw new IllegalArgumentException("Changed team name cannot be blank.");
        this.teamName = teamName;
    }

    public void changeLeader(@NonNull Member member) {
        if (!isMemberIncluded(member)) {
            throw new NotJoinedTeamException(member, this);
        }

        leader = member;
    }

    public void toggleLock() { locked = !locked; }

    public void lock() { locked = true; }

    public void unlock() { locked = false; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return id == team.id && locked == team.locked && leader.equals(team.leader) && teamName.equals(team.teamName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, leader, teamName, locked);
    }
}
