package com.devsuperior.dscommerce.services;

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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.dto.ProductMinDTO;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.exceptions.DatabaseException;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscommerce.tests.ProductFactory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	@InjectMocks
	private ProductService productService;

	@Mock
	private ProductRepository productRepository;

	private long existingProductId;
	private long nonExistingProductId;
	private long dependentProductId;

	private String productName;
	private Product product;

	private ProductDTO productDTO;

	private PageImpl<Product> pageProduct;

	@BeforeEach
	void setUp() throws Exception {

		existingProductId = 1L;
		nonExistingProductId = 2L;
		dependentProductId = 3L;

		productName = "PlayStation 5";
		product = ProductFactory.createProduct(productName);

		pageProduct = new PageImpl<>(List.of(product));

		productDTO = new ProductDTO(product);

		Mockito.when(productRepository.findById(existingProductId)).thenReturn(Optional.of(product));
		Mockito.when(productRepository.findById(nonExistingProductId)).thenReturn(Optional.empty());

		Mockito.when(productRepository.searchByName(ArgumentMatchers.any(), (Pageable)ArgumentMatchers.any())).thenReturn(pageProduct);

		Mockito.when(productRepository.save(ArgumentMatchers.any())).thenReturn(product);

		Mockito.when(productRepository.getReferenceById(existingProductId)).thenReturn(product);
		Mockito.when(productRepository.getReferenceById(nonExistingProductId)).thenThrow(EntityNotFoundException.class);


		Mockito.when(productRepository.existsById(existingProductId)).thenReturn(true);
		Mockito.when(productRepository.existsById(nonExistingProductId)).thenReturn(false);
		Mockito.when(productRepository.existsById(dependentProductId)).thenReturn(true);

		Mockito.doNothing().when(productRepository).deleteById(existingProductId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentProductId);
	}

	@Test
	public void findByIdDeveRetornarProductDTOquandoIdExists() {

		ProductDTO productDTOresult = productService.findById(existingProductId);

		Assertions.assertNotNull(productDTOresult);
		Assertions.assertEquals(productDTOresult.getId(), existingProductId);
		Assertions.assertEquals(productDTOresult.getName(), productName);
	}

	@Test
	public void findByIdDeveRetornarResourceNotFoundExceptionQuandoIdDoesNotExists() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {

			productService.findById(nonExistingProductId);
		});
	}

	@Test
	public void findAllDeveRetornarPagedProductMinDTO() {

		Pageable pageable = PageRequest.of(0, 12);

		Page<ProductMinDTO> productMinDTOresult = productService.findAll(productName, pageable);

		Assertions.assertNotNull(productMinDTOresult);
		Assertions.assertEquals(productMinDTOresult.getSize(), 1);
		Assertions.assertEquals(productMinDTOresult.iterator().next().getName(), productName);
	}

	@Test
	public void insertDeveRetornarProductDTO() {

		ProductDTO productDTOresult = productService.insert(productDTO);

		Assertions.assertNotNull(productDTOresult);
		Assertions.assertEquals(productDTOresult.getId(), product.getId());
	}

	@Test
	public void updateDeveRetornarProductDTOquandoIdExists() {

		ProductDTO productDTOresult = productService.update(existingProductId, productDTO);

		Assertions.assertNotNull(productDTOresult);
		Assertions.assertEquals(productDTOresult.getId(), existingProductId);
		Assertions.assertEquals(productDTOresult.getName(), productDTO.getName());
	}

	@Test
	public void updateDeveRetornarResourceNotFoundExceptionQuandoIdDoesNotExists() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {

			productService.update(nonExistingProductId, productDTO);
		});
	}

	@Test
	public void deleteDeveRetornarVazioQuandoIdExists() {

		Assertions.assertDoesNotThrow( () ->
			{
				productService.delete(existingProductId);
			}
		);
	}

	@Test
	public void deleteDeveRetornarResourceNotFoundExceptionQuandoIdDoesNotExists() {

		Assertions.assertThrows(ResourceNotFoundException.class, () ->
			{
				productService.delete(nonExistingProductId);
			}
		);
	}

	@Test
	public void deleteDeveRetornarDatabaseExceptionQuandoDependentProductId() {

		Assertions.assertThrows(DatabaseException.class, () ->
			{
				productService.delete(dependentProductId);
			}
		);
	}
}