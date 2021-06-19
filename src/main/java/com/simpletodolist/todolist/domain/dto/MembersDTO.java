package com.simpletodolist.todolist.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MembersDTO {

    @JsonProperty("members")
    List<MemberDTO> members = new ArrayList<>();

    public MembersDTO(List<MemberDTO> members) {
        this.members = members;
    }
}
