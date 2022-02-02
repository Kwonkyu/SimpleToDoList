package com.simpletodolist.todolist.domains.user.domain;

import com.simpletodolist.todolist.domains.team.domain.MemberEntity;
import com.simpletodolist.todolist.domains.team.domain.TeamEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
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
@Table(name = "user_entity")
public class UserEntity implements UserDetails {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", nullable = false, length = 32, unique = true)
    private String username;

    @Column(name = "alias", nullable = false, length = 64)
    private String alias;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private final List<MemberEntity> teams = new ArrayList<>();

    @Column(name = "locked")
    private boolean locked;

    public void addTeamRegistration(MemberEntity registration) {
        this.teams.add(registration);
    }

    public void deleteTeamRegistration(MemberEntity registration) {
        this.teams.remove(registration);
    }

    @Builder
    public UserEntity(@NonNull String username,
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

    public List<TeamEntity> getTeamsReadOnly(){
        return teams.stream().map(MemberEntity::getTeam).collect(Collectors.toList());
    }

    public void changeAlias(@NonNull String alias) {
        if(alias.isBlank()) throw new IllegalArgumentException("Updated alias should not be blank.");
        this.alias = alias;
    }

    public void changePassword(@NonNull String password){
        if(password.isBlank()) throw new IllegalArgumentException("Updated password should not be blank.");
        this.password = password;
    }

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
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(
            o)) {
            return false;
        }
        UserEntity that = (UserEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
