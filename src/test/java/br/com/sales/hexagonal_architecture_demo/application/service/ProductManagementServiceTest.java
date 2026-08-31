package br.com.sales.hexagonal_architecture_demo.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import br.com.sales.hexagonal_architecture_demo.application.port.input.command.CreateProductCommand;
import br.com.sales.hexagonal_architecture_demo.application.port.input.command.UpdateProductCommand;
import br.com.sales.hexagonal_architecture_demo.domain.exception.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.sales.hexagonal_architecture_demo.application.port.input.response.ProductResponse;
import br.com.sales.hexagonal_architecture_demo.application.port.output.ProductEventPublisher;
import br.com.sales.hexagonal_architecture_demo.application.port.output.ProductRepository;
import br.com.sales.hexagonal_architecture_demo.application.port.service.ProductManagementService;
import br.com.sales.hexagonal_architecture_demo.application.port.service.ProductMapper;
import br.com.sales.hexagonal_architecture_demo.domain.model.Product;
import br.com.sales.hexagonal_architecture_demo.domain.vo.Money;
import br.com.sales.hexagonal_architecture_demo.domain.vo.ProductId;

@ExtendWith(MockitoExtension.class)
public class ProductManagementServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductEventPublisher eventPublisher;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductManagementService productManagementService;

    private ProductId productId;
    private Product product;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        productId = new ProductId("123");
        product = Product.create(productId, "Test Product", new Money(50.0));

        productResponse = ProductResponse.builder()
                .id(productId.getValue())
                .name("Test Product")
                .price(product.getPrice().getAmount())
                .status(product.getStatus().name())
                .build();
    }

    @Test
    void shouldCreateProduct() {
        CreateProductCommand command = new CreateProductCommand("Test Product", 50.0);

        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        ProductResponse response = productManagementService.createProduct(command);

        assertNotNull(response);
        assertEquals(productResponse, response);

        verify(productRepository).save(any(Product.class));
        verify(eventPublisher).publishProductCreated(any(Product.class));
    }

    @Test
    void shouldFindProduct() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        ProductResponse response = productManagementService.findProduct(productId);

        assertNotNull(response);
        assertEquals(productResponse, response);

        verify(productRepository).findById(productId);
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> {
            productManagementService.findProduct(productId);
        });

        verify(productRepository).findById(productId);
    }

    @Test
    void shouldFindAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        List<ProductResponse> responses = productManagementService.findAllProducts();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(productResponse, responses.getFirst());

        verify(productRepository).findAll();
    }

    @Test
    void shouldUpdateProduct() {
        UpdateProductCommand command = new UpdateProductCommand("Update Product", 100.0);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        ProductResponse response = productManagementService.updateProduct(productId, command);

        assertNotNull(response);
        assertEquals(productResponse, response);

        verify(productRepository).findById(productId);
        verify(productRepository).save(any(Product.class));
        verify(eventPublisher).publishProductUpdated(product);
    }

    @Test
    void shouldDeleteProduct() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        productManagementService.deleteProduct(productId);

        verify(productRepository).findById(productId);
        verify(productRepository).deleteById(productId);
        verify(eventPublisher).publishProductDeleted(productId);
    }

    @Test
    void shouldActivateProduct() {
        product.deactivate();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        ProductResponse response = productManagementService.activateProduct(productId);

        assertNotNull(response);
        assertEquals(productResponse, response);

        verify(productRepository).findById(productId);
        verify(productRepository).save(any(Product.class));
        verify(eventPublisher).publishProductActivated(any(Product.class));
    }

    @Test
    void shouldDeactivateProduct() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        ProductResponse response = productManagementService.deactivateProduct(productId);

        assertNotNull(response);
        assertEquals(productResponse, response);

        verify(productRepository).findById(productId);
        verify(productRepository).save(any(Product.class));
        verify(eventPublisher).publishProductDeactivated(any(Product.class));
    }
}