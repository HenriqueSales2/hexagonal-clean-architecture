package br.com.sales.hexagonal_architecture_demo.infrastructure.input.request;

public class UpdateProductRequest {

    private String name;
    private Double price;

    public UpdateProductRequest() {}

    public UpdateProductRequest(String name, Double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }
}