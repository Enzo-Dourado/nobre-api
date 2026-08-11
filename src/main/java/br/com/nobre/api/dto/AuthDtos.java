package br.com.nobre.api.dto;

import br.com.nobre.api.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDtos {
    private AuthDtos() {}
    public record RegisterRequest(@NotBlank @Size(max=120) String name, @NotBlank @Email @Size(max=180) String email,
                                  @NotBlank @Size(min=6, max=72) String password, @Size(max=30) String phone) {}
    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {}
    public record UserResponse(Long id, String name, String email, String role) {
        public static UserResponse from(User u) { return new UserResponse(u.id, u.name, u.email, u.role.name()); }
    }
}
