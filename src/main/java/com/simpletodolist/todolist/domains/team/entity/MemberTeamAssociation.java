package com.simpletodolist.todolist.domains.team.entity;

import com.simpletodolist.todolist.domains.member.entity.Member;
import lombok.*;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "member_team_association")
public class MemberTeamAssociation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;


    @Builder
    public MemberTeamAssociation(@NonNull Member member,
                                 @NonNull Team team) {
        this.member = member;
        this.team = team;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MemberTeamAssociation that = (MemberTeamAssociation) o;
        return Objects.equals(id, that.id) && member.equals(that.member) && team.equals(
            that.team);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, member, team);
    }
}