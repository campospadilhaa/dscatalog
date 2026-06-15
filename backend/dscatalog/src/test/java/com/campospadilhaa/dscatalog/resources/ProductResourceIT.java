package com.campospadilhaa.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.campospadilhaa.dscatalog.dto.ProductDTO;
import com.campospadilhaa.dscatalog.tests.Factory;
import com.campospadilhaa.dscatalog.tests.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // para executar um rollback depois de cada teste executado. Isto para que o resultado do teste não interfira no teste seguinte
public class ProductResourceIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TokenUtil tokenUtil;

	private Long existingId;
	private Long notExistingId;
	private Long countTotalProducts;

	private String username;
	private String password;
	private String bearerToken;

	@BeforeEach
	void setUp() throws Exception {

		existingId = 1L;
		notExistingId = 1000L;
		countTotalProducts = 25L;

		username = "maria@gmail.com";
		password = "123456";
		
		bearerToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
	}

	@Test
	public void findAllDeveRetornarSortedPageQuandoSortByName() throws Exception {

		ResultActions resultActions = mockMvc.perform(get("/products?page=0&size=5&sort=name,asc")
				.accept(MediaType.APPLICATION_JSON)); // tipo da resposta

		resultActions.andExpect(status().isOk());

		resultActions.andExpect(jsonPath("$.totalElements").value(countTotalProducts));

		resultActions.andExpect(jsonPath("$.content").exists());

		resultActions.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
		resultActions.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
		resultActions.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));
	}

	@Test
	public void updateDeveRetornarProductDTOQuandoIdExiste() throws Exception {

		ProductDTO productDTO = Factory.createdProductDTO();

		String expectedName = productDTO.getName();

		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions resultActions = mockMvc.perform(put("/products/{id}", existingId)
				.header("Authorization", "Bearer " + bearerToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON) // tipo da requisição
				.accept(MediaType.APPLICATION_JSON)); // tipo da resposta

		resultActions.andExpect(status().isOk());

		// verifica se os valores retornados depois do update se refere ao objeto atualizado
		resultActions.andExpect(jsonPath("$.id").value(existingId));
		resultActions.andExpect(jsonPath("$.name").value(expectedName));
	}

	@Test
	public void updateDeveLancarExcecaoThrowResourceNotFoundExceptionQuandoIdNaoExiste() throws Exception {

		ProductDTO productDTO = Factory.createdProductDTO();

		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions resultActions = mockMvc.perform(put("/products/{id}", notExistingId)
				.header("Authorization", "Bearer " + bearerToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON) // tipo da requisição
				.accept(MediaType.APPLICATION_JSON)); // tipo da resposta

		resultActions.andExpect(status().isNotFound());
	}
}