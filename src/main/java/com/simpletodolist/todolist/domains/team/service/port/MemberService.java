package com.simpletodolist.todolist.domains.team.service.port;

import com.simpletodolist.todolist.domains.team.domain.Members;

public interface MemberService {

	Members getJoinedMembers(Long teamId);

	Members inviteMember(Long teamId, String invitedUsername);

	Members withdrawMember(Long teamId, String withdrawnUsername);

}
