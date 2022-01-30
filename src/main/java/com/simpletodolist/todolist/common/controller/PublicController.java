package com.simpletodolist.todolist.common.controller;

import com.simpletodolist.todolist.common.ApiResponse;
import com.simpletodolist.todolist.common.controller.command.UserLoginRequest;
import com.simpletodolist.todolist.common.controller.command.UserRegisterRequest;
import com.simpletodolist.todolist.domains.user.domain.User;
import com.simpletodolist.todolist.domains.user.service.BasicUserService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public")
public class PublicController {

	private final BasicUserService memberService;
	private final AuthenticationManager authenticationManager;

	@PostMapping("/login")
	public ResponseEntity<Object> login(
		@RequestBody @Valid UserLoginRequest request,
		HttpServletRequest httpServletRequest
	) {
		Authentication authentication = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		if (authentication.isAuthenticated()) {
			HttpSession session = httpServletRequest.getSession();
			session.setAttribute("username", authentication.getName());
		}

		return ResponseEntity.ok("logged in");
	}

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<User>> registerMember(
		@Valid @RequestBody UserRegisterRequest request
	) {
		return ResponseEntity.ok(ApiResponse.success(
			memberService.registerMember(request)));
	}
}
