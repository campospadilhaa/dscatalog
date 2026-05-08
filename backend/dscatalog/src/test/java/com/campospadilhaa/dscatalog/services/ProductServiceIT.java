package com.campospadilhaa.dscatalog.services;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.campospadilhaa.dscatalog.dto.ProductDTO;
import com.campospadilhaa.dscatalog.repositories.ProductRepository;
import com.campospadilhaa.dscatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
@Transactional // para executar um rollback depois de cada teste executado. Isto para que o resultado do teste não interfira no teste seguinte
public class ProductServiceIT {

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductRepository productRepository;

	private Long existingId;
	private Long notExistingId;
	private Long countTotalProducts;

	@BeforeEach
	void setUp() throws Exception {

		existingId = 1L;
		notExistingId = 1000L;
		countTotalProducts = 25L;
	}

	@Test
	public void deleteDeveDeletarRecursoQuandoIdExiste() {

		productService.delete(existingId);

		Assertions.assertEquals(countTotalProducts - 1, productRepository.count());
	}

	@Test
	public void deleteDeveLancarExcecaoThrowResourceNotFoundExceptionQuandoIdNaoExiste() {

		Assertions.assertThrows(ResourceNotFoundException.class, () ->
			{
				productService.delete(notExistingId);
			}
		);
	}

	@Test
	public void findAllPagedDeveRetornarPageQuandoPage0Size10() {

		PageRequest pageRequest = PageRequest.of(0, 10);

		Page<ProductDTO> pageProduct =  productService.findAllPaged(pageRequest);

		Assertions.assertFalse(pageProduct.isEmpty());
		Assertions.assertEquals(0, pageProduct.getNumber());
		Assertions.assertEquals(10, pageProduct.getSize());
		Assertions.assertEquals(countTotalProducts, pageProduct.getTotalElements());
	}

	@Test
	public void findAllPagedDeveRetornarVazioQuandoPageNaoExiste() {

		PageRequest pageRequest = PageRequest.of(50, 10);

		Page<ProductDTO> pageProduct =  productService.findAllPaged(pageRequest);

		Assertions.assertTrue(pageProduct.isEmpty());
	}

	@Test
	public void findAllPagedDeveRetornarSortedPageQuandoSortByName() {

		PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));

		Page<ProductDTO> pageProduct =  productService.findAllPaged(pageRequest);

		Assertions.assertFalse(pageProduct.isEmpty());

		// para testar a ordenação serão verificados os 3 (três) primeiros objetos da lista retornada
		Assertions.assertEquals("Macbook Pro", pageProduct.getContent().get(0).getName());
		Assertions.assertEquals("PC Gamer", pageProduct.getContent().get(1).getName());
		Assertions.assertEquals("PC Gamer Alfa", pageProduct.getContent().get(2).getName());
	}
}