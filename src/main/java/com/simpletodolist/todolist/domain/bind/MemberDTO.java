package com.simpletodolist.todolist.domain.bind;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.domain.entity.Member;
import lombok.Getter;
import org.springframework.beans.BeanUtils;

@Getter
public class MemberDTO {
    @JsonProperty("id")
    private long id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("alias")
    private String alias;

    @JsonProperty("password")
    private String password;

    @JsonProperty("locked")
    boolean locked;


    public MemberDTO(Member member) {
        // test usage of copyProperties.
        BeanUtils.copyProperties(member, this);
    }
}
