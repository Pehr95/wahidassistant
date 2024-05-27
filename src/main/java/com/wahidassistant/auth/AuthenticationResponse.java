package com.wahidassistant.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
// A class for the authentication request. Author Pehr Nortén.
public class AuthenticationResponse {
    private String token;
}
