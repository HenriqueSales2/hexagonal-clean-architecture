package br.com.sales.hexagonal_architecture_demo.domain.exception;

import br.com.sales.hexagonal_architecture_demo.domain.vo.ProductId;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(ProductId id) {
        super("Product not found with id: " + id.getValue());
    }
}