package br.com.teste.outsera.config;

import br.com.teste.outsera.model.Movie;
import br.com.teste.outsera.repository.MovieRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DatabaseInitializerTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private DatabaseInitializer databaseInitializer;

    @Test
    @DisplayName("Deve ler o arquivo CSV de recursos e salvar os filmes corretamente no repositório")
    void shouldReadCsvFileAndSaveMoviesSuccessfully() throws Exception {
        // Executa o inicializador que lerá o arquivo 'movielist.csv' real do classpath de teste/main
        databaseInitializer.run();

        // Captura os objetos Movie enviados para o método save do repositório
        ArgumentCaptor<Movie> movieCaptor = ArgumentCaptor.forClass(Movie.class);
        verify(movieRepository, atLeastOnce()).save(movieCaptor.capture());

        // Valida se os dados foram processados e populados
        assertThat(movieCaptor.getAllValues()).isNotEmpty();

        // Obtém o primeiro filme processado para validar as propriedades estruturais do parser
        Movie firstMovie = movieCaptor.getAllValues().get(0);
        assertThat(firstMovie.getReleaseYear()).isNotNull();
        assertThat(firstMovie.getTitle()).isNotBlank();
        assertThat(firstMovie.getStudios()).isNotBlank();
        assertThat(firstMovie.getProducers()).isNotBlank();
        // O campo winner deve ser true ou false baseado na coluna correspondente
        assertThat(firstMovie.getWinner()).isNotNull();
    }
}