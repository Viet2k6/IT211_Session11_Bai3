package com.example.shoppingcart.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    private Long productId;
    private int quantity;
}