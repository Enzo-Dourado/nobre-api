package br.com.nobre.api.repository;
import br.com.nobre.api.model.CustomerOrder;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {
    @EntityGraph(attributePaths="items") List<CustomerOrder> findByUserIdOrderByCreatedAtDesc(Long userId);
}
