package br.com.sales.hexagonal_architecture_demo.application.port.output;

import java.util.List;
import java.util.Optional;

import br.com.sales.hexagonal_architecture_demo.domain.model.Product;
import br.com.sales.hexagonal_architecture_demo.domain.vo.ProductId;

public interface ProductRepository {

    Optional<Product> findById(ProductId id);
    List<Product> findAll();
    Product save(Product product);
    void deleteById(ProductId id);
}