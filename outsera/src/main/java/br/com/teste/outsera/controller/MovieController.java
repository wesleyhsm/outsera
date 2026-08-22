package br.com.teste.outsera.controller;

import br.com.teste.outsera.dto.ProducerRangeResponseDTO;
import br.com.teste.outsera.service.AwardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final AwardService awardService;

    public MovieController(AwardService awardService) {
        this.awardService = awardService;
    }

    @GetMapping("/producer-intervals")
    public ResponseEntity<ProducerRangeResponseDTO> getProducerIntervals() {
        return ResponseEntity.ok(awardService.getProducerIntervals());
    }
}