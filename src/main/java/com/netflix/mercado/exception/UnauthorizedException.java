package com.netflix.mercado.exception;

/**
 * Exceção lançada quando há erro de autorização (acesso não permitido).
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
