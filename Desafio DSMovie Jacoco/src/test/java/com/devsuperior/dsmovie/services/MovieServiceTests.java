package com.devsuperior.dsmovie.services;

import java.util.List;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.services.exceptions.DatabaseException;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTests {

	@InjectMocks
	private MovieService movieService;

	@Mock
	private MovieRepository movieRepository;

	private long existingMovieEntityId;
	private long nonExistingMovieEntityId;
	private long dependentMovieId;

	private MovieEntity movieEntity;

	private MovieDTO movieDTO;

	private PageImpl<MovieEntity> pageMovieEntity;

	@BeforeEach
	void setUp() throws Exception {

		existingMovieEntityId = 1L;
		nonExistingMovieEntityId = 2L;

		movieEntity = MovieFactory.createMovieEntity();

		movieDTO = new MovieDTO(movieEntity);

		pageMovieEntity = new PageImpl<>(List.of(movieEntity));
	}

	@Test
	public void findAllShouldReturnPagedMovieDTO() {

		Mockito.when(movieRepository.searchByTitle(ArgumentMatchers.anyString(), ArgumentMatchers.any(Pageable.class))).thenReturn(pageMovieEntity);

		Pageable pageable = PageRequest.of(0, 10);

		Page<MovieDTO> movieDTOResult = movieService.findAll(movieEntity.getTitle(), pageable);

		Assertions.assertNotNull(movieDTOResult);
		Assertions.assertEquals(1, movieDTOResult.getNumberOfElements());
		Assertions.assertEquals(movieEntity.getTitle(), movieDTOResult.iterator().next().getTitle());
	}

	@Test
	public void findByIdShouldReturnMovieDTOWhenIdExists() {

		Mockito.when(movieRepository.findById(existingMovieEntityId)).thenReturn(Optional.of(movieEntity));

		MovieDTO movieDTOResult = movieService.findById(existingMovieEntityId);

		Assertions.assertNotNull(movieDTOResult);
		Assertions.assertEquals(existingMovieEntityId, movieDTOResult.getId());
		Assertions.assertEquals(movieEntity.getTitle(), movieDTOResult.getTitle());
	}

	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

		Mockito.when(movieRepository.findById(nonExistingMovieEntityId)).thenReturn(Optional.empty());

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {

			movieService.findById(nonExistingMovieEntityId);
		});
	}

	@Test
	public void insertShouldReturnMovieDTO() {

		Mockito.when(movieRepository.save(ArgumentMatchers.any())).thenReturn(movieEntity);

		MovieDTO movieDTOResult = movieService.insert(movieDTO);

		Assertions.assertNotNull(movieDTOResult);
		Assertions.assertEquals(movieDTOResult.getId(), movieEntity.getId());
	}

	@Test
	public void updateShouldReturnMovieDTOWhenIdExists() {

		Mockito.when(movieRepository.getReferenceById(existingMovieEntityId)).thenReturn(movieEntity);
		Mockito.when(movieRepository.save(ArgumentMatchers.any())).thenReturn(movieEntity);

		MovieDTO movieDTOResult = movieService.update(existingMovieEntityId, movieDTO);

		Assertions.assertNotNull(movieDTOResult);
		Assertions.assertEquals(movieDTOResult.getId(), existingMovieEntityId);
		Assertions.assertEquals(movieDTOResult.getTitle(), movieDTO.getTitle());
	}

	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

		Mockito.when(movieRepository.getReferenceById(nonExistingMovieEntityId)).thenThrow(EntityNotFoundException.class);

		Assertions.assertThrows(ResourceNotFoundException.class, () ->
			{

			movieService.update(nonExistingMovieEntityId, movieDTO);
			}
		);
	}

	@Test
	public void deleteShouldDoNothingWhenIdExists() {

		Mockito.when(movieRepository.existsById(existingMovieEntityId)).thenReturn(true);

		Assertions.assertDoesNotThrow( () ->
			{
				movieService.delete(existingMovieEntityId);
			}
		);
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

		Mockito.when(movieRepository.existsById(nonExistingMovieEntityId)).thenReturn(false);

		Assertions.assertThrows(ResourceNotFoundException.class, () ->
			{
				movieService.delete(nonExistingMovieEntityId);
			}
		);
	}

	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {

		Mockito.when(movieRepository.existsById(dependentMovieId)).thenReturn(true);

		Mockito.doThrow(DataIntegrityViolationException.class).when(movieRepository).deleteById(dependentMovieId);

		Assertions.assertThrows(DatabaseException.class, () ->
			{
				movieService.delete(dependentMovieId);
			}
		);
	}
}