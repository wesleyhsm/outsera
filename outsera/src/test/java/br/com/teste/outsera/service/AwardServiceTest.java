package br.com.teste.outsera.service;

import br.com.teste.outsera.dto.ProducerAwardIntervalDTO;
import br.com.teste.outsera.dto.ProducerRangeResponseDTO;
import br.com.teste.outsera.model.Movie;
import br.com.teste.outsera.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AwardServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private AwardService awardService;

    private Movie createMovie(Integer year, String title, String producers) {
        Movie movie = new Movie();
        movie.setReleaseYear(year);
        movie.setTitle(title);
        movie.setProducers(producers);
        movie.setWinner(true);
        return movie;
    }

    @Test
    @DisplayName("Deve calcular corretamente os intervalos mínimo e máximo de prêmios dos produtores")
    void shouldCalculateCorrectMinAndMaxIntervals() {
        // Dados de teste mockados
        // Producer 1 ganha em 1980 e 1981 (intervalo = 1) -> Esperado no MIN
        // Producer 2 ganha em 2000 e 2022 (intervalo = 22) -> Esperado no MAX
        // Testando também a separação complexa por vírgulas e a palavra 'and'
        List<Movie> mockMovies = List.of(
            createMovie(1980, "Filme A", "Producer 1"),
            createMovie(1981, "Filme B", "Producer 1, and Producer 3"),
            createMovie(2000, "Filme C", "Producer 2"),
            createMovie(2022, "Filme D", "Producer 2 and Producer 4")
        );

        when(movieRepository.findByWinnerTrue()).thenReturn(mockMovies);

        // Execução do método testado
        ProducerRangeResponseDTO response = awardService.getProducerIntervals();

        // Validações da lista MIN
        assertThat(response.min()).hasSize(1);
        ProducerAwardIntervalDTO minResult = response.min().get(0);
        assertThat(minResult.producer()).isEqualTo("Producer 1");
        assertThat(minResult.interval()).isEqualTo(1);
        assertThat(minResult.previousWin()).isEqualTo(1980);
        assertThat(minResult.followingWin()).isEqualTo(1981);

        // Validações da lista MAX
        assertThat(response.max()).hasSize(1);
        ProducerAwardIntervalDTO maxResult = response.max().get(0);
        assertThat(maxResult.producer()).isEqualTo("Producer 2");
        assertThat(maxResult.interval()).isEqualTo(22);
        assertThat(maxResult.previousWin()).isEqualTo(2000);
        assertThat(maxResult.followingWin()).isEqualTo(2022);
    }

    @Test
    @DisplayName("Deve retornar listas vazias quando nenhum produtor tiver mais de um prêmio")
    void shouldReturnEmptyListsWhenNoProducerHasMultipleWins() {
        // Cada produtor ganhou apenas uma vez
        List<Movie> mockMovies = List.of(
            createMovie(1990, "Filme X", "Producer A"),
            createMovie(1995, "Filme Y", "Producer B")
        );

        when(movieRepository.findByWinnerTrue()).thenReturn(mockMovies);

        ProducerRangeResponseDTO response = awardService.getProducerIntervals();

        // Garante que não quebra o fluxo e retorna coleções vazias
        assertThat(response.min()).isEmpty();
        assertThat(response.max()).isEmpty();
    }
}