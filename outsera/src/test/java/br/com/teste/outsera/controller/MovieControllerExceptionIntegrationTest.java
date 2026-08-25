package br.com.teste.outsera.controller;

import br.com.teste.outsera.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
class MovieControllerExceptionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieRepository movieRepository;

    @Test
    void shouldReturn422WhenNumberFormatExceptionOccurs() throws Exception {

        Mockito.when(movieRepository.findEssentialWinnerData())
               .thenThrow(new NumberFormatException("For input string: \"ano_invalido\""));


        mockMvc.perform(get("/api/movies/producer-intervals")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.status", is(422)))
                .andExpect(jsonPath("$.error", is("Erro de Processamento de Dados")))
                .andExpect(jsonPath("$.message", containsString("Erro ao converter os dados numéricos do arquivo CSV")))
                .andExpect(jsonPath("$.message", containsString("For input string: \"ano_invalido\"")))
                .andExpect(jsonPath("$.path", is("/api/movies/producer-intervals")));
    }

    @Test
    void shouldReturn500WhenUnexpectedGenericExceptionOccurs() throws Exception {

        Mockito.when(movieRepository.findEssentialWinnerData())
               .thenThrow(new RuntimeException("Falha catastrófica interna no banco"));

        mockMvc.perform(get("/api/movies/producer-intervals")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.error", is("Erro Interno do Servidor")))
                .andExpect(jsonPath("$.message", is("Ocorreu um erro inesperado no sistema. Tente novamente mais tarde.")))
                .andExpect(jsonPath("$.path", is("/api/movies/producer-intervals")));
    }
}
