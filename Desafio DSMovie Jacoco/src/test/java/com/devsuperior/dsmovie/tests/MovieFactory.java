package com.devsuperior.dsmovie.tests;

import com.devsuperior.dsmovie.dto.MovieGenreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;

public class MovieFactory {
	
	public static MovieEntity createMovieEntity() {
		MovieEntity movie = new MovieEntity(1L, "Test Movie", 0.0, 0, "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");
		return movie;
	}
	
	public static MovieGenreDTO createMovieDTO() {
		MovieEntity movie = createMovieEntity();
		return new MovieGenreDTO(movie);
	}
}