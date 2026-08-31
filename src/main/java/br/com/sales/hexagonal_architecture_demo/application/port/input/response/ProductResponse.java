package br.com.sales.hexagonal_architecture_demo.application.port.input.response;

import java.math.BigDecimal;

public class ProductResponse {

    private final String id;
    private final String name;
    private final BigDecimal price;
    private final String status;

    private ProductResponse(ProductResponseBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.price = builder.price;
        this.status = builder.status;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    public static ProductResponseBuilder builder() {
        return new ProductResponseBuilder();
    }

    public static class ProductResponseBuilder {
        private String id;
        private String name;
        private BigDecimal price;
        private String status;

        public ProductResponseBuilder() {}

        public ProductResponseBuilder id(String id) {
            this.id = id;
            return this;
        }

        public ProductResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ProductResponseBuilder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public ProductResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public ProductResponse build() {
            return new ProductResponse(this);
        }
    }
}
