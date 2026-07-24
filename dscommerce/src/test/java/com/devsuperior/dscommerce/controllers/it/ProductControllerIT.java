package com.devsuperior.dscommerce.controllers.it;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.entities.Category;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.tests.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TokenUtil tokenUtil;

	@Autowired
	private ObjectMapper objectMapper;

	private String adminToken;
	private String clientToken;
	private String invalidToken;

	private String adminUsername;
	private String adminPassword;
	private String clientUsername;
	private String clientPassword;

	private Long existingProductId;
	private Long nonExistingProductId;
	private Long dependentProductId;

	private String productName;

	private Product product;
	private ProductDTO productDTO;

	@BeforeEach
	void setUp() throws Exception {

		existingProductId = 2L;
		nonExistingProductId = 100L;
		dependentProductId = 3L;

		productName = "Macbook";

		adminUsername = "alex@gmail.com";
		adminPassword = "123456";

		clientUsername = "maria@gmail.com";
		clientPassword = "123456";

		adminToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
		clientToken = tokenUtil.obtainAccessToken(mockMvc, clientUsername, clientPassword);
		invalidToken = "aabbccddeeffgg";// simulação de token inválido

		product = new Product(null, "PlayStation 5", "Lorem ipsum, dolor sit amet consectetur adipisicing elit.", 3999.90, "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg");
			Category category = new Category(2L, null);
			product.getCategories().add(category);
		productDTO = new ProductDTO(product);
	}

	@Test
	public void findAllDeveRetornarPageQuandoNameParamIsEmpty() throws Exception {

		ResultActions result = mockMvc
				.perform(get("/products").accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.content[0].id").value(1L));
		result.andExpect(jsonPath("$.content[0].name").value("The Lord of the Rings"));
		result.andExpect(jsonPath("$.content[0].price").value(90.5));
		result.andExpect(jsonPath("$.content[0].imgUrl").value("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg"));
	}

	@Test
	public void findAllDeveRetornarPageQuandoNameParamIsNotEmpty() throws Exception {

		ResultActions result = mockMvc
				.perform(get("/products?name={productName}", productName)
						.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.content[0].id").value(3L));
		result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
		result.andExpect(jsonPath("$.content[0].price").value(1250.0));
		result.andExpect(jsonPath("$.content[0].imgUrl").value("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/3-big.jpg"));
	}

	@Test
	public void insertDeveRetornarProductDTOCreatedQuandoDadosValidosLogadoComAdmin() throws Exception {

		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result = mockMvc
				.perform(post("/products")
						.header("Authorization", "Bearer " + adminToken)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						)

						// atributo para debug (retorno da requisição realizada)
						.andDo(MockMvcResultHandlers.print());

		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.id").value(26L)); // o banco de ddaos H2 é inicializado com 25 itens, então o teste verifica se o item 26 foi corretamente inserido
		result.andExpect(jsonPath("$.name").value("PlayStation 5"));
		result.andExpect(jsonPath("$.description").value("Lorem ipsum, dolor sit amet consectetur adipisicing elit."));
		result.andExpect(jsonPath("$.price").value(3999.90));
		result.andExpect(jsonPath("$.imgUrl").value("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg"));
		result.andExpect(jsonPath("$.categories[0].id").value(2L));
	}

	@Test
	public void insertDeveRetornarUnprocessabledEntityQuandoLogadoComAdminAndInvalidName() throws Exception {

		product.setName("ab");
		productDTO = new ProductDTO(product);

		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result = mockMvc
				.perform(post("/products")
						.header("Authorization", "Bearer " + adminToken)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						)

						// atributo para debug (retorno da requisição realizada)
						.andDo(MockMvcResultHandlers.print());

		result.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void insertDeveRetornarUnprocessabledEntityQuandoLogadoComAdminAndInvalidDescription() throws Exception {

		product.setDescription("ab");
		productDTO = new ProductDTO(product);

		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result = mockMvc
				.perform(post("/products")
						.header("Authorization", "Bearer " + adminToken)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						)

						// atributo para debug (retorno da requisição realizada)
						.andDo(MockMvcResultHandlers.print());

		result.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void insertDeveRetornarUnprocessabledEntityQuandoLogadoComAdminAndInvalidPriceIsNegative() throws Exception {

		product.setPrice(-50.0);
		productDTO = new ProductDTO(product);

		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result = mockMvc
				.perform(post("/products")
						.header("Authorization", "Bearer " + adminToken)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						)

						// atributo para debug (retorno da requisição realizada)
						.andDo(MockMvcResultHandlers.print());

		result.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void insertDeveRetornarUnprocessabledEntityQuandoLogadoComAdminAndInvalidPriceIsZero() throws Exception {

		product.setPrice(0.0);
		productDTO = new ProductDTO(product);

		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result = mockMvc
				.perform(post("/products")
						.header("Authorization", "Bearer " + adminToken)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						)

						// atributo para debug (retorno da requisição realizada)
						.andDo(MockMvcResultHandlers.print());

		result.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void insertDeveRetornarUnprocessabledEntityQuandoLogadoComAdminAndProductHasNotCategory() throws Exception {

		product.getCategories().clear();;
		productDTO = new ProductDTO(product);

		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result = mockMvc
				.perform(post("/products")
						.header("Authorization", "Bearer " + adminToken)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						)

						// atributo para debug (retorno da requisição realizada)
						.andDo(MockMvcResultHandlers.print());

		result.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void insertDeveRetornarForbiddenQuandoLogadoComClient() throws Exception {

		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result = mockMvc
				.perform(post("/products")
						.header("Authorization", "Bearer " + clientToken)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						)

						// atributo para debug (retorno da requisição realizada)
						.andDo(MockMvcResultHandlers.print());

		result.andExpect(status().isForbidden());
	}

	@Test
	public void insertDeveRetornarUnauthorizedQuandoInvalidToken() throws Exception {

		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result = mockMvc
				.perform(post("/products")
						.header("Authorization", "Bearer " + invalidToken)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						)

						// atributo para debug (retorno da requisição realizada)
						.andDo(MockMvcResultHandlers.print());

		result.andExpect(status().isUnauthorized());
	}

	@Test
	public void deleteDeveRetornarNoContentQuandoDadosLogadoComAdmin() throws Exception {

		ResultActions result = mockMvc
				.perform(delete("/products/{id}", existingProductId)
						.header("Authorization", "Bearer " + adminToken)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						)

						// atributo para debug (retorno da requisição realizada)
						.andDo(MockMvcResultHandlers.print());

		result.andExpect(status().isNoContent());
	}

	@Test
	public void deleteDeveRetornarResourceNotFoundExceptionQuandoDadosLogadoComAdminAndProductNotExists() throws Exception {

		ResultActions result = mockMvc
				.perform(delete("/products/{id}", nonExistingProductId)
						.header("Authorization", "Bearer " + adminToken)
						.accept(MediaType.APPLICATION_JSON)
						)

						// atributo para debug (retorno da requisição realizada)
						.andDo(MockMvcResultHandlers.print());

		result.andExpect(status().isNotFound());
	}

	@Test
	@Transactional(propagation = Propagation.SUPPORTS) 
	public void deleteDeveRetornarBadRequestQuandoDadosLogadoComAdminAndProductDepend() throws Exception {

		ResultActions result = mockMvc
				.perform(delete("/products/{id}", dependentProductId)
						.header("Authorization", "Bearer " + adminToken)
						.accept(MediaType.APPLICATION_JSON)
						)

						// atributo para debug (retorno da requisição realizada)
						.andDo(MockMvcResultHandlers.print());

		result.andExpect(status().isBadRequest());
	}

	@Test
	public void deleteDeveRetornarForbiddenQuandoLogadoComClient() throws Exception {

		ResultActions result = mockMvc
				.perform(delete("/products/{id}", existingProductId)
						.header("Authorization", "Bearer " + clientToken)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						)

						// atributo para debug (retorno da requisição realizada)
						.andDo(MockMvcResultHandlers.print());

		result.andExpect(status().isForbidden());
	}

	@Test
	public void deleteDeveRetornarUnauthorizedQuandoInvalidToken() throws Exception {

		ResultActions result = mockMvc
				.perform(delete("/products/{id}", existingProductId)
						.header("Authorization", "Bearer " + invalidToken)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						)

						// atributo para debug (retorno da requisição realizada)
						.andDo(MockMvcResultHandlers.print());

		result.andExpect(status().isUnauthorized());
	}
}