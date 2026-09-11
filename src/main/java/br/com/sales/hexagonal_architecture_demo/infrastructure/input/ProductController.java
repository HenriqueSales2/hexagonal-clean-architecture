package br.com.sales.hexagonal_architecture_demo.infrastructure.input;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.sales.hexagonal_architecture_demo.application.port.input.ProductManagementUseCase;
import br.com.sales.hexagonal_architecture_demo.application.port.input.command.CreateProductCommand;
import br.com.sales.hexagonal_architecture_demo.application.port.input.command.UpdateProductCommand;
import br.com.sales.hexagonal_architecture_demo.application.port.input.response.ProductResponse;
import br.com.sales.hexagonal_architecture_demo.domain.vo.ProductId;
import br.com.sales.hexagonal_architecture_demo.infrastructure.input.request.CreateProductRequest;
import br.com.sales.hexagonal_architecture_demo.infrastructure.input.request.UpdateProductRequest;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductManagementUseCase productManagementUseCase;

    public ProductController(ProductManagementUseCase productManagementUseCase) {
        this.productManagementUseCase = productManagementUseCase;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody CreateProductRequest request) {
        CreateProductCommand command = new CreateProductCommand(
                request.getName(),
                request.getPrice()
        );

        ProductResponse response = productManagementUseCase.createProduct(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable String id) {
        ProductId productId = new ProductId(id);
        ProductResponse response = productManagementUseCase.findProduct(productId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> responses = productManagementUseCase.findAllProducts();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String id,
            @RequestBody UpdateProductRequest request
    ) {
        ProductId productId = new ProductId(id);
        UpdateProductCommand command = new UpdateProductCommand(
                request.getName(),
                request.getPrice()
        );

        ProductResponse response = productManagementUseCase.updateProduct(productId, command);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductResponse> deleteProduct(@PathVariable String id) {
        ProductId productId = new ProductId(id);
        productManagementUseCase.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ProductResponse> activateProduct(@PathVariable String id) {
        ProductId productId = new ProductId(id);
        ProductResponse response = productManagementUseCase.activateProduct(productId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ProductResponse> deactivateProduct(@PathVariable String id) {
        ProductId productId = new ProductId(id);
        ProductResponse response = productManagementUseCase.deactivateProduct(productId);
        return ResponseEntity.ok(response);
    }
}