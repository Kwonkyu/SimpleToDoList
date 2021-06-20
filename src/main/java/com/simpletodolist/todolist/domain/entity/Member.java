package com.simpletodolist.todolist.domain.entity;

import com.simpletodolist.todolist.domain.dto.MemberDTO;
import com.simpletodolist.todolist.domain.dto.TeamDTO;
import com.simpletodolist.todolist.domain.dto.TeamsDTO;
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
public class Member {

    public static final String NO_MEMBER_FOUND = "No Member Found.";
    public static final String NOT_JOINED_TEAM = "Not Joined Team.";
    public static final String DUPLICATED_TEAM_JOINED = "Already Joined Team.";
    public static final String DUPLICATED_MEMBER_FOUND = "Already Existing Member.";


    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private long id;

    @NonNull
    @Column(name = "USERID", nullable = false, length = 32)
    private String userId;

    @NonNull
    @Column(name = "USERNAME", nullable = false, length = 32)
    private String username;

    @NonNull
    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @OneToMany(mappedBy = "member")
    private List<MemberTeamAssociation> teams = new ArrayList<>();

    @OneToMany(mappedBy = "owner")
    private List<TodoList> todoLists = new ArrayList<>();


    public TeamsDTO getTeamsAsDTO(){
        return new TeamsDTO(teams.stream().map(MemberTeamAssociation::getTeam).map(TeamDTO::new).collect(Collectors.toList()));
    }

    public void changeUserId(String userId){
        this.userId = userId;
    }

    public void changeUsername(String username){
        this.username = username;
    }

    public void changePassword(String password){
        this.password = password;
    }

    public boolean isJoinedTeam(Team team) {
        return teams.stream().anyMatch(t -> t.getTeam().equals(team));
    }


    public Member(MemberDTO memberDTO){
        this.userId = memberDTO.getUserId();
        this.username = memberDTO.getUsername();
        this.password = memberDTO.getPassword();
    }

}
