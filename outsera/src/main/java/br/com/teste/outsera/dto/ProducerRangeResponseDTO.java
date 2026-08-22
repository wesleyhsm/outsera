package br.com.teste.outsera.dto;

import java.util.List;

public record ProducerRangeResponseDTO(
    List<ProducerAwardIntervalDTO> min,
    List<ProducerAwardIntervalDTO> max
) {}