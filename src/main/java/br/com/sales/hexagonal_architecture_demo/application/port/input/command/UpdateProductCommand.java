package br.com.sales.hexagonal_architecture_demo.application.port.input.command;

public class UpdateProductCommand {

    private final String name;
    private final double price;

    public UpdateProductCommand(String name, double price) {
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