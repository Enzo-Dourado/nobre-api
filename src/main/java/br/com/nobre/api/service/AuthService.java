package br.com.nobre.api.service;

import br.com.nobre.api.model.User;
import br.com.nobre.api.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    public static final String COOKIE_NAME = "nobre_session";
    private final UserRepository users;
    private final SecretKey key;
    private final boolean secure;
    private final String sameSite;

    public AuthService(UserRepository users, @Value("${app.jwt-secret}") String secret,
                       @Value("${app.cookie-secure}") boolean secure,
                       @Value("${app.cookie-same-site}") String sameSite) {
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) throw new IllegalStateException("JWT_SECRET deve ter ao menos 32 caracteres");
        this.users = users; this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.secure = secure; this.sameSite = sameSite;
    }

    public String token(User user) {
        var now = Instant.now();
        return Jwts.builder().subject(user.id.toString()).claim("role", user.role.name())
            .issuedAt(java.util.Date.from(now)).expiration(java.util.Date.from(now.plus(7, ChronoUnit.DAYS)))
            .signWith(key).compact();
    }

    public Optional<User> currentUser(HttpServletRequest request) {
        if (request.getCookies() == null) return Optional.empty();
        return Arrays.stream(request.getCookies()).filter(c -> COOKIE_NAME.equals(c.getName())).findFirst().flatMap(this::parse);
    }

    private Optional<User> parse(Cookie cookie) {
        try {
            var id = Long.valueOf(Jwts.parser().verifyWith(key).build().parseSignedClaims(cookie.getValue()).getPayload().getSubject());
            return users.findById(id);
        } catch (RuntimeException ignored) { return Optional.empty(); }
    }

    public ResponseCookie sessionCookie(User user) { return cookie(token(user), 604800); }
    public ResponseCookie clearCookie() { return cookie("", 0); }
    private ResponseCookie cookie(String value, long age) {
        return ResponseCookie.from(COOKIE_NAME, value).httpOnly(true).secure(secure).sameSite(sameSite).path("/").maxAge(age).build();
    }
}
