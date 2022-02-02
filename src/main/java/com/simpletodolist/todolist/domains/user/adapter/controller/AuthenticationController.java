package com.simpletodolist.todolist.domains.user.adapter.controller;

import com.simpletodolist.todolist.common.ApiResponse;
import com.simpletodolist.todolist.domains.user.adapter.controller.command.UserLoginRequest;
import com.simpletodolist.todolist.domains.user.adapter.controller.command.UserRegisterRequest;
import com.simpletodolist.todolist.domains.user.domain.User;
import com.simpletodolist.todolist.domains.user.service.port.UserCrudService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public")
public class AuthenticationController {

	private final UserCrudService memberService;
	private final AuthenticationManager authenticationManager;

	@PostMapping("/login")
	public ResponseEntity<Object> login(
		@RequestBody @Valid UserLoginRequest request
	) {
		Authentication authentication = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
		SecurityContext securityContext = SecurityContextHolder.getContext();
		securityContext.setAuthentication(authentication);
		return ResponseEntity.ok()
							 .build();
	}

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<User>> registerMember(
		@Valid @RequestBody UserRegisterRequest request
	) {
		return ResponseEntity.ok(ApiResponse.success(
			memberService.registerUser(request)));
	}
}
