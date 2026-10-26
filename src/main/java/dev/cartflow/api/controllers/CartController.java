package dev.cartflow.api.controllers;

import dev.cartflow.api.dtos.*;
import dev.cartflow.api.services.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.UUID;

@RestController @RequestMapping("/carts") @RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartDto> createCart() {
        var cart = cartService.createCart();
        return ResponseEntity.created(URI.create("/carts/" + cart.id())).body(cart);
    }

    @GetMapping("/{cartId}")
    public CartDto getCart(@PathVariable UUID cartId) { return cartService.getCart(cartId); }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartItemDto> addItem(@PathVariable UUID cartId, @Valid @RequestBody AddCartItemRequest request) {
        return ResponseEntity.ok(cartService.addItem(cartId, request.productId()));
    }

    @PutMapping("/{cartId}/items/{productId}")
    public CartItemDto updateItem(@PathVariable UUID cartId, @PathVariable Long productId,
                                  @Valid @RequestBody UpdateCartItemRequest request) {
        return cartService.updateItem(cartId, productId, request.quantity());
    }

    @DeleteMapping("/{cartId}/items/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@PathVariable UUID cartId, @PathVariable Long productId) {
        cartService.removeItem(cartId, productId);
    }

    @DeleteMapping("/{cartId}/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(@PathVariable UUID cartId) { cartService.clearCart(cartId); }
}
