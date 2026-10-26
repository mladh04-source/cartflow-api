package dev.cartflow.api.services;

import dev.cartflow.api.dtos.*;
import dev.cartflow.api.entities.*;
import dev.cartflow.api.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CartDto createCart() {
        return toDto(cartRepository.save(new Cart()));
    }

    @Transactional(readOnly = true)
    public CartDto getCart(UUID cartId) { return toDto(findCart(cartId)); }

    @Transactional
    public CartItemDto addItem(UUID cartId, Long productId) {
        var cart = findCart(cartId);
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        var item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst().orElse(null);
        if (item == null) {
            item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(1);
            cart.getItems().add(item);
        } else {
            if (item.getQuantity() >= 100) throw new IllegalArgumentException("Quantity cannot exceed 100");
            item.setQuantity(item.getQuantity() + 1);
        }
        cartRepository.save(cart);
        return toDto(item);
    }

    @Transactional
    public CartItemDto updateItem(UUID cartId, Long productId, int quantity) {
        if (quantity < 1 || quantity > 100) throw new IllegalArgumentException("Quantity must be between 1 and 100");
        var cart = findCart(cartId);
        var item = findItem(cart, productId);
        item.setQuantity(quantity);
        cartRepository.save(cart);
        return toDto(item);
    }

    @Transactional
    public void removeItem(UUID cartId, Long productId) {
        var cart = findCart(cartId);
        var item = findItem(cart, productId);
        cart.getItems().remove(item);
        cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(UUID cartId) {
        var cart = findCart(cartId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private Cart findCart(UUID cartId) {
        return cartRepository.findById(cartId).orElseThrow(() -> new CartNotFoundException(cartId));
    }

    private CartItem findItem(Cart cart, Long productId) {
        return cart.getItems().stream().filter(i -> i.getProduct().getId().equals(productId))
                .findFirst().orElseThrow(() -> new CartItemNotFoundException(productId));
    }

    private CartDto toDto(Cart cart) {
        var items = cart.getItems().stream().map(this::toDto).toList();
        var total = items.stream().map(CartItemDto::totalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartDto(cart.getId(), items, total);
    }

    private CartItemDto toDto(CartItem item) {
        var p = item.getProduct();
        return new CartItemDto(new CartProductDto(p.getId(), p.getName(), p.getPrice()),
                item.getQuantity(), p.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
    }
}
