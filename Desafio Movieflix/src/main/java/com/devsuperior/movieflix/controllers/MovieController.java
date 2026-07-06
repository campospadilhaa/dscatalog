package com.devsuperior.movieflix.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devsuperior.movieflix.dto.MovieCardDTO;
import com.devsuperior.movieflix.dto.MovieDetailsDTO;
import com.devsuperior.movieflix.dto.ReviewDetailsDTO;
import com.devsuperior.movieflix.services.MovieService;
import com.devsuperior.movieflix.services.ReviewService;

@RestController
@RequestMapping(value = "/movies")
public class MovieController {

	@Autowired
	private MovieService movieService;

	@Autowired
	ReviewService reviewService;

	@GetMapping
	@PreAuthorize("hasAnyRole('ROLE_VISITOR', 'ROLE_MEMBER')")
	public ResponseEntity<Page<MovieCardDTO>> findAllPaged(
			@RequestParam(name = "genreId", defaultValue = "0") String genreId,
			Pageable pageable){

		Page<MovieCardDTO> list = movieService.findAllPaged(genreId, pageable);

		return ResponseEntity.ok().body(list);
	}

	@GetMapping(value = "/{id}")
	@PreAuthorize("hasAnyRole('ROLE_VISITOR', 'ROLE_MEMBER')")
	public ResponseEntity<MovieDetailsDTO> findById(@PathVariable Long id){

		MovieDetailsDTO movieDetailsDTO = movieService.searchMovieWithReview(id);

		return ResponseEntity.ok().body(movieDetailsDTO);
	}

	@GetMapping(value = "/{id}/reviews")
	@PreAuthorize("hasAnyRole('ROLE_VISITOR', 'ROLE_MEMBER')")
	public ResponseEntity<List<ReviewDetailsDTO>> findReviewsById(@PathVariable Long id){

		List<ReviewDetailsDTO> listaReviewDetailsDTO = reviewService.searchReviews(id);

		return ResponseEntity.ok().body(listaReviewDetailsDTO);
	}	
}