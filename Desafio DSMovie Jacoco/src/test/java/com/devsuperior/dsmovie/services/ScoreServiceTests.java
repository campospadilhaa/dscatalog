package com.devsuperior.dsmovie.services;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.devsuperior.dsmovie.dto.MovieGenreDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;

@ExtendWith(MockitoExtension.class)
public class ScoreServiceTests {

	@InjectMocks
	private ScoreService scoreService;

	@Mock
	private ScoreRepository scoreRepository;

	@Mock
	private UserService userService;

	@Mock
	private MovieRepository movieRepository;

	private ScoreEntity scoreEntity;
	private ScoreDTO scoreDTO;

	private UserEntity userEntity;

	private long existingMovieEntityId;
	private long nonExistingMovieEntityId;

	@BeforeEach
	void setUp() throws Exception {

		scoreEntity = ScoreFactory.createScoreEntity();
		scoreDTO = ScoreFactory.createScoreDTO();

		userEntity = UserFactory.createUserEntity();

		existingMovieEntityId = 1L;
		nonExistingMovieEntityId = 2L;
	}

	@Test
	public void saveScoreShouldReturnMovieDTO() {

		MovieEntity movieEntity = MovieFactory.createMovieEntity();

		Mockito.when(userService.authenticated()).thenReturn(userEntity);
		Mockito.when(movieRepository.findById(existingMovieEntityId)).thenReturn(Optional.of(movieEntity));
		Mockito.when(scoreRepository.saveAndFlush(ArgumentMatchers.any())).thenReturn(scoreEntity);

		movieEntity.getScores().add(scoreEntity);

		Mockito.when(movieRepository.save(ArgumentMatchers.any())).thenReturn(movieEntity);

		MovieGenreDTO movieDTOResult = scoreService.saveScore(scoreDTO);

		Assertions.assertNotNull(movieDTOResult);
	}

	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {

		Mockito.when(userService.authenticated()).thenReturn(userEntity);

		Mockito.when(movieRepository.findById(nonExistingMovieEntityId)).thenReturn(Optional.empty());

		ScoreDTO scoreDTO = new ScoreDTO(nonExistingMovieEntityId, 5D);

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			@SuppressWarnings("unused")
			MovieGenreDTO movieDTOResult = scoreService.saveScore(scoreDTO);
		});		
	}
}