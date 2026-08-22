package br.com.teste.outsera.service;

import br.com.teste.outsera.dto.ProducerAwardIntervalDTO;
import br.com.teste.outsera.dto.ProducerRangeResponseDTO;
import br.com.teste.outsera.model.Movie;
import br.com.teste.outsera.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AwardService {

    private final MovieRepository movieRepository;

    public AwardService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public ProducerRangeResponseDTO getProducerIntervals() {
        List<Movie> winningMovies = movieRepository.findByWinnerTrue();
        Map<String, List<Integer>> producerWins = new HashMap<>();

        Pattern splitPattern = Pattern.compile(",\\s*|\\s+and\\s+");

        for (Movie movie : winningMovies) {
            String cleanProducers = movie.getProducers().replace(", and", ",");
            String[] names = splitPattern.split(cleanProducers);

            for (String name : names) {
                String producerName = name.trim();
                if (!producerName.isEmpty()) {
                    producerWins.computeIfAbsent(producerName, k -> new ArrayList<>()).add(movie.getReleaseYear());
                }
            }
        }

        List<ProducerAwardIntervalDTO> intervals = new ArrayList<>();

        for (Map.Entry<String, List<Integer>> entry : producerWins.entrySet()) {
            List<Integer> years = entry.getValue();
            if (years.size() < 2) continue;
            Collections.sort(years);

            for (int i = 0; i < years.size() - 1; i++) {
                int prev = years.get(i);
                int next = years.get(i + 1);
                int interval = next - prev;
                intervals.add(new ProducerAwardIntervalDTO(entry.getKey(), interval, prev, next));
            }
        }

        if (intervals.isEmpty()) {
            return new ProducerRangeResponseDTO(List.of(), List.of());
        }

        int minInterval = intervals.stream().mapToInt(ProducerAwardIntervalDTO::interval).min().orElse(Integer.MAX_VALUE);
        int maxInterval = intervals.stream().mapToInt(ProducerAwardIntervalDTO::interval).max().orElse(Integer.MIN_VALUE);

        List<ProducerAwardIntervalDTO> minList = intervals.stream()
                .filter(i -> i.interval() == minInterval)
                .collect(Collectors.toList());

        List<ProducerAwardIntervalDTO> maxList = intervals.stream()
                .filter(i -> i.interval() == maxInterval)
                .collect(Collectors.toList());

        return new ProducerRangeResponseDTO(minList, maxList);
    }
}