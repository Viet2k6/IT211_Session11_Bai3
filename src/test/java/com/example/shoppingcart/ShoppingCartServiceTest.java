package com.example.shoppingcart;

import com.example.shoppingcart.entity.CartItem;
import com.example.shoppingcart.entity.Product;
import com.example.shoppingcart.entity.ShoppingCart;
import com.example.shoppingcart.repository.CartRepository;
import com.example.shoppingcart.repository.ProductRepository;
import com.example.shoppingcart.service.ShoppingCartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private ShoppingCartService shoppingCartService;

    @Test
    void shouldAddNewProductToCart() {

        Product product = new Product(1L, "Laptop", 1000, 10);

        when(productRepository.findById(1L))
                .thenReturn(product);

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.empty());

        when(cartRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        ShoppingCart result =
                shoppingCartService.addProductToCart(1L, 1L, 2);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getQuantity())
                .isEqualTo(2);

        verify(cartRepository).save(any());
    }

    @Test
    void shouldIncreaseQuantityIfProductAlreadyExists() {

        Product product = new Product(1L, "Laptop", 1000, 10);

        ShoppingCart cart = new ShoppingCart(1L);
        cart.addItem(new CartItem(1L, 2));

        when(productRepository.findById(1L))
                .thenReturn(product);

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(cart));

        when(cartRepository.save(any()))
                .thenReturn(cart);

        ShoppingCart result =
                shoppingCartService.addProductToCart(1L, 1L, 3);

        assertThat(result.getItems().get(0).getQuantity())
                .isEqualTo(5);
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {

        when(productRepository.findById(1L))
                .thenReturn(null);

        assertThatThrownBy(() ->
                shoppingCartService.addProductToCart(1L, 1L, 1)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product not found");

        verify(cartRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenStockNotEnough() {

        Product product = new Product(1L, "Laptop", 1000, 2);

        when(productRepository.findById(1L))
                .thenReturn(product);

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                shoppingCartService.addProductToCart(1L, 1L, 5)
        )
                .isInstanceOf(IllegalStateException.class);

        verify(cartRepository, never()).save(any());
    }

    @Test
    void shouldUpdateProductQuantity() {

        Product product = new Product(1L, "Laptop", 1000, 10);

        ShoppingCart cart = new ShoppingCart(1L);
        cart.addItem(new CartItem(1L, 2));

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(cart));

        when(productRepository.findById(1L))
                .thenReturn(product);

        when(cartRepository.save(any()))
                .thenReturn(cart);

        ShoppingCart result =
                shoppingCartService.updateProductQuantity(1L, 1L, 5);

        assertThat(result.getItems().get(0).getQuantity())
                .isEqualTo(5);
    }

    @Test
    void shouldRemoveProductFromCart() {

        ShoppingCart cart = new ShoppingCart(1L);
        cart.addItem(new CartItem(1L, 2));

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(cart));

        when(cartRepository.save(any()))
                .thenReturn(cart);

        ShoppingCart result =
                shoppingCartService.removeProductFromCart(1L, 1L);

        assertThat(result.getItems()).isEmpty();
    }
}