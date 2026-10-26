package dev.cartflow.api.controllers;

import dev.cartflow.api.dtos.ProductRequest;
import dev.cartflow.api.dtos.ProductResponse;
import dev.cartflow.api.entities.Product;
import dev.cartflow.api.repositories.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController @RequestMapping("/products") @RequiredArgsConstructor
public class ProductController {
    private final ProductRepository productRepository;

    @GetMapping
    public List<ProductResponse> listProducts() {
        return productRepository.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(p -> ResponseEntity.ok(toResponse(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        var product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        var saved = productRepository.save(product);
        return ResponseEntity.created(URI.create("/products/" + saved.getId())).body(toResponse(saved));
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice());
    }
}
