package com.harebusiness.form.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    @NotBlank(message = "The email field is required.")
    @Email(message = "The email must be a valid email address.")
    String email;

    @NotBlank(message = "The password field is required.")
    String password;

}
