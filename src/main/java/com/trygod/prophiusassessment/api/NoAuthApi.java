package com.trygod.prophiusassessment.api;

import com.trygod.prophiusassessment.data.UserData;
import com.trygod.prophiusassessment.dto.UserDto;
import com.trygod.prophiusassessment.dto.request.AuthenticateUserDto;
import com.trygod.prophiusassessment.dto.response.MessageResponse;
import com.trygod.prophiusassessment.dto.response.StorageResponse;
import com.trygod.prophiusassessment.dto.response.UserResponse;
import com.trygod.prophiusassessment.service.StorageService;
import com.trygod.prophiusassessment.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.CREATED;


@RestController
@RequestMapping("/api/users/no-auth")
@RequiredArgsConstructor
@Tag(name = "No Auth Api", description = "Endpoints for managing user registration/authentication")
public class NoAuthApi {

    private final UserService<UserDto, UserData, UserResponse> userService;

    //Choose the storage service to use
//    @Qualifier("azureBlobStorageService")
    private final StorageService storageService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Create a new user")
    public ResponseEntity<MessageResponse<UserResponse>> createUser(@RequestBody @Valid UserDto userDto, @RequestPart("docxFile") MultipartFile docxFile) {
        userDto.setProfilePicture(savePicture(docxFile));
        UserResponse userResponse = userService.create(userDto);
        return new ResponseEntity<>(MessageResponse.response(userResponse), CREATED);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<MessageResponse<String>> authenticateUser (@RequestBody @Valid AuthenticateUserDto request){
        String token = userService.authenticateUser(request);
        return new ResponseEntity<>(MessageResponse.response(token), ACCEPTED);
    }

    private String savePicture(MultipartFile docxFile) {
        String fileName = storageService.store(docxFile.getOriginalFilename(), docxFile);
        StorageResponse storageResponse = storageService.details(fileName);
        return storageResponse.getUrl();
    }
}
