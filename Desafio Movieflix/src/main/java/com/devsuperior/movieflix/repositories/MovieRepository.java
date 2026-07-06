package com.devsuperior.movieflix.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.devsuperior.movieflix.entities.Movie;
import com.devsuperior.movieflix.projections.MovieCardProjection;

public interface MovieRepository extends JpaRepository<Movie, Long> {

	@Query(nativeQuery = true, value = """
			SELECT tb_movie.id, tb_movie.title, tb_movie.sub_title as subTitle, tb_movie.movie_year as "year", tb_movie.img_url as imgUrl FROM tb_movie
			WHERE (:genreId is null OR tb_movie.genre_id = :genreId)
			ORDER BY tb_movie.title
			""",
			countQuery = """
			SELECT COUNT(*) FROM tb_movie
			WHERE (:genreId is null OR tb_movie.genre_id = :genreId)
			""")
	Page<MovieCardProjection>searchMovies(String genreId, Pageable peageble);

	@Query("SELECT movie FROM Movie movie " +
		   "JOIN FETCH movie.genre " +
		   "LEFT JOIN FETCH movie.reviews review " +
		   "LEFT JOIN FETCH review.user " +
		   "WHERE movie.id = :movieId")
	Movie searchMovieWithReview(Long movieId);
}