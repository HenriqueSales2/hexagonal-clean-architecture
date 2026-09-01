package br.com.sales.hexagonal_architecture_demo.infrastructure.output.persistance.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.sales.hexagonal_architecture_demo.domain.vo.Money;
import br.com.sales.hexagonal_architecture_demo.domain.model.Product;
import br.com.sales.hexagonal_architecture_demo.domain.vo.ProductId;
import br.com.sales.hexagonal_architecture_demo.infrastructure.output.persistance.entity.ProductEntity;
import br.com.sales.hexagonal_architecture_demo.infrastructure.output.persistance.mapper.ProductEntityMapper;
import br.com.sales.hexagonal_architecture_demo.infrastructure.output.persistance.repository.SpringDataProductRepository;

@ExtendWith(MockitoExtension.class)
public class JpaProductRepositoryAdapterTest {

    @Mock
    private SpringDataProductRepository repository;

    @Mock
    private ProductEntityMapper mapper;

    @InjectMocks
    private JpaProductRepositoryAdapter adapter;

    private ProductId productId;
    private Product product;
    private ProductEntity entity;

    @BeforeEach
    void setUp() {
        productId = new ProductId("123");
        product = Product.create(productId, "Test Product", new Money(50.0));

        entity = new ProductEntity();
        entity.setId(productId.getValue());
        entity.setName("Test Product");
        entity.setPrice(product.getPrice().getAmount());
        entity.setStatus(product.getStatus());
    }

    @Test
    void shouldFindById() {
        when(repository.findById(productId.getValue())).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(product);

        Optional<Product> result = adapter.findById(productId);

        assertTrue(result.isPresent());
        assertEquals(product, result.get());

        verify(repository).findById(productId.getValue());
        verify(mapper).toDomain(entity);
    }

    @Test
    void shouldReturnEmptyWhenNotFound() {
        when(repository.findById(productId.getValue())).thenReturn(Optional.empty());

        Optional<Product> result = adapter.findById(productId);

        assertFalse(result.isPresent());

        verify(repository).findById(productId.getValue());
        verify(mapper, never()).toDomain(any());
    }

    @Test
    void shouldFindAll() {
        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(product);

        List<Product> result = adapter.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(product, result.getFirst());

        verify(repository).findAll();
        verify(mapper).toDomain(entity);
    }

    @Test
    void shouldSave() {
        when(mapper.toEntity(product)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(product);

        Product result = adapter.save(product);

        assertNotNull(result);
        assertEquals(product, result);

        verify(mapper).toEntity(product);
        verify(repository).save(entity);
        verify(mapper).toDomain(entity);
    }

    @Test
    void shouldDeleteById() {
        adapter.deleteById(productId);

        verify(repository).deleteById(productId.getValue());
    }
}