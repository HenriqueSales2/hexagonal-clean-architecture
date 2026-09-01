package br.com.sales.hexagonal_architecture_demo.application.port.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sales.hexagonal_architecture_demo.application.port.input.ProductManagementUseCase;
import br.com.sales.hexagonal_architecture_demo.application.port.input.command.CreateProductCommand;
import br.com.sales.hexagonal_architecture_demo.application.port.input.command.UpdateProductCommand;
import br.com.sales.hexagonal_architecture_demo.application.port.input.response.ProductResponse;
import br.com.sales.hexagonal_architecture_demo.application.port.output.ProductEventPublisher;
import br.com.sales.hexagonal_architecture_demo.application.port.output.ProductRepository;
import br.com.sales.hexagonal_architecture_demo.domain.exception.ProductNotFoundException;
import br.com.sales.hexagonal_architecture_demo.domain.model.Product;
import br.com.sales.hexagonal_architecture_demo.domain.vo.Money;
import br.com.sales.hexagonal_architecture_demo.domain.vo.ProductId;

@Service
@Transactional
public class ProductManagementService implements ProductManagementUseCase {

    private final ProductRepository productRepository;
    private final ProductEventPublisher eventPublisher;
    private final ProductMapper productMapper;

    public ProductManagementService(
            ProductRepository productRepository,
            ProductEventPublisher eventPublisher,
            ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
        this.productMapper = productMapper;
    }

    @Override
    public ProductResponse createProduct(CreateProductCommand command) {
        Product product = Product.create(
                ProductId.generate(),
                command.name(),
                new Money(command.price())
        );

        Product savedProduct = productRepository.save(product);

        eventPublisher.publishProductCreated(savedProduct);

        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse findProduct(ProductId id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> findAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse updateProduct(ProductId id, UpdateProductCommand command) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.update(
                command.name(),
                command.price() != null ? new Money(command.price()) : null
        );

            Product savedProduct = productRepository.save(product);

            eventPublisher.publishProductUpdated(savedProduct);

            return productMapper.toResponse(savedProduct);
    }

    @Override
    public void deleteProduct(ProductId id) {
        if (productRepository.findById(id).isEmpty()) {
            throw new ProductNotFoundException(id);
        }

        productRepository.deleteById(id);

        eventPublisher.publishProductDeleted(id);
    }

    @Override
    public ProductResponse activateProduct(ProductId id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.activate();

        Product savedProduct = productRepository.save(product);

        eventPublisher.publishProductActivated(savedProduct);

        return productMapper.toResponse(savedProduct);
    }

    @Override
    public ProductResponse deactivateProduct(ProductId id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.deactivate();

        Product savedProduct = productRepository.save(product);

        eventPublisher.publishProductDeactivated(savedProduct);

        return productMapper.toResponse(savedProduct);
    }
}