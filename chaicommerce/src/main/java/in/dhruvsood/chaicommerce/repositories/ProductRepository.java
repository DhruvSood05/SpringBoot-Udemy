package in.dhruvsood.chaicommerce.repositories;

import in.dhruvsood.chaicommerce.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

}
