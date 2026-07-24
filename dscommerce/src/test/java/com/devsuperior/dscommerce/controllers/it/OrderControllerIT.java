package com.devsuperior.dscommerce.controllers.it;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscommerce.tests.TokenUtil;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrderControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TokenUtil tokenUtil;

	private String adminToken;
	private String clientMariaToken;
	private String invalidToken;

	private String adminUsername;
	private String adminPassword;
	private String clientUsername;
	private String clientPassword;

	private Long clientMariaOrderId;
	private Long clientAlexOrderId;
	private Long notExistOrderId;

	@BeforeEach
	void setUp() throws Exception {

		clientMariaOrderId = 1L;
		clientAlexOrderId = 2L;
		notExistOrderId = 100L;

		adminUsername = "alex@gmail.com";
		adminPassword = "123456";

		clientUsername = "maria@gmail.com";
		clientPassword = "123456";

		adminToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
		clientMariaToken = tokenUtil.obtainAccessToken(mockMvc, clientUsername, clientPassword);
		invalidToken = "teste";
	}

	@Test
	public void findByIdDeveRetornarOrderDTOQuandoIdExistsLogadoComAdmin() throws Exception {

		ResultActions result = mockMvc
				.perform(get("/orders/{id}", clientMariaOrderId)
						.header("Authorization", "Bearer " + adminToken)
						.accept(MediaType.APPLICATION_JSON)
						)

						// atributo para debug (retorno da requisição realizada)
						.andDo(MockMvcResultHandlers.print());

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").value(clientMariaOrderId));
		result.andExpect(jsonPath("$.moment").value("2022-07-25T13:00:00Z"));
		result.andExpect(jsonPath("$.status").value("PAID"));
		result.andExpect(jsonPath("$.client").exists());
		result.andExpect(jsonPath("$.client.name").value("Maria Brown"));
		result.andExpect(jsonPath("$.payment").exists());
		result.andExpect(jsonPath("$.items").exists());
		result.andExpect(jsonPath("$.items[0].name").value("The Lord of the Rings"));
		result.andExpect(jsonPath("$.total").exists());
	}

	@Test
	public void findByIdDeveRetornarOrderDTOQuandoPedidoPertenceAoUsuario() throws Exception {

		ResultActions result = mockMvc
				.perform(get("/orders/{id}", clientMariaOrderId)
						.header("Authorization", "Bearer " + clientMariaToken)
						.accept(MediaType.APPLICATION_JSON)
						)

						// atributo para debug (retorno da requisição realizada)
						.andDo(MockMvcResultHandlers.print());

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").value(clientMariaOrderId));
		result.andExpect(jsonPath("$.moment").value("2022-07-25T13:00:00Z"));
		result.andExpect(jsonPath("$.status").value("PAID"));
		result.andExpect(jsonPath("$.client").exists());
		result.andExpect(jsonPath("$.client.name").value("Maria Brown"));
		result.andExpect(jsonPath("$.payment").exists());
		result.andExpect(jsonPath("$.items").exists());
		result.andExpect(jsonPath("$.items[0].name").value("The Lord of the Rings"));
		result.andExpect(jsonPath("$.total").exists());
	}

	@Test
	public void findByIdDeveRetornarForbiddenQuandoPedidoPertenceAOutroUsuario() throws Exception {

		ResultActions result = mockMvc
				.perform(get("/orders/{id}", clientAlexOrderId)
						.header("Authorization", "Bearer " + clientMariaToken)
						.accept(MediaType.APPLICATION_JSON)
						)

						// atributo para debug (retorno da requisição realizada)
						.andDo(MockMvcResultHandlers.print());

		result.andExpect(status().isForbidden());
	}

	@Test
	public void findByIdDeveRetornarNotFoundQuandoPedidoInexistenteLogadoComAdmin() throws Exception {

		ResultActions result = mockMvc
				.perform(get("/orders/{id}", notExistOrderId)
						.header("Authorization", "Bearer " + adminToken)
						.accept(MediaType.APPLICATION_JSON)
						)

						// atributo para debug (retorno da requisição realizada)
						.andDo(MockMvcResultHandlers.print());

		result.andExpect(status().isNotFound());
	}

	@Test
	public void findByIdDeveRetornarNotFoundQuandoPedidoInexistenteLogadoComClient() throws Exception {

		ResultActions result = mockMvc
				.perform(get("/orders/{id}", notExistOrderId)
						.header("Authorization", "Bearer " + clientMariaToken)
						.accept(MediaType.APPLICATION_JSON)
						)

						// atributo para debug (retorno da requisição realizada)
						.andDo(MockMvcResultHandlers.print());

		result.andExpect(status().isNotFound());
	}

	@Test
	public void findByIdDeveRetornarUnauthorizedQuandoInvalidToken() throws Exception {

		ResultActions result = mockMvc
				.perform(get("/orders/{id}", clientMariaOrderId)
						.header("Authorization", "Bearer " + invalidToken)
						.accept(MediaType.APPLICATION_JSON)
						)

						// atributo para debug (retorno da requisição realizada)
						.andDo(MockMvcResultHandlers.print());

		result.andExpect(status().isUnauthorized());
	}
}