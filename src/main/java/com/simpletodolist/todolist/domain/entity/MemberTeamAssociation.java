package com.simpletodolist.todolist.domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class MemberTeamAssociation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_TEAM_ASSOC_ID")
    private long id;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;




}
