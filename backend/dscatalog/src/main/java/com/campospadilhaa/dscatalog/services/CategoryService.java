package com.campospadilhaa.dscatalog.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.campospadilhaa.dscatalog.dto.CategoryDTO;
import com.campospadilhaa.dscatalog.entities.Category;
import com.campospadilhaa.dscatalog.repositories.CategoryRepository;
import com.campospadilhaa.dscatalog.services.exceptions.DatabaseException;
import com.campospadilhaa.dscatalog.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	/*
	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll(){

		List<Category> listCategory = categoryRepository.findAll();

		List<CategoryDTO> listCategoryDTO =
				listCategory
					.stream()
					.map(category -> new CategoryDTO(category))
					.collect(Collectors.toList());

		return listCategoryDTO;
	}*/

	public Page<CategoryDTO> findAllPaged(PageRequest pageRequest){

		Page<Category> listCategory = categoryRepository.findAll(pageRequest);

		Page<CategoryDTO> listCategoryDTO =
				listCategory
					.map(category -> new CategoryDTO(category));

		return listCategoryDTO;
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id){

		Optional<Category> optCategory = categoryRepository.findById(id);

		// Category category = optCategory.get();

		Category category =
				optCategory
					.orElseThrow( () -> new ResourceNotFoundException("Categoria não encontrada") );

		CategoryDTO categoryDTO = new CategoryDTO(category);

		return categoryDTO;
	}

	@Transactional
	public CategoryDTO insert(CategoryDTO categoryDTO) {

		Category category = new Category();
		category.setName(categoryDTO.getName());

		category = categoryRepository.save(category);

		return new CategoryDTO(category);
	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO categoryDTO) {

		try {

			// instancia um objeto sem ir ao banco de dados
			Category category = categoryRepository.getReferenceById(id);
			category.setName(categoryDTO.getName());

			category = categoryRepository.save(category);

			return new CategoryDTO(category);

		} catch (EntityNotFoundException e) {

			throw new ResourceNotFoundException("Categoria não encontrada: " + id);
		}
	}

	/* substituído pelo método abaixo com o acréscimo do controle "existsById"
	@Transactional(propagation = Propagation.SUPPORTS)
	public void delete(Long id) {
	    try {
	    	categoryRepository.deleteById(id);
	    }
	    catch (EmptyResultDataAccessException e) {
	        throw new ResourceNotFoundException("Categoria não encontrada");
	    }
	    catch (DataIntegrityViolationException e) {
	        throw new DatabaseException("Falha ao excluir a categoria, existem registros relacionados");
	    }
	}*/

	@Transactional(propagation = Propagation.SUPPORTS)
	public void delete(Long id) {

		if (!categoryRepository.existsById(id)) {
			throw new ResourceNotFoundException("Categoria não encontrada");
		}

		try {
			categoryRepository.deleteById(id);    		
		}
    	catch (DataIntegrityViolationException e) {
        	throw new DatabaseException("Falha ao excluir a categoria, existem registros relacionados");
	   	}
	}
}