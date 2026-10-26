package dev.cartflow.api;

import dev.cartflow.api.entities.Cart;
import dev.cartflow.api.entities.Product;
import dev.cartflow.api.repositories.CartRepository;
import dev.cartflow.api.repositories.ProductRepository;
import dev.cartflow.api.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
    @Mock CartRepository cartRepository;
    @Mock ProductRepository productRepository;
    @InjectMocks CartService cartService;

    private Cart cart;
    private Product product;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        product = new Product();
        product.setId(1L);
        product.setName("Keyboard");
        product.setPrice(new BigDecimal("20.00"));
    }

    @Test
    void createCartReturnsEmptyCart() {
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));
        var result = cartService.createCart();
        assertNotNull(result.id());
        assertTrue(result.items().isEmpty());
        assertEquals(0, result.totalPrice().compareTo(BigDecimal.ZERO));
    }

    @Test
    void addingSameProductIncrementsQuantityAndTotals() {
        when(cartRepository.findById(cart.getId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.save(cart)).thenReturn(cart);
        assertEquals(1, cartService.addItem(cart.getId(), 1L).quantity());
        var updated = cartService.addItem(cart.getId(), 1L);
        assertEquals(2, updated.quantity());
        assertEquals(new BigDecimal("40.00"), updated.totalPrice());
        assertEquals(1, cart.getItems().size());
    }

    @Test
    void updateRejectsInvalidQuantity() {
        assertThrows(IllegalArgumentException.class, () -> cartService.updateItem(cart.getId(), 1L, 0));
        assertThrows(IllegalArgumentException.class, () -> cartService.updateItem(cart.getId(), 1L, 101));
    }

    @Test
    void unknownCartReturnsNotFoundException() {
        UUID id = cart.getId();
        when(cartRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(CartNotFoundException.class, () -> cartService.getCart(id));
    }

    @Test
    void unknownProductReturnsBadRequestException() {
        when(cartRepository.findById(cart.getId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> cartService.addItem(cart.getId(), 999L));
    }

    @Test
    void removeAndClearCart() {
        when(cartRepository.findById(cart.getId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.save(cart)).thenReturn(cart);
        cartService.addItem(cart.getId(), 1L);
        cartService.removeItem(cart.getId(), 1L);
        assertTrue(cart.getItems().isEmpty());
        cartService.addItem(cart.getId(), 1L);
        cartService.clearCart(cart.getId());
        assertTrue(cart.getItems().isEmpty());
    }
}
