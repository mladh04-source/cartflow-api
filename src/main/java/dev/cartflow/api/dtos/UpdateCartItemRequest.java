package dev.cartflow.api.dtos;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
public record UpdateCartItemRequest(@Min(1) @Max(100) int quantity) {}
