package br.com.teste.outsera.dto;

public record ProducerAwardIntervalDTO(
    String producer,
    Integer interval,
    Integer previousWin,
    Integer followingWin
) {}