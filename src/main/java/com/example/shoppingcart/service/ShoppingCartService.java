package com.example.shoppingcart.service;

import com.example.shoppingcart.entity.CartItem;
import com.example.shoppingcart.entity.Product;
import com.example.shoppingcart.entity.ShoppingCart;
import com.example.shoppingcart.repository.CartRepository;
import com.example.shoppingcart.repository.ProductRepository;

import java.util.Optional;

public class ShoppingCartService {

    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    public ShoppingCartService(ProductRepository productRepository,
                               CartRepository cartRepository) {
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
    }

    public ShoppingCart addProductToCart(Long userId,
                                         Long productId,
                                         int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Product product = productRepository.findById(productId);

        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }

        ShoppingCart cart = cartRepository.findByUserId(userId)
                .orElse(new ShoppingCart(userId));

        Optional<CartItem> existingItem =
                cart.findItemByProductId(productId);

        int currentQuantity =
                existingItem.map(CartItem::getQuantity).orElse(0);

        int totalQuantity = currentQuantity + quantity;

        if (product.getStock() < totalQuantity) {
            throw new IllegalStateException(
                    "Not enough stock for product: " + product.getName()
            );
        }

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(totalQuantity);
        } else {
            cart.addItem(new CartItem(productId, quantity));
        }

        return cartRepository.save(cart);
    }

    public ShoppingCart updateProductQuantity(Long userId,
                                              Long productId,
                                              int newQuantity) {

        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        ShoppingCart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Cart not found for user: " + userId
                        )
                );

        CartItem item = cart.findItemByProductId(productId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Product not found in cart: " + productId
                        )
                );

        Product product = productRepository.findById(productId);

        if (product == null) {
            throw new IllegalArgumentException(
                    "Product not found in database"
            );
        }

        if (product.getStock() < newQuantity) {
            throw new IllegalStateException(
                    "Not enough stock for product: " + product.getName()
            );
        }

        item.setQuantity(newQuantity);

        return cartRepository.save(cart);
    }

    public ShoppingCart removeProductFromCart(Long userId,
                                              Long productId) {

        ShoppingCart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Cart not found for user: " + userId
                        )
                );

        CartItem item = cart.findItemByProductId(productId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Product not found in cart: " + productId
                        )
                );

        cart.removeItem(item);

        return cartRepository.save(cart);
    }
}