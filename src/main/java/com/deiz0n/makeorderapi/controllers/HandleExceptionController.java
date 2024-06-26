package com.deiz0n.makeorderapi.controllers;

import com.deiz0n.makeorderapi.domain.exceptions.*;
import com.deiz0n.makeorderapi.domain.utils.responses.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;

@ControllerAdvice
public class HandleExceptionController extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException exception, HttpServletRequest request) {
        var error = new ErrorResponse(
                Instant.now(),
                "Recurso não encontrado",
                exception.getMessage(),
                HttpStatus.NOT_FOUND,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler({ResourceExistingException.class})
    public ResponseEntity<ErrorResponse> handleResourceExistingExcepion(ResourceExistingException exception, HttpServletRequest request) {
        var error = new ErrorResponse(
                Instant.now(),
                "Recurdo existente",
                exception.getMessage(),
                HttpStatus.CONFLICT,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler({DataIntegrityException.class})
    public ResponseEntity<ErrorResponse> handleDataIntegrityException(DataIntegrityException exception, HttpServletRequest request) {
        var error = new ErrorResponse(
                Instant.now(),
                "Recurdo em uso",
                exception.getMessage(),
                HttpStatus.CONFLICT,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler({ResourceIsEmptyException.class})
    public ResponseEntity<ErrorResponse> handleResourceIsEmptyException(ResourceIsEmptyException exception, HttpServletRequest request) {
        var error = new ErrorResponse(
                Instant.now(),
                "Campo vazio",
                exception.getMessage(),
                HttpStatus.BAD_REQUEST,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        var error = new ErrorResponse(
                Instant.now(),
                "Campo inválido",
                ex.getFieldError().getDefaultMessage(),
                HttpStatus.BAD_REQUEST,
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        var error = new ErrorResponse(
                Instant.now(),
                "Formato inválido",
                "O JSON informado possui formato inválido",
                HttpStatus.BAD_REQUEST,
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        var error = new ErrorResponse(
                Instant.now(),
                "Não encontrado",
                "Recurso inexistente",
                HttpStatus.NOT_FOUND,
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        var error = new ErrorResponse(
                Instant.now(),
                "Recurso não aceito",
                "Os formatos aceitos são: [application/json, application/*+json].",
                HttpStatus.valueOf(status.value()),
                request.getDescription(false)
        );
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(error);
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<?> handlerAuthenticationException() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @ExceptionHandler({InsufficientAuthenticationException.class})
    public ResponseEntity<ErrorResponse> handleInsufficientAuthenticationException(HttpServletRequest request) {
        var error = new ErrorResponse(
                Instant.now(),
                "Token inválido",
                "Token inválido, expirado ou nulo",
                HttpStatus.UNAUTHORIZED,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler({BadCredentialsException.class, InternalAuthenticationServiceException.class})
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(HttpServletRequest request) {
        var error = new ErrorResponse(
                Instant.now(),
                "Credenciais inválidas",
                "Email ou senha inválido(a)",
                HttpStatus.UNAUTHORIZED,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(HttpServletRequest request) {
        var error = new ErrorResponse(
                Instant.now(),
                "Acesso negado",
                "O funcionário não tem permissão para acessar tal recurso",
                HttpStatus.FORBIDDEN,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler({GenerateCodeException.class})
    public ResponseEntity<ErrorResponse> handleGenerateCodeException(GenerateCodeException exception, HttpServletRequest request) {
        var error = new ErrorResponse(
                Instant.now(),
                "Erro no servidor interno",
                exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler({SendEmailException.class})
    public ResponseEntity<ErrorResponse> handleSendEmailException(SendEmailException exception, HttpServletRequest request) {
        var error = new ErrorResponse(
                Instant.now(),
                "Erro no servidor interno",
                exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler({PasswordNotEqualsException.class})
    public ResponseEntity<ErrorResponse> handlePasswordNotEqualsException(PasswordNotEqualsException exception, HttpServletRequest request) {
        var error = new ErrorResponse(
                Instant.now(),
                "Erro ao alterar a senha",
                exception.getMessage(),
                HttpStatus.UNAUTHORIZED,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
}
