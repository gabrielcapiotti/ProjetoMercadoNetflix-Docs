package com.netflix.mercado.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import com.netflix.mercado.dto.common.ErrorResponse;
import com.netflix.mercado.dto.common.ValidationErrorResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Tratador global de exceções para a aplicação.
 * Centraliza o tratamento de erros em toda a API.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = Logger.getLogger(GlobalExceptionHandler.class.getName());

    /**
     * Trata exceções de recurso não encontrado.
     *
     * @param ex exceção lançada
     * @param request requisição web
     * @return resposta de erro com status 404
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {

        log.warning("Recurso não encontrado: " + ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .mensagem(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .codigo("RECURSO_NAO_ENCONTRADO")
                .detalhes("")
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Trata exceções de validação.
     *
     * @param ex exceção lançada
     * @param request requisição web
     * @return resposta de erro com status 400
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
            ValidationException ex, WebRequest request) {

        log.warning("Erro de validação: " + ex.getMessage());

        ValidationErrorResponse errorResponse = ValidationErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .mensagem(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .erros(new ArrayList<>())
                .codigo("VALIDATION_ERROR")
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Trata exceções de acesso não autorizado.
     *
     * @param ex exceção lançada
     * @param request requisição web
     * @return resposta de erro com status 401
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            UnauthorizedException ex, WebRequest request) {

        log.warning("Acesso não autorizado: " + ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .mensagem(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .codigo("ACESSO_NEGADO")
                .detalhes("")
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Trata exceções de validação de argumentos não válidos.
     *
     * @param ex exceção lançada
     * @param request requisição web
     * @return resposta de erro com status 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, WebRequest request) {

        log.warning("Erro na validação dos argumentos da requisição");

        ValidationErrorResponse errorResponse = ValidationErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .mensagem("Erro na validação dos campos")
                .timestamp(LocalDateTime.now())
                .erros(new ArrayList<>())
                .codigo("VALIDATION_ERROR")
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Trata exceções genéricas não tratadas especificamente.
     *
     * @param ex exceção lançada
     * @param request requisição web
     * @return resposta de erro com status 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {

        log.severe("Erro interno do servidor: " + ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .mensagem("Erro interno do servidor. Contacte o suporte.")
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .codigo("INTERNAL_SERVER_ERROR")
                .detalhes("")
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
