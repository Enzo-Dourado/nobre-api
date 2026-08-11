package br.com.nobre.api.repository;
import br.com.nobre.api.model.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySlug(String slug);
    boolean existsBySlugAndIdNot(String slug, Long id);
    List<Product> findByCategoryIgnoreCaseOrderByIdAsc(String category);
}
