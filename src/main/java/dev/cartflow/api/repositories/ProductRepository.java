package dev.cartflow.api.repositories;
import dev.cartflow.api.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductRepository extends JpaRepository<Product, Long> {}
