package com.devsuperior.dscommerce.services;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscommerce.dto.CategoryDTO;
import com.devsuperior.dscommerce.entities.Category;
import com.devsuperior.dscommerce.repositories.CategoryRepository;
import com.devsuperior.dscommerce.tests.CategoryFactory;

@ExtendWith(SpringExtension.class)
public class CategoryServiceTests {

	@InjectMocks
	private CategoryService categoryService;

	@Mock
	private CategoryRepository categoryRepository;

	private Category category;
	private List<Category> listaCategory;

	@BeforeEach
	void setUp() throws Exception {

		category = CategoryFactory.createCategory();

		listaCategory = new ArrayList<>();
			listaCategory.add(category);

		Mockito.when(categoryRepository.findAll()).thenReturn(listaCategory);
	}

	@Test
	public void findAllDeveRetornarListaCategoryDTO() {

		List<CategoryDTO> listaCategoryDTOresult = categoryService.findAll();

		Assertions.assertEquals(listaCategoryDTOresult.size(), 1);
		Assertions.assertEquals(listaCategoryDTOresult.get(0).getId(), category.getId());
		Assertions.assertEquals(listaCategoryDTOresult.get(0).getName(), category.getName());
	}
}