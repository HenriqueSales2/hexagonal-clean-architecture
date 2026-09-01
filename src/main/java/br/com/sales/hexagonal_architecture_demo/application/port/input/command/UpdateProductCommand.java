package br.com.sales.hexagonal_architecture_demo.application.port.input.command;

public record UpdateProductCommand(String name, Double price) {}