package br.com.teste.outsera.controller;

import br.com.teste.outsera.dto.ProducerAwardIntervalDTO;
import br.com.teste.outsera.dto.ProducerRangeResponseDTO;
import br.com.teste.outsera.service.AwardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(MovieController.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AwardService awardService;

    @Test
    @DisplayName("Deve retornar status 200 e os intervalos de prêmios formatados em JSON")
    void shouldReturnProducerIntervalsWithSuccess() throws Exception {
        // Massa de dados mockada para o DTO
        ProducerAwardIntervalDTO minAward = new ProducerAwardIntervalDTO("Producer Min", 1, 2008, 2009);
        ProducerAwardIntervalDTO maxAward = new ProducerAwardIntervalDTO("Producer Max", 99, 1900, 1999);
        
        ProducerRangeResponseDTO mockResponse = new ProducerRangeResponseDTO(
            List.of(minAward),
            List.of(maxAward)
        );

        // Define o comportamento simulado do serviço
        when(awardService.getProducerIntervals()).thenReturn(mockResponse);

        // Executa a requisição HTTP fictícia e valida o retorno
        mockMvc.perform(get("/api/movies/producer-intervals")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.min").isArray())
                .andExpect(jsonPath("$.min[0].producer").value("Producer Min"))
                .andExpect(jsonPath("$.min[0].interval").value(1))
                .andExpect(jsonPath("$.min[0].previousWin").value(2008))
                .andExpect(jsonPath("$.min[0].followingWin").value(2009))
                .andExpect(jsonPath("$.max").isArray())
                .andExpect(jsonPath("$.max[0].producer").value("Producer Max"))
                .andExpect(jsonPath("$.max[0].interval").value(99))
                .andExpect(jsonPath("$.max[0].previousWin").value(1900))
                .andExpect(jsonPath("$.max[0].followingWin").value(1999));
    }
}