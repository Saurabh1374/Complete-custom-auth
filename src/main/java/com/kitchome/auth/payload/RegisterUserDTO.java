package com.kitchome.auth.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class RegisterUserDTO {
	private String username;
    private String email;
    private String password;
}
