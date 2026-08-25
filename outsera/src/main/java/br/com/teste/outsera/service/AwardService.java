package br.com.teste.outsera.service;

import br.com.teste.outsera.dto.ProducerAwardIntervalDTO;
import br.com.teste.outsera.dto.ProducerRangeResponseDTO;
import br.com.teste.outsera.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class AwardService {

    private final MovieRepository movieRepository;
    private static final Pattern SPLIT_PATTERN = Pattern.compile(",\\s*|\\s+and\\s+");

    public AwardService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public ProducerRangeResponseDTO getProducerIntervals() {
        List<Object[]> rawData = movieRepository.findEssentialWinnerData();
        if (rawData.isEmpty()) {
            return new ProducerRangeResponseDTO(List.of(), List.of());
        }

        Map<String, TreeSet<Integer>> producerWins = groupAndSortYears(rawData);

        List<ProducerAwardIntervalDTO> minList = new ArrayList<>();
        List<ProducerAwardIntervalDTO> maxList = new ArrayList<>();

        int[] globalBounds = { Integer.MAX_VALUE, Integer.MIN_VALUE };

        for (Map.Entry<String, TreeSet<Integer>> entry : producerWins.entrySet()) {
            TreeSet<Integer> years = entry.getValue();
            if (years.size() < 2) continue;

            computeExtremesInline(entry.getKey(), years, globalBounds, minList, maxList);
        }

        return new ProducerRangeResponseDTO(minList, maxList);
    }

    private Map<String, TreeSet<Integer>> groupAndSortYears(List<Object[]> rawData) {
        Map<String, TreeSet<Integer>> producerWins = new HashMap<>();

        for (Object[] row : rawData) {
            Integer year = (Integer) row[0];
            String producersStr = (String) row[1];

            if (producersStr == null) continue;

            String cleanProducers = producersStr.replace(", and", ",");
            String[] names = SPLIT_PATTERN.split(cleanProducers);

            for (String name : names) {
                String producerName = name.trim();
                if (!producerName.isEmpty()) {

                    producerWins.computeIfAbsent(producerName, k -> new TreeSet<>())
                            .add(year);
                }
            }
        }
        return producerWins;
    }

    private void computeExtremesInline(String producer, TreeSet<Integer> years, int[] bounds,
                                       List<ProducerAwardIntervalDTO> minList, List<ProducerAwardIntervalDTO> maxList) {

        Iterator<Integer> it = years.iterator();
        int prev = it.next();

        while (it.hasNext()) {
            int next = it.next();
            int interval = next - prev;
            ProducerAwardIntervalDTO dto = new ProducerAwardIntervalDTO(producer, interval, prev, next);

            if (interval < bounds[0]) {
                bounds[0] = interval;
                minList.clear();
                minList.add(dto);
            } else if (interval == bounds[0]) {
                minList.add(dto);
            }

            if (interval > bounds[1]) {
                bounds[1] = interval;
                maxList.clear();
                maxList.add(dto);
            } else if (interval == bounds[1]) {
                maxList.add(dto);
            }

            prev = next;
        }
    }
}
