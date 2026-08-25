package br.com.teste.outsera.service;

import br.com.teste.outsera.dto.ProducerAwardIntervalDTO;
import br.com.teste.outsera.dto.ProducerRangeResponseDTO;
import br.com.teste.outsera.model.Movie;
import br.com.teste.outsera.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AwardServiceIntegrationTest {

    @Autowired
    private AwardService awardService;

    @Autowired
    private MovieRepository movieRepository;

    @BeforeEach
    void setUp() throws Exception {
        movieRepository.deleteAll();

        ClassPathResource resource = new ClassPathResource("movielist.csv");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            br.readLine();
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
    void shouldCalculateMinAndMaxProducerIntervalsCorrectly() {
        ProducerRangeResponseDTO response = awardService.getProducerIntervals();

        assertNotNull(response);
        assertNotNull(response.min());
        assertNotNull(response.max());

        List<ProducerAwardIntervalDTO> minList = response.min();
        assertFalse(minList.isEmpty(), "A lista de intervalo mínimo não deveria estar vazia");
        
        ProducerAwardIntervalDTO minAward = minList.get(0);
        assertEquals("Joel Silver", minAward.producer());
        assertEquals(1, minAward.interval());
        assertEquals(1990, minAward.previousWin());
        assertEquals(1991, minAward.followingWin());

        List<ProducerAwardIntervalDTO> maxList = response.max();
        assertFalse(maxList.isEmpty(), "A lista de intervalo máximo não deveria estar vazia");
        
        ProducerAwardIntervalDTO maxAward = maxList.get(0);
        assertEquals("Matthew Vaughn", maxAward.producer());
        assertEquals(13, maxAward.interval());
        assertEquals(2002, maxAward.previousWin());
        assertEquals(2015, maxAward.followingWin());
    }

    @Test
    void shouldReturnEmptyListsWhenNoWinnersExist() {

        movieRepository.deleteAll();
        
        Movie movie = new Movie();
        movie.setReleaseYear(2020);
        movie.setTitle("Normal Movie");
        movie.setProducers("Some Producer");
        movie.setWinner(false);
        movieRepository.save(movie);

        ProducerRangeResponseDTO response = awardService.getProducerIntervals();

        assertTrue(response.min().isEmpty());
        assertTrue(response.max().isEmpty());
    }
}
