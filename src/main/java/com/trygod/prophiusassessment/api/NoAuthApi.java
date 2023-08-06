package com.trygod.prophiusassessment.api;

import com.trygod.prophiusassessment.data.UserData;
import com.trygod.prophiusassessment.dto.UserDto;
import com.trygod.prophiusassessment.dto.request.AuthenticateUserDto;
import com.trygod.prophiusassessment.dto.response.MessageResponse;
import com.trygod.prophiusassessment.dto.response.UserResponse;
import com.trygod.prophiusassessment.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.CREATED;


@RestController
@RequestMapping("/api/users/no-auth")
@RequiredArgsConstructor
@Tag(name = "No Auth Api", description = "Endpoints for managing user registration/authentication")
public class NoAuthApi {

    private final UserService<UserDto, UserData, UserResponse> userService;

    @PostMapping
    @Operation(summary = "Create a new user")
    public ResponseEntity<MessageResponse<UserResponse>> createUser(@RequestBody @Valid UserDto userDto) {
        UserResponse userResponse = userService.create(userDto);
        return new ResponseEntity<>(MessageResponse.response(userResponse), CREATED);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<MessageResponse<String>> authenticateUser (@RequestBody @Valid AuthenticateUserDto request){
        String token = userService.authenticateUser(request);
        return new ResponseEntity<>(MessageResponse.response(token), ACCEPTED);
    }
}
