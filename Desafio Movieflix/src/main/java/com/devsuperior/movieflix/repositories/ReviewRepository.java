package com.devsuperior.movieflix.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.devsuperior.movieflix.entities.Review;
import com.devsuperior.movieflix.projections.ReviewProjection;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	@Query(nativeQuery = true, value = """
			SELECT tb_review.id, tb_review.text, tb_user.id as user_id, tb_user.name FROM tb_review
			INNER JOIN tb_user on tb_user.id = tb_review.user_id
			WHERE tb_review.movie_id = :movieId
			""")
	List<ReviewProjection>searchReviews(Long movieId);
}