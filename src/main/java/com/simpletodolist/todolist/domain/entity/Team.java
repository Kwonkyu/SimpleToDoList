package com.simpletodolist.todolist.domain.entity;

import com.simpletodolist.todolist.domain.dto.MemberDTO;
import com.simpletodolist.todolist.domain.dto.MembersDTO;
import com.simpletodolist.todolist.exception.member.NoMemberFoundException;
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
    private List<MemberTeamAssociation> members = new ArrayList<>();


    public MembersDTO getMembersAsDTO(){
        return new MembersDTO(members.stream().map(MemberTeamAssociation::getMember).map(MemberDTO::new).collect(Collectors.toList()));
    }

    public void changeTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void changeLeader(Member member) {
        // leader should be one of member.
        members.stream()
                .filter(memberTeamAssociation -> memberTeamAssociation.getMember().equals(member)).findAny()
                .orElseThrow(NoMemberFoundException::new);

        leader = member;
    }

//    public void joinMember(Member member) {
//        MemberTeamAssociation joinAssociation = new MemberTeamAssociation(member, this);
//        joinMember(joinAssociation);
//        member.joinTeam(joinAssociation);
//    }
//
//    public void joinMember(MemberTeamAssociation member) {
//        this.members.add(member);
//    }
//
//    public void quitMember(Member member) {
//        MemberTeamAssociation quitAssociation = new MemberTeamAssociation(member, this);
//        quitMember(quitAssociation);
//        member.quitTeam(quitAssociation);
//    }
//
//    public void quitMember(MemberTeamAssociation member) {
//        this.members.remove(member);
//    }

}
