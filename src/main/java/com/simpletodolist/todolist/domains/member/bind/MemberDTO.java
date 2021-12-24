package com.simpletodolist.todolist.domains.member.bind;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simpletodolist.todolist.domains.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@Getter
@Setter // used at jwt token util to deserialize jwt subject.
@NoArgsConstructor
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
        this.password = "ENCRYPTED";
    }
}
