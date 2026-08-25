package br.com.teste.outsera.dto;

import java.time.LocalDateTime;

public record ApiErrorResponseDTO(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path
) {}
