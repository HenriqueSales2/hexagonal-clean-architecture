package br.com.sales.hexagonal_architecture_demo.application.port.service;

import org.springframework.stereotype.Component;

import br.com.sales.hexagonal_architecture_demo.application.port.input.response.ProductResponse;
import br.com.sales.hexagonal_architecture_demo.domain.model.Product;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId().getValue())
                .name(product.getName())
                .price(product.getPrice().getAmount())
                .status(product.getStatus().name())
                .build();
    }
}