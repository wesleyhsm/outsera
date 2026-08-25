package br.com.teste.outsera.controller;

import br.com.teste.outsera.model.Movie;
import br.com.teste.outsera.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
class MovieControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieRepository movieRepository;

    @BeforeEach
    void setUp() throws Exception {
        movieRepository.deleteAll();

        ClassPathResource resource = new ClassPathResource("movielist.csv");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            br.readLine(); // Pula o cabeçalho
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(";");
                if (columns.length >= 4) {
                    Movie movie = new Movie();
                    movie.setReleaseYear(Integer.parseInt(columns[0].trim()));
                    movie.setTitle(columns[1].trim());
                    movie.setStudios(columns[2].trim());
                    movie.setProducers(columns[3].trim());
                    movie.setWinner(columns.length > 4 && "yes".equalsIgnoreCase(columns[4].trim()));
                    movieRepository.save(movie);
                }
            }
        }
    }

    @Test
    void shouldReturnProducerIntervalsWithStatus200() throws Exception {
        mockMvc.perform(get("/api/movies/producer-intervals")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.min", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.min[0].producer", is("Joel Silver")))
                .andExpect(jsonPath("$.min[0].interval", is(1)))
                .andExpect(jsonPath("$.min[0].previousWin", is(1990)))
                .andExpect(jsonPath("$.min[0].followingWin", is(1991)))
                .andExpect(jsonPath("$.max", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.max[0].producer", is("Matthew Vaughn")))
                .andExpect(jsonPath("$.max[0].interval", is(13)))
                .andExpect(jsonPath("$.max[0].previousWin", is(2002)))
                .andExpect(jsonPath("$.max[0].followingWin", is(2015)));
    }

    @Test
    void shouldReturnEmptyListsWhenNoDataAvailable() throws Exception {
        movieRepository.deleteAll();

        mockMvc.perform(get("/api/movies/producer-intervals")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.min", hasSize(0)))
                .andExpect(jsonPath("$.max", hasSize(0)));
    }
}
