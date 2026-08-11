package br.com.nobre.api.repository;
import br.com.nobre.api.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<User, Long> { Optional<User> findByEmailIgnoreCase(String email); }
