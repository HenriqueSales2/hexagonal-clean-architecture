package br.com.sales.hexagonal_architecture_demo.application.port.input.command;

public class CreateProductCommand {

    private final String name;
    private final double price;

    public CreateProductCommand(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}