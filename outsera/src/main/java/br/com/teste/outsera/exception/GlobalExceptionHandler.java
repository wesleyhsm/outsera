package br.com.teste.outsera.exception;

import br.com.teste.outsera.dto.ApiErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleFileNotFound(FileNotFoundException ex, HttpServletRequest request) {

        log.warn("Arquivo de dados não encontrado ao acessar {}: {}", request.getRequestURI(), ex.getMessage());

        ApiErrorResponseDTO error = new ApiErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Ficheiro Não Encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler({IOException.class, NumberFormatException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiErrorResponseDTO> handleFileProcessingError(Exception ex, HttpServletRequest request) {
        String message = ex instanceof NumberFormatException
                ? "Erro ao converter os dados numéricos do arquivo CSV (ex: ano inválido)."
                : "Falha na leitura ou processamento do arquivo de dados.";

        // Grava o stack trace completo no log do servidor para análise dos desenvolvedores
        log.warn("Falha de processamento de dados na rota [{}]. Erro original: ", request.getRequestURI(), ex);

        ApiErrorResponseDTO error = new ApiErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Erro de Processamento de Dados",
                message + " Detalhes: " + ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDTO> handleGenericException(Exception ex, HttpServletRequest request) {

        log.error("Exceção não tratada capturada na rota [{}]: ", request.getRequestURI(), ex);

        ApiErrorResponseDTO error = new ApiErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro Interno do Servidor",
                "Ocorreu um erro inesperado no sistema. Tente novamente mais tarde.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
