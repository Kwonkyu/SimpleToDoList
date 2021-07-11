package com.simpletodolist.todolist.domain.entity;

import com.simpletodolist.todolist.controller.bind.MemberDTO;
import com.simpletodolist.todolist.controller.bind.MembersDTO;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Team {

    public static final String NO_TEAM_FOUND = "No Team Found.";
    public static final String DUPLICATED_TEAM_FOUND = "Already Existing Team.";
    public static final String DUPLICATED_MEMBER_JOINED = "Already Joined Member.";


    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TEAM_ID")
    private long id;

    @NonNull
    @OneToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member leader;

    @NonNull
    @Column(name = "NAME", nullable = false, length = 64)
    private String teamName;

    @OneToMany(mappedBy = "team")
    private final List<MemberTeamAssociation> members = new ArrayList<>();

    @OneToMany(mappedBy = "team")
    private final List<TodoList> todoLists = new ArrayList<>();

    @Column(name = "LOCKED")
    private boolean locked = false;


    public MembersDTO getMembersDTO(){
        return new MembersDTO(members.stream().map(MemberTeamAssociation::getMember).map(MemberDTO::new).collect(Collectors.toList()));
    }

    public void changeTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void changeLeader(Member member) {
        leader = member;
    }

    public void toggleLock() { locked = !locked; }

    public void lock() { locked = true; }

    public void unlock() { locked = false; }
}
