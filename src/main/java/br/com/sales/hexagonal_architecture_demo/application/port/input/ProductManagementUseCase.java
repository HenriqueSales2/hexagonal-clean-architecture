package br.com.sales.hexagonal_architecture_demo.application.port.input;

import br.com.sales.hexagonal_architecture_demo.application.port.input.command.CreateProductCommand;
import br.com.sales.hexagonal_architecture_demo.application.port.input.command.UpdateProductCommand;
import br.com.sales.hexagonal_architecture_demo.application.port.input.response.ProductResponse;
import br.com.sales.hexagonal_architecture_demo.domain.vo.ProductId;

import java.util.List;

public interface ProductManagementUseCase {

    ProductResponse createProduct(CreateProductCommand command);
    ProductResponse findProduct(ProductId id);
    List<ProductResponse> findAllProducts();
    ProductResponse updateProduct(ProductId id, UpdateProductCommand command);
    void deleteProduct(ProductId id);
    ProductResponse activateProduct(ProductId id);
    ProductResponse deactivateProduct(ProductId id);
}