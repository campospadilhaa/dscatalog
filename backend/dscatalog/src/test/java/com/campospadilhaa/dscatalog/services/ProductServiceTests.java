package com.campospadilhaa.dscatalog.services;

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

import com.campospadilhaa.dscatalog.dto.ProductDTO;
import com.campospadilhaa.dscatalog.entities.Category;
import com.campospadilhaa.dscatalog.entities.Product;
import com.campospadilhaa.dscatalog.factory.Factory;
import com.campospadilhaa.dscatalog.repositories.CategoryRepository;
import com.campospadilhaa.dscatalog.repositories.ProductRepository;
import com.campospadilhaa.dscatalog.services.exceptions.DatabaseException;
import com.campospadilhaa.dscatalog.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

// Não carrega o contexto, mas permite usar os recursos do Spring com JUnit (teste de unidade: service/component)
@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	// não coloca a ingestão do @Autowired, para não utilizar o Service real. Utiliza @InjectMocks para simular o Service
	@InjectMocks
	private ProductService productService;

	// não coloca a ingestão do @Autowired, para não utilizar o Service real. Utiliza @InjectMocks para simular o Service
	// diferente dos testes de Repository, o teste não vai ao banco de dados
	@Mock
	private ProductRepository productRepository;
	@Mock
	private CategoryRepository categoryRepository;

	private Product product;
	private Category category;
	private ProductDTO productDTO;
	private PageImpl<Product> pageProduct;

	private long existingId;
	private long notExistingId;
	private long dependentId;

	@BeforeEach
	void setUp() throws Exception {

		// valores mockados para simular o comportamento do teste
		product = Factory.createdProduct();
		category = Factory.createCategory();
		productDTO = Factory.createdProductDTO();
		pageProduct = new PageImpl<>(List.of(product));

		existingId = 1L;
		notExistingId = 2L;
		dependentId = 3L;

		// quando o retorno do método é um void, vem primeiro a sintaxe when... depois Mockito.do...
		// configurado os mock's relacionado com findAll
		Mockito.when(productRepository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(pageProduct);
		// configurado os mock's relacionado com save
		Mockito.when(productRepository.save(ArgumentMatchers.any())).thenReturn(product);
		// configurado os mock's relacionado com findById
		Mockito.when(productRepository.findById(existingId)).thenReturn(Optional.of(product));
		Mockito.when(productRepository.findById(notExistingId)).thenReturn(Optional.empty());
		// configurado os mock's relacionado com update
		Mockito.when(productRepository.getReferenceById(existingId)).thenReturn(product);
		Mockito.when(productRepository.getReferenceById(notExistingId)).thenThrow(EntityNotFoundException.class);

		Mockito.when(categoryRepository.getReferenceById(existingId)).thenReturn(category);
		Mockito.when(categoryRepository.getReferenceById(notExistingId)).thenThrow(EntityNotFoundException.class);

		// quando o retorno do método é um void, vem primeiro a sintaxe Mockito.do... depois when...
		// configurando o objeto productRepository para simular a execução da classe
		// relacionado com o método: deveDeletarRetornandoVazioQuandoIdExiste e deveLancarExcecaoThrowDatabaseExceptionQuandoDependentId, que executa a deleção
		Mockito.doNothing().when(productRepository).deleteById(existingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentId);

		/* utilizado somente para Spring Boot 2. Depois desta versão o erro (excecao) não mais ocorre
		 * mesmo o regitro não existindo erros não são retornados 
		Mockito.doThrow(EmptyResultDataAccessException.class).when(productRepository).deleteById(notExistingId); */

		// configurados os mock's referentes ao método existsById executados pelo productService.delete
		// relacionado com o méotod: deveLancarExcecaoThrowResourceNotFoundExceptionQuandoIdNaoExiste, que lança a exceção
		Mockito.when(productRepository.existsById(existingId)).thenReturn(true);
		Mockito.when(productRepository.existsById(notExistingId)).thenReturn(false);
		Mockito.when(productRepository.existsById(dependentId)).thenReturn(true);
		////
	}

	@Test
	public void findAllDeveRetornarUmaPagina(){

		Pageable pageable = PageRequest.of(0, 10);

		Page<ProductDTO> pageProductDTO = productService.findAllPaged(pageable);

		Assertions.assertNotNull(pageProductDTO);
		Mockito.verify(productRepository, Mockito.times(1)).findAll(pageable);
	}

	@Test
	public void deveRetornarProductDTOAtravesDoFindByIdQuandoIdExiste() {

		ProductDTO productDTO = productService.findById(existingId);

		Assertions.assertNotNull(productDTO);
	}

	@Test
	public void deveLancarExcecaoThrowResourceNotFoundExceptionParaProductDTOAtravesDoFindByIdQuandoIdNaoExiste() {

		Assertions.assertThrows(ResourceNotFoundException.class, () ->
			{
				productService.findById(notExistingId);
			}
		);
	}

	@Test
	public void updateDeveRetornarProductDTOAtualizadoQuandoIdExiste() {

		productDTO = productService.update(existingId, productDTO);

		Assertions.assertNotNull(productDTO);
	}

	@Test
	public void updateDeveLancarExcecaoThrowResourceNotFoundExceptionQuandoIdNaoExiste() {

		Assertions.assertThrows(ResourceNotFoundException.class, () ->
			{
				productService.update(notExistingId, productDTO);
			}
		);
	}
	
	@Test
	public void deveDeletarRetornandoVazioQuandoIdExiste() {

		Assertions.assertDoesNotThrow( () ->
			{
				productService.delete(existingId);
			}
		);

		Mockito.verify(productRepository, Mockito.times(1)).deleteById(existingId);
	}

	@Test
	public void deveLancarExcecaoThrowResourceNotFoundExceptionQuandoIdNaoExiste() {

		Assertions.assertThrows(ResourceNotFoundException.class, () ->
			{
				productService.delete(notExistingId);
			}
		);

		/* utilizado somente para Spring Boot 2. Depois desta versão o erro (excecao) não mais ocorre
		 * mesmo o regitro não existindo erros não são retornados 

		// Mockito.times(1): significa a quantidade de vezes que o teste é executado
		Mockito.verify(productRepository, Mockito.times(1)).deleteById(notExistingId);*/
	}

	@Test
	public void deveLancarExcecaoThrowDatabaseExceptionQuandoDependentId() {

		Assertions.assertThrows(DatabaseException.class, () ->
			{
				productService.delete(dependentId);
			}
		);
	}
}