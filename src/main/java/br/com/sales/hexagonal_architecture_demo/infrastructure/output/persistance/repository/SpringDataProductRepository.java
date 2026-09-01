package br.com.sales.hexagonal_architecture_demo.infrastructure.output.persistance.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.sales.hexagonal_architecture_demo.infrastructure.output.persistance.entity.ProductEntity;

@Repository
public interface SpringDataProductRepository extends CrudRepository<ProductEntity, String> {
}
