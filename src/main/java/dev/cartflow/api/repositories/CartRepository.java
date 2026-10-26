package dev.cartflow.api.repositories;

import dev.cartflow.api.entities.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    @Override
    @EntityGraph(attributePaths = {"items", "items.product"})
    Optional<Cart> findById(UUID id);
}
