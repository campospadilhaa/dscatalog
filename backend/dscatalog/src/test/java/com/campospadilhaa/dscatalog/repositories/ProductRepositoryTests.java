package com.campospadilhaa.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.campospadilhaa.dscatalog.entities.Product;
import com.campospadilhaa.dscatalog.factory.Factory;

// Carrega somente os componentes relacionados ao Spring Data JPA. Cada teste é transacional e dá rollback ao final. (teste de unidade: repository)
@DataJpaTest
public class ProductRepositoryTests {

	@Autowired
	private ProductRepository productRepository;

	private long idexistente = 1L;
	private long nonExistingId = 1000L;
	private long countTotalProducts;

	@BeforeEach
	void setUp() throws Exception {

		idexistente = 1L;
		nonExistingId = 1000L;
		countTotalProducts = 25L;
	}

	@Test
	public void deveDeletarQuandoIdExiste() {

		//long idexistente = 1L;

		productRepository.deleteById(idexistente);

		Optional<Product> optionalProduct = productRepository.findById(1L);

		Assertions.assertFalse( optionalProduct.isPresent() );
	}

	/* utilizado somente para Spring Boot 2. Depois desta versão o erro (excecao) não mais ocorre
	 * mesmo o regitro não existindo erros não são retornados 
	@Test
	public void lancarExcecaoThrowEmptyResultDataAccessExceptionQuandoIdNaoExiste() {

		//long nonExistingId = 1000L;

		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			productRepository.deleteById(nonExistingId);			
		});
	}/**/

	@Test
	public void deveSalvarPersistirAutoincrementQuandoIdNulo() {

		Product product = Factory.createdProduct();

		// atribuiçõa de null para criar um novo registro
		product.setId(null);

		product = productRepository.save(product);

		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(countTotalProducts + 1, product.getId());
	}

	@Test
	public void deveRetornarProductQuandoIdExiste() {

		//long idexistente = 1L;

		Optional<Product> optionalProduct = productRepository.findById(idexistente);

		Assertions.assertTrue( optionalProduct.isPresent() );
	}

	@Test
	public void deveRetornarVazioProductQuandoIdExiste() {

		//long nonExistingId = 1000L;

		Optional<Product> optionalProduct = productRepository.findById(nonExistingId);

		Assertions.assertTrue( optionalProduct.isEmpty() );
	}
}