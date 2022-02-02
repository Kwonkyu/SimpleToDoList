package com.simpletodolist.todolist.domains.user.adapter.repository;

import com.simpletodolist.todolist.domains.user.domain.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

	Optional<UserEntity> findByUsername(String username);

	default UserEntity findUserByUsername(String username) {
		return findByUsername(username)
			.orElseThrow(() -> new UserNotFoundException(username));
	}

	boolean existsByUsername(String username);
}
