package br.com.sales.hexagonal_architecture_demo.infrastructure.output.persistance.adapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Component;

import br.com.sales.hexagonal_architecture_demo.application.port.output.ProductRepository;
import br.com.sales.hexagonal_architecture_demo.domain.model.Product;
import br.com.sales.hexagonal_architecture_demo.domain.vo.ProductId;
import br.com.sales.hexagonal_architecture_demo.infrastructure.output.persistance.mapper.ProductEntityMapper;
import br.com.sales.hexagonal_architecture_demo.infrastructure.output.persistance.repository.SpringDataProductRepository;

@Component
public class JpaProductRepositoryAdapter implements ProductRepository {

    private final SpringDataProductRepository repository;
    private final ProductEntityMapper mapper;

    public JpaProductRepositoryAdapter(SpringDataProductRepository repository, ProductEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Product> findById(ProductId id) {
        return repository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Product save(Product product) {
        var entity = mapper.toEntity(product);
        var savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(ProductId id) {
        repository.deleteById(id.getValue());
    }
}