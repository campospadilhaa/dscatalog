package com.campospadilhaa.dscatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campospadilhaa.dscatalog.dto.CategoryDTO;
import com.campospadilhaa.dscatalog.entities.Category;
import com.campospadilhaa.dscatalog.repositories.CategoryRepository;
import com.campospadilhaa.dscatalog.services.exceptions.EntityNotFoundException;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;

	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll(){

		List<Category> listCategory = repository.findAll();

		List<CategoryDTO> listCategoryDTO =
				listCategory
					.stream()
					.map(category -> new CategoryDTO(category))
					.collect(Collectors.toList());

		return listCategoryDTO;
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id){

		Optional<Category> optCategory = repository.findById(id);

		// Category category = optCategory.get();

		Category category =
				optCategory
					.orElseThrow( () -> new EntityNotFoundException("Categoria não encontrada") );

		CategoryDTO categoryDTO = new CategoryDTO(category);

		return categoryDTO;
	}
}