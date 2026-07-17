package com.devsuperior.examplemockspy.services;

import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.examplemockspy.dto.ProductDTO;
import com.devsuperior.examplemockspy.entities.Product;
import com.devsuperior.examplemockspy.repositories.ProductRepository;
import com.devsuperior.examplemockspy.services.exceptions.InvalidDataException;
import com.devsuperior.examplemockspy.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	@InjectMocks
	private ProductService productService;

	@Mock
	private ProductRepository productRepository;

	private Long existingId;
	private Long nonExistingId;

	private Product product;
	private ProductDTO productDTO;

	@BeforeEach
	void setUp() throws Exception {

		existingId = 1L;
		nonExistingId = 2L;

		product = new Product(1L, "Playstation", 2000.0);
		productDTO = new ProductDTO(product);

		Mockito.when(productRepository.save(any())).thenReturn(product);

		Mockito.when(productRepository.getReferenceById(existingId)).thenReturn(product);
		Mockito.when(productRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
	}

	@Test
	public void insertDeveRetornarProductDTOquandoDadosValidos() {

		// utiliza-se Mockito.spy para mokar o validateData()
		ProductService productServiceSpy = Mockito.spy(productService);

		Mockito.doNothing().when(productServiceSpy).validateData(productDTO);

		ProductDTO productDTOresult = productServiceSpy.insert(productDTO);

		Assertions.assertNotNull(productDTOresult);
		Assertions.assertEquals(productDTOresult.getName(), "Playstation");
	}

	@Test
	public void insertDeveRetornarInvalidDataExceptionQuandoProductNameIsBlank() {

		productDTO.setName("");

		// utiliza-se Mockito.spy para mokar o validateData()
		ProductService productServiceSpy = Mockito.spy(productService);

		Mockito.doThrow(InvalidDataException.class).when(productServiceSpy).validateData(productDTO);

		Assertions.assertThrows(InvalidDataException.class, () -> {

			@SuppressWarnings("unused")
			ProductDTO productDTOresult = productServiceSpy.insert(productDTO);
		});
	}

	@Test
	public void insertDeveRetornarInvalidDataExceptionQuandoExistsIdProductPriceNegativeOrZero() {

		productDTO.setPrice(-5.0);

		// utiliza-se Mockito.spy para mokar o validateData()
		ProductService productServiceSpy = Mockito.spy(productService);

		Mockito.doThrow(InvalidDataException.class).when(productServiceSpy).validateData(productDTO);

		Assertions.assertThrows(InvalidDataException.class, () -> {

			@SuppressWarnings("unused")
			ProductDTO productDTOresult = productServiceSpy.insert(productDTO);
		});
	}

	@Test
	public void updateDeveRetornarproductDTOquandoExistsIdAndDadosValidos() {
		
		// utiliza-se Mockito.spy para mokar o validateData()
		ProductService productServiceSpy = Mockito.spy(productService);

		Mockito.doNothing().when(productServiceSpy).validateData(productDTO);

		ProductDTO productDTOresult = productServiceSpy.update(existingId, productDTO);

		Assertions.assertNotNull(productDTOresult);
		Assertions.assertEquals(productDTOresult.getId(), existingId);
	}

	@Test
	public void updateDeveRetornarInvalidDataExceptionQuandoExistsIdAndProductNameIsBlank() {

		productDTO.setName("");

		// utiliza-se Mockito.spy para mokar o validateData()
		ProductService productServiceSpy = Mockito.spy(productService);

		Mockito.doThrow(InvalidDataException.class).when(productServiceSpy).validateData(productDTO);

		Assertions.assertThrows(InvalidDataException.class, () -> {

			@SuppressWarnings("unused")
			ProductDTO productDTOresult = productServiceSpy.update(existingId, productDTO);
		});
	}

	@Test
	public void updateDeveRetornarInvalidDataExceptionQuandoExistsIdProductPriceNegativeOrZero() {

		productDTO.setPrice(-5.0);

		// utiliza-se Mockito.spy para mokar o validateData()
		ProductService productServiceSpy = Mockito.spy(productService);

		Mockito.doThrow(InvalidDataException.class).when(productServiceSpy).validateData(productDTO);

		Assertions.assertThrows(InvalidDataException.class, () -> {

			@SuppressWarnings("unused")
			ProductDTO productDTOresult = productServiceSpy.update(existingId, productDTO);
		});
	}

	@Test
	public void updateDeveRetornarResourceNotFoundExceptionQuandoNonExistingIdAndDadosValidos() {

		// utiliza-se Mockito.spy para mokar o validateData()
		ProductService productServiceSpy = Mockito.spy(productService);

		Mockito.doNothing().when(productServiceSpy).validateData(productDTO);

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {

			@SuppressWarnings("unused")
			ProductDTO productDTOresult = productServiceSpy.update(nonExistingId, productDTO);
		});
	}

	@Test
	public void updateDeveRetornarInvalidDataExceptionQuandoNonExistsIdAndProductNameIsBlank() {

		productDTO.setName("");

		// utiliza-se Mockito.spy para mokar o validateData()
		ProductService productServiceSpy = Mockito.spy(productService);

		Mockito.doThrow(InvalidDataException.class).when(productServiceSpy).validateData(productDTO);

		Assertions.assertThrows(InvalidDataException.class, () -> {

			@SuppressWarnings("unused")
			ProductDTO productDTOresult = productServiceSpy.update(nonExistingId, productDTO);
		});
	}

	@Test
	public void updateDeveRetornarInvalidDataExceptionQuandoNonExistsIdProductPriceNegativeOrZero() {

		productDTO.setPrice(-5.0);

		// utiliza-se Mockito.spy para mokar o validateData()
		ProductService productServiceSpy = Mockito.spy(productService);

		Mockito.doThrow(InvalidDataException.class).when(productServiceSpy).validateData(productDTO);

		Assertions.assertThrows(InvalidDataException.class, () -> {

			@SuppressWarnings("unused")
			ProductDTO productDTOresult = productServiceSpy.update(nonExistingId, productDTO);
		});
	}
}