package com.example.shoppingcart.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
public class ShoppingCart {

    private Long id;
    private Long userId;
    private List<CartItem> items;

    public ShoppingCart(Long userId) {
        this.userId = userId;
        this.items = new ArrayList<>();
    }

    public Optional<CartItem> findItemByProductId(Long productId) {
        return items.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();
    }

    public void addItem(CartItem item) {
        items.add(item);
    }

    public void removeItem(CartItem item) {
        items.remove(item);
    }
}