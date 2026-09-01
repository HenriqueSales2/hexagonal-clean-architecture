package br.com.sales.hexagonal_architecture_demo.application.port.input.command;

public record CreateProductCommand(String name, Double price) {}