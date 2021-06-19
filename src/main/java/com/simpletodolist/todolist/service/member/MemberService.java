package com.simpletodolist.todolist.service.member;

import com.simpletodolist.todolist.domain.dto.MemberDTO;
import com.simpletodolist.todolist.domain.dto.TeamsDTO;
import com.simpletodolist.todolist.exception.DuplicatedMemberException;
import com.simpletodolist.todolist.exception.NoMemberFoundException;

public interface MemberService {

    /**
     * Get general information of member.
     * @param memberUserId Member's user id.
     * @return MemberDTO object filled with found member's information.
     * @throws NoMemberFoundException when no member found with given user id.
     */
    MemberDTO getMemberDetails(String memberUserId) throws NoMemberFoundException;

    /**
     * Register new member.
     * @param memberDTO Registering member's information.
     * @return MemberDTO object filled with registered member's information.
     * @throws DuplicatedMemberException when member with equivalent user id exists.
     */
    MemberDTO registerMember(MemberDTO memberDTO) throws DuplicatedMemberException;

    /**
     * Withdraw member.
     * @param memberUserId Member to withdraw.
     * @throws NoMemberFoundException when given member does not exists.
     */
    void withdrawMember(String memberUserId, String rawPassword) throws NoMemberFoundException;

    /**
     * Get list of teams members joined.
     * @param memberUserId Member's user id.
     * @return TeamsDTO object filled with found member's teams.
     * @throws NoMemberFoundException when no member found with given user id.
     */
    TeamsDTO getTeamsOfMember(String memberUserId) throws NoMemberFoundException;
}
