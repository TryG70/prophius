package com.trygod.prophiusassessment.api;

import com.trygod.prophiusassessment.data.UserData;
import com.trygod.prophiusassessment.dto.UserDto;
import com.trygod.prophiusassessment.dto.response.MessageResponse;
import com.trygod.prophiusassessment.dto.response.UserResponse;
import com.trygod.prophiusassessment.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "Endpoints for managing users")
public class UserApi {

    private final UserService<UserDto, UserData> userService;

    @PostMapping
    @Operation(summary = "Create a new user")
    public ResponseEntity<MessageResponse<UserDto>> createUser(@RequestBody @Valid UserDto userDto) {
        return new ResponseEntity<>(userService.create(userDto), CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing user by ID")
    public ResponseEntity<MessageResponse<UserDto>> updateUser(
            @PathVariable @Parameter(description = "User ID", required = true) Long id,
            @RequestBody UserDto userDto
    ) {
        return ResponseEntity.ok(userService.update(id, userDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user by ID")
    public ResponseEntity<Void> deleteUser(@PathVariable @Parameter(description = "User ID", required = true) Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a user by ID")
    public ResponseEntity<MessageResponse<UserResponse>> getUserById(
            @PathVariable @Parameter(description = "User ID", required = true) Long id
    ) {
        UserData userData = userService.findById(id);
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(userData, userResponse);
        return ResponseEntity.ok(MessageResponse.response(userResponse));
    }

    @GetMapping(params = {"page", "size", "sort", "search"})
    @Operation(summary = "Search for users by keyword")
    public ResponseEntity<MessageResponse<Page<UserDto>>> searchUsers(
            @RequestParam @Parameter(description = "Keyword to search for", required = true) String keyWord,
            Pageable pageable
    ) {
        MessageResponse<Page<UserDto>> userDtoPage = userService.search(keyWord, pageable);
        return ResponseEntity.ok(userDtoPage);
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Get a user by username")
    public ResponseEntity<MessageResponse<UserDto>> getUserByUsername(
            @PathVariable @Parameter(description = "Username", required = true) String username
    ) {
        MessageResponse<UserDto> userDto = userService.findByUsername(username);
        return ResponseEntity.ok(userDto);
    }

    @PatchMapping("/{followerId}/follow/{followeeId}")
    @Operation(summary = "Follow a user")
    public ResponseEntity<Void> followUser(
            @PathVariable @Parameter(description = "Follower ID", required = true) Long followerId,
            @PathVariable @Parameter(description = "Followee ID", required = true) Long followeeId
    ) {
        userService.followUser(followerId, followeeId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{followerId}/unfollow/{followeeId}")
    @Operation(summary = "Unfollow a user")
    public ResponseEntity<Void> unfollowUser(
            @PathVariable @Parameter(description = "Follower ID", required = true) Long followerId,
            @PathVariable @Parameter(description = "Followee ID", required = true) Long followeeId
    ) {
        userService.unfollowUser(followerId, followeeId);
        return ResponseEntity.noContent().build();
    }
}
