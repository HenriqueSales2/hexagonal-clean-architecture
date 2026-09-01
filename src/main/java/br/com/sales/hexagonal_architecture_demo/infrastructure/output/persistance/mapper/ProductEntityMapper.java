package br.com.sales.hexagonal_architecture_demo.infrastructure.output.persistance.mapper;

import org.springframework.stereotype.Component;

import br.com.sales.hexagonal_architecture_demo.domain.model.Product;
import br.com.sales.hexagonal_architecture_demo.domain.vo.Money;
import br.com.sales.hexagonal_architecture_demo.domain.vo.ProductId;
import br.com.sales.hexagonal_architecture_demo.domain.vo.ProductStatus;
import br.com.sales.hexagonal_architecture_demo.infrastructure.output.persistance.entity.ProductEntity;

@Component
public class ProductEntityMapper {

    public Product toDomain(ProductEntity entity) {
        var product = Product.create(
                new ProductId(entity.getId()),
                entity.getName(),
                new Money(entity.getPrice())
        );

        if (entity.getStatus() != product.getStatus()) {
            if (entity.getStatus().equals(ProductStatus.ACTIVE)) {
                product.activate();
            }
            else {
                product.deactivate();
            }
        }
        return product;
    }

    public ProductEntity toEntity(Product domain) {
        return new ProductEntity(
                domain.getId().getValue(),
                domain.getName(),
                domain.getPrice().getAmount(),
                domain.getStatus()
        );
    }
}