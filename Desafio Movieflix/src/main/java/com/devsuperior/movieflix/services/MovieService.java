package com.devsuperior.movieflix.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.movieflix.dto.MovieCardDTO;
import com.devsuperior.movieflix.dto.MovieDetailsDTO;
import com.devsuperior.movieflix.entities.Movie;
import com.devsuperior.movieflix.projections.MovieCardProjection;
import com.devsuperior.movieflix.repositories.MovieRepository;
import com.devsuperior.movieflix.services.exceptions.ResourceNotFoundException;

@Service
public class MovieService {

	@Autowired
	private MovieRepository movieRepository;

	@Transactional(readOnly = true)
	public Page<MovieCardDTO> findAllPaged(String genreId, Pageable pageable) {

		if(genreId.equals("0")) {
			genreId = null;
		}

		// primeiro obtém os itens da lista paginada
		Page<MovieCardProjection> pageMovieCardProjection = movieRepository.searchMovies(genreId, pageable);

		// converte MovieCardProjection para MovieCardDTO
		List<MovieCardDTO> listaMovieCardDTO = pageMovieCardProjection.stream().map(movieCardProjection -> new MovieCardDTO(movieCardProjection)).toList();

		// convert 
		Page<MovieCardDTO> pageMovieCardDTO = new PageImpl<>(listaMovieCardDTO, pageMovieCardProjection.getPageable(), pageMovieCardProjection.getTotalElements());

		return pageMovieCardDTO;
	}

	public Movie findById(Long id) {

		Optional<Movie> optionalMovie =  movieRepository.findById(id);

		Movie movie =
				optionalMovie
					.orElseThrow( () -> new ResourceNotFoundException("Filme não encontrado") );

		return movie;
	}

	@Transactional(readOnly = true)
	public MovieDetailsDTO searchMovieWithReview(Long id) {

		Movie move = movieRepository.searchMovieWithReview(id);

		if(move==null){
			throw new ResourceNotFoundException("Filme não encontrado");
		}

		MovieDetailsDTO movieDetailsDTO = new MovieDetailsDTO(move);

		return movieDetailsDTO;
	}
}