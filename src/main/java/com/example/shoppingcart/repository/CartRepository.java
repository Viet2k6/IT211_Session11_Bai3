package com.example.shoppingcart.repository;

import com.example.shoppingcart.entity.ShoppingCart;

import java.util.Optional;

public interface CartRepository {

    Optional<ShoppingCart> findByUserId(Long userId);
    ShoppingCart save(ShoppingCart cart);
}
