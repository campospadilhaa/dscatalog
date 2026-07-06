package com.devsuperior.movieflix.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.movieflix.dto.ReviewDTO;
import com.devsuperior.movieflix.dto.ReviewDetailsDTO;
import com.devsuperior.movieflix.entities.Movie;
import com.devsuperior.movieflix.entities.Review;
import com.devsuperior.movieflix.entities.User;
import com.devsuperior.movieflix.projections.ReviewProjection;
import com.devsuperior.movieflix.repositories.ReviewRepository;
import com.devsuperior.movieflix.services.exceptions.ResourceNotFoundException;

@Service
public class ReviewService {

	@Autowired
	private ReviewRepository reviewRepository;

	@Autowired
	private MovieService movieService;

	@Autowired
	AuthService authService;

	@Transactional
	public ReviewDTO insert(ReviewDTO reviewDTO) {

		Movie movie = movieService.findById(reviewDTO.getMovieId());

		if(movie==null) {
			throw new ResourceNotFoundException("Filme não encontrado");
		}

		User user = authService.authenticated();

		Review review = new Review();
		review.setMovie(movie);
		review.setText(reviewDTO.getText());
		review.setUser(user);

		review = reviewRepository.save(review);

		return new ReviewDTO(review);
	}

	@Transactional(readOnly = true)
	public List<ReviewDetailsDTO> searchReviews(Long id){

		List<ReviewProjection> listaReviewProjection = reviewRepository.searchReviews(id);

		// converte listaReviewProjection para listaReviewDetailsDTO
		List<ReviewDetailsDTO> listaReviewDetailsDTO = listaReviewProjection
				.stream()
					.map(reviewProjection -> new ReviewDetailsDTO(reviewProjection.getId(), reviewProjection.getText(), reviewProjection.getName())).toList();

		return listaReviewDetailsDTO;
	}
}