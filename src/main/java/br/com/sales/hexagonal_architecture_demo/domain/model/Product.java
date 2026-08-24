package br.com.sales.hexagonal_architecture_demo.domain.model;

import br.com.sales.hexagonal_architecture_demo.domain.exception.InvalidProductException;
import br.com.sales.hexagonal_architecture_demo.domain.vo.Money;
import br.com.sales.hexagonal_architecture_demo.domain.vo.ProductId;
import br.com.sales.hexagonal_architecture_demo.domain.vo.ProductStatus;

public class Product {

    private ProductId id;
    private String name;
    private Money price;
    private ProductStatus status;

    protected Product() {}

    private Product(ProductId id, String name, Money price) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.status = price.isLessThanZero() ? ProductStatus.INACTIVE : ProductStatus.ACTIVE;
    }

    public ProductId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Money getPrice() {
        return price;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public static Product create(ProductId id, String name, Money price) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidProductException("Product name cannot be empty");
        }
        if (price == null) {
            throw new InvalidProductException("Product price cannot be null");
        }

        return new Product(id, name, price);
    }

    public void update(String name, Money price) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }

        if (price != null) {
            this.price = price;
            if (this.status == ProductStatus.ACTIVE && price.isLessThanZero()) {
                this.status = ProductStatus.INACTIVE;
            }
        }
    }

    public void activate() {
        if (this.price.isLessThanZero()) {
            throw new InvalidProductException("Cannot activate product with negative price");
        }
        this.status = ProductStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = ProductStatus.INACTIVE;
    }
}
