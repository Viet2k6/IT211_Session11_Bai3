package com.example.shoppingcart.repository;

import com.example.shoppingcart.entity.Product;

public interface ProductRepository {

    Product findById(Long id);
    void save(Product product);
}
