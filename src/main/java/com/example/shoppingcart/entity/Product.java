package com.example.shoppingcart.entity;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private Long id;
    private String name;
    private double price;
    private int stock;
}