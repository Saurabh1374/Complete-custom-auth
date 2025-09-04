package com.kitchome.auth.payload;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor


public class RegisterUserDTO {
	@NotNull
	private String username;
    @NotNull
    @Email
    private String email;
    @NotNull
    private String password;
}
