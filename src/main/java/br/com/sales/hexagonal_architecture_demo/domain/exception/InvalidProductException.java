package br.com.sales.hexagonal_architecture_demo.domain.exception;

public class InvalidProductException extends RuntimeException {

    public InvalidProductException(String message) {
        super(message);
    }
}
