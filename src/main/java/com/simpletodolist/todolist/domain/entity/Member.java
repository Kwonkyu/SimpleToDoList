package com.simpletodolist.todolist.domain.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor
public class Member implements UserDetails {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "username", nullable = false, length = 32)
    private String username;

    @Column(name = "alias", nullable = false, length = 32)
    private String alias;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToMany(mappedBy = "member", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private final List<MemberTeamAssociation> teams = new ArrayList<>();

    @Column(name = "locked")
    private boolean locked;


    @Builder
    public Member(@NonNull String username,
                  @NonNull String alias,
                  @NonNull String password,
                  boolean locked) {
        if(username.isBlank()) {
            throw new IllegalArgumentException("Username should not be blank.");
        }

        this.username = username;
        changeAlias(alias);
        changePassword(password);
        this.locked = locked;
    }

    public List<Team> getTeamsReadOnly(){
        return teams.stream().map(MemberTeamAssociation::getTeam).collect(Collectors.toList());
    }

    public void changeAlias(@NonNull String alias) {
        if(alias.isBlank()) throw new IllegalArgumentException("Updated alias should not be blank.");
        this.alias = alias;
    }

    public void changePassword(@NonNull String password){
        if(password.isBlank()) throw new IllegalArgumentException("Updated password should not be blank.");
        this.password = password;
    }

    public void toggleLock() { locked = !locked; }

    public void lock() { locked = true; }

    public void unlock() { locked = false; }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return id == member.id &&
                locked == member.locked &&
                username.equals(member.username) &&
                alias.equals(member.alias) &&
                password.equals(member.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, alias, password, locked);
    }
}
