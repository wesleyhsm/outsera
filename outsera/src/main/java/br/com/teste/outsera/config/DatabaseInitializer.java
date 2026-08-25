package br.com.teste.outsera.config;

import br.com.teste.outsera.model.Movie;
import br.com.teste.outsera.repository.MovieRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final MovieRepository movieRepository;

    public DatabaseInitializer(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:*");

        Resource targetResource = null;
        String targetName = "movielist.csv";

        for (Resource resource : resources) {
            if (resource.getFilename() != null && resource.getFilename().equalsIgnoreCase(targetName)) {
                targetResource = resource;
                break;
            }
        }

        if (targetResource == null) {
            throw new java.io.FileNotFoundException("Arquivo " + targetName + " não foi encontrado no classpath.");
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(targetResource.getInputStream(), StandardCharsets.UTF_8))) {
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
}