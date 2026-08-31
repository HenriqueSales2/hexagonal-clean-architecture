package br.com.sales.hexagonal_architecture_demo.application.port.output;

import br.com.sales.hexagonal_architecture_demo.domain.model.Product;
import br.com.sales.hexagonal_architecture_demo.domain.vo.ProductId;

public interface ProductEventPublisher {

    void publishProductCreated(Product product);
    void publishProductUpdated(Product product);
    void publishProductDeleted(ProductId id);
    void publishProductActivated(Product product);
    void publishProductDeactivated(Product product);
}