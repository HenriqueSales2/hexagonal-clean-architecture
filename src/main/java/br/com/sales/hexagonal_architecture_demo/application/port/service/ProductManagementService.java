package br.com.sales.hexagonal_architecture_demo.application.port.service;

import java.util.List;
import java.util.stream.Collectors;

import br.com.sales.hexagonal_architecture_demo.application.port.input.ProductManagementUseCase;
import br.com.sales.hexagonal_architecture_demo.application.port.input.command.CreateProductCommand;
import br.com.sales.hexagonal_architecture_demo.application.port.input.command.UpdateProductCommand;
import br.com.sales.hexagonal_architecture_demo.application.port.input.response.ProductResponse;
import br.com.sales.hexagonal_architecture_demo.application.port.output.ProductEventPublisher;
import br.com.sales.hexagonal_architecture_demo.application.port.output.ProductRepository;
import br.com.sales.hexagonal_architecture_demo.domain.exception.InvalidProductException;
import br.com.sales.hexagonal_architecture_demo.domain.exception.ProductNotFoundException;
import br.com.sales.hexagonal_architecture_demo.domain.model.Product;
import br.com.sales.hexagonal_architecture_demo.domain.vo.Money;
import br.com.sales.hexagonal_architecture_demo.domain.vo.ProductId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductManagementService implements ProductManagementUseCase {

    private final ProductRepository productRepository;
    private final ProductEventPublisher eventPublisher;
    private final ProductMapper productMapper;

    // this constructor need infrastructure (Component and Repository), fix later
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
                command.getName(),
                new Money(command.getPrice())
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
                .map(product ->
                        productMapper.toResponse(product)) // change for lambda here, fix later
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse updateProduct(ProductId id, UpdateProductCommand command) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        Money money = new Money(command.getPrice());

        if (money == null) {
            throw new InvalidProductException("");
        }
        else {
            product.update(
                    command.getName(),
                    money
                    );

            Product savedProduct = productRepository.save(product);

            eventPublisher.publishProductUpdated(savedProduct);

            return productMapper.toResponse(savedProduct);
        }
    }

    @Override
    public void deleteProduct(ProductId id) {
        if (!productRepository.findById(id).isPresent()) {
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