package com.campospadilhaa.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.campospadilhaa.dscatalog.dto.ProductDTO;
import com.campospadilhaa.dscatalog.factory.Factory;
import com.campospadilhaa.dscatalog.services.ProductService;
import com.campospadilhaa.dscatalog.services.exceptions.DatabaseException;
import com.campospadilhaa.dscatalog.services.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;

// Carrega o contexto somente até a camada Web, não chegando a instanciar componentes; services...
@WebMvcTest(ProductResource.class)
public class ProductResourceTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ProductService productService;

	@Autowired
	private ObjectMapper objectMapper;

	private Long existingId;
	private Long notExistingId;
	private Long dependentId;

	private ProductDTO productDTO;
	private PageImpl<ProductDTO> page;

	@BeforeEach
	void setUp() throws Exception {

		existingId = 1L;
		notExistingId = 2L;
		dependentId = 3L;
	
		productDTO = Factory.createdProductDTO();
		page = new PageImpl<>(List.of(productDTO));

		// mockando o comportamento dos métodos contidos nos services e repositoires
		Mockito.when(productService.findAllPaged(ArgumentMatchers.any())).thenReturn(page);

		Mockito.when(productService.findById(existingId)).thenReturn(productDTO);
		Mockito.when(productService.findById(notExistingId)).thenThrow(ResourceNotFoundException.class);

		// informa any() para simular o comportamento de qualquer objeto
		Mockito.when(productService.update(Mockito.eq(existingId), Mockito.any())).thenReturn(productDTO);
		Mockito.when(productService.update(Mockito.eq(notExistingId), Mockito.any())).thenThrow(ResourceNotFoundException.class);

		// informa any() para simular o comportamento de qualquer objeto
		Mockito.when(productService.insert(Mockito.any())).thenReturn(productDTO);

		Mockito.doNothing().when(productService).delete(existingId);
		Mockito.doThrow(ResourceNotFoundException.class) .when(productService).delete(notExistingId);
		Mockito.doThrow(DatabaseException.class) .when(productService).delete(dependentId);
	}

	@Test
	public void findAllDeveRetornarPage() throws Exception {

		//mockMvc.perform(get("/products")).andExpect(status().isOk());

		ResultActions resultActions = mockMvc.perform(get("/products")
				.accept(MediaType.APPLICATION_JSON)); // tipo da resposta

		resultActions.andExpect(status().isOk());
	}

	@Test
	public void findByIdDeveRetornarProductQuandoIdExiste() throws Exception {

		ResultActions resultActions = mockMvc.perform(get("/products/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)); // tipo da resposta

		resultActions.andExpect(status().isOk());

		// verifica se no response existe o campo id, sinal que o dado foi encontrado
		resultActions.andExpect(jsonPath("$.id").exists());
		resultActions.andExpect(jsonPath("$.name").exists());
	}

	@Test
	public void findByIdDeveLancarExcecaoThrowResourceNotFoundExceptionQuandoIdNaoExiste() throws Exception {

		ResultActions resultActions = mockMvc.perform(get("/products/{id}", notExistingId)
				.accept(MediaType.APPLICATION_JSON)); // tipo da resposta

		resultActions.andExpect(status().isNotFound());

		// verifica se no response existe o campo id, sinal que o dado foi encontrado
		resultActions.andExpect(jsonPath("$.id").doesNotExist());
	}

	@Test
	public void updateDeveRetornarProductDTOQuandoIdExiste() throws Exception {

		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions resultActions = mockMvc.perform(put("/products/{id}", existingId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON) // tipo da requisição
				.accept(MediaType.APPLICATION_JSON)); // tipo da resposta

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(jsonPath("$.id").exists());
	}

	@Test
	public void updateDeveLancarExcecaoThrowResourceNotFoundExceptionQuandoIdNaoExiste() throws Exception {

		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions resultActions = mockMvc.perform(put("/products/{id}", notExistingId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON) // tipo da requisição
				.accept(MediaType.APPLICATION_JSON)); // tipo da resposta

		resultActions.andExpect(status().isNotFound());
	}

	@Test
	public void insertDeveRetornarProductDTOCreated() throws Exception {

		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions resultActions = mockMvc.perform(post("/products")
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON) // tipo da requisição
				.accept(MediaType.APPLICATION_JSON)); // tipo da resposta

		resultActions.andExpect(status().isCreated());
		resultActions.andExpect(jsonPath("$.id").exists());
	}

	@Test
	public void deleteDeveRetornarNoContentQuandoIdExiste() throws Exception {

		ResultActions resultActions = mockMvc.perform(delete("/products/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON)); // tipo da resposta

		resultActions.andExpect(status().isNoContent());
	}

	@Test
	public void deleteDeveLancarExcecaoThrowResourceNotFoundExceptionQuandoIdNaoExiste() throws Exception {

		ResultActions resultActions = mockMvc.perform(delete("/products/{id}", notExistingId)
				.accept(MediaType.APPLICATION_JSON)); // tipo da resposta

		resultActions.andExpect(status().isNotFound());
	}
}