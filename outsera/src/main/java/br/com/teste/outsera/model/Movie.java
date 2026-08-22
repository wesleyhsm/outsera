package br.com.teste.outsera.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="MOVIE")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="releaseYear")
    private Integer releaseYear;

    @Column(name="title")
    private String title;

    @Column(name="studios")
    private String studios;

    @Column(name="producers")
    private String producers;

    @Column(name="winner")
    private Boolean winner;
}