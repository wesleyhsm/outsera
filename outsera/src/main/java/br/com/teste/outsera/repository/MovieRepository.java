package br.com.teste.outsera.repository;

import br.com.teste.outsera.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("SELECT m.releaseYear, m.producers FROM Movie m WHERE m.winner = true AND m.producers IS NOT NULL")
    List<Object[]> findEssentialWinnerData();
}