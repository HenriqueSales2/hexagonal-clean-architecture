package br.com.sales.hexagonal_architecture_demo.domain;

import static org.junit.jupiter.api.Assertions.*;

import br.com.sales.hexagonal_architecture_demo.domain.exception.InvalidProductException;
import br.com.sales.hexagonal_architecture_demo.domain.model.Product;
import br.com.sales.hexagonal_architecture_demo.domain.vo.Money;
import br.com.sales.hexagonal_architecture_demo.domain.vo.ProductId;
import br.com.sales.hexagonal_architecture_demo.domain.vo.ProductStatus;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void shouldCreateActiveProductWithPositivePrice() {
        ProductId id = ProductId.generate();
        String name = "Test Product";
        Money price = new Money(50.0);

        Product product = Product.create(id, name, price);

        assertEquals(id, product.getId());
        assertEquals(name, product.getName());
        assertEquals(price, product.getPrice());
        assertEquals(ProductStatus.ACTIVE, product.getStatus());
    }

    @Test
    void shouldCreateInactiveProductWithNegativePrice() {
        ProductId id = ProductId.generate();
        String name = "Test Product";
        Money price = new Money(-50.0);

        Product product = Product.create(id, name, price);

        assertEquals(id, product.getId());
        assertEquals(name, product.getName());
        assertEquals(price, product.getPrice());
        assertEquals(ProductStatus.INACTIVE, product.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        ProductId id = ProductId.generate();
        String name = "";
        Money price = new Money(50);

        assertThrows(InvalidProductException.class, () -> {
            Product.create(id, name, price);
        });
    }

    @Test
    void shouldThrowExceptionWhenActivatingProductWithNegativePrice() {
        ProductId id = ProductId.generate();
        String name = "Test Product";
        Money price = new Money(-50.0);

        Product product = Product.create(id, name, price);

        assertThrows(InvalidProductException.class, () -> {
            product.activate();
        });
    }

    @Test
    void shouldDeactivateProduct() {
        ProductId id = ProductId.generate();
        String name = "Test Product";
        Money price = new Money(50.0);

        Product product = Product.create(id, name, price);

        product.deactivate();

        assertEquals(ProductStatus.INACTIVE, product.getStatus());
    }

    @Test
    void shouldActivateProduct() {
        ProductId id = ProductId.generate();
        String name = "Test Product";
        Money price = new Money(50.0);

        Product product = Product.create(id, name, price);

        product.deactivate();

        assertEquals(ProductStatus.INACTIVE, product.getStatus());

        product.activate();

        assertEquals(ProductStatus.ACTIVE, product.getStatus());
    }

    @Test
    void shouldUpdateProductName() {
        ProductId id = ProductId.generate();
        String name = "Test Product";
        Money price = new Money(50.0);

        Product product = Product.create(id, name, price);

        assertEquals(id, product.getId());
        assertEquals(name, product.getName());
        assertEquals(price, product.getPrice());
        assertEquals(ProductStatus.ACTIVE, product.getStatus());

        String newName = "Update Test Product";

        product.update(newName, null);

        assertEquals(newName, product.getName());
        assertEquals(price, product.getPrice());
        assertEquals(ProductStatus.ACTIVE, product.getStatus());
    }

    @Test
    void shouldUpdateProductPrice() {
        ProductId id = ProductId.generate();
        String name = "Test Product";
        Money price = new Money(50.0);

        Product product = Product.create(id, name, price);

        assertEquals(id, product.getId());
        assertEquals(name, product.getName());
        assertEquals(price, product.getPrice());
        assertEquals(ProductStatus.ACTIVE, product.getStatus());

        Money newPrice = new Money(100.0);

        product.update(null, newPrice);

        assertEquals(name, product.getName());
        assertEquals(newPrice, product.getPrice());
        assertEquals(ProductStatus.ACTIVE, product.getStatus());
    }
}