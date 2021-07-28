package com.simpletodolist.todolist.domain.entity;

import com.simpletodolist.todolist.domain.bind.TeamDTO;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Member implements UserDetails {

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

    @Column(name = "LOCKED")
    private boolean locked;

    // TODO: 1+N check.
    public List<TeamDTO.Response> getTeamsDTO(){
        return teams.stream().map(MemberTeamAssociation::getTeam).map(TeamDTO.Response::new).collect(Collectors.toList());
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

    public void toggleLock() { locked = !locked; }

    public void lock() { locked = true; }

    public void unlock() { locked = false; }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !locked;
    }

}
