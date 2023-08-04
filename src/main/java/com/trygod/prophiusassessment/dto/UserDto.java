package com.trygod.prophiusassessment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto {


    @NotBlank(message = "Username is required")
    private String username;

    @Size(min = 6, max = 20, message = "Password length must be between {min} and {max} characters")
    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(regexp = "^(?=.{1,64}@)(?:(?!.*\\.\\.)[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?<!\\.)|\\s)\"?[\\p{L}0-9.!#$%&'*+/=?^_`{|}~-]+\"?@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$\n",
            message = "Email is invalid")
    private String email;


    private String profilePicture;
}
