package co.edu.sena.productsreact.dto.auth;

public record ForgotPasswordResponse(
        String message,
        String resetLink
) {
}
