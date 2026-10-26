package dev.cartflow.api.services;
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) { super("Product not found: " + id); }
}
