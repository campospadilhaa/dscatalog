package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

import io.restassured.http.ContentType;

public class MovieControllerRA {

	private String adminUsername;
	private String adminPassword;
	private String clientUsername;
	private String clientPassword;

	private String clientToken;
	private String adminToken;
	private String invalidToken;

	private Long existingMovieId;
	private Long nonExistingMovieId;

	private String movieTitle;

	private Map<String, Object> postMovieInstance;

	@BeforeEach
	public void setUp() throws JSONException {

		baseURI = "http://localhost:8080";

		adminUsername = "maria@gmail.com";
		adminPassword = "123456";
		clientUsername = "alex@gmail.com";
		clientPassword = "123456";

		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
		adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);

		existingMovieId = 1L;
		nonExistingMovieId = 100L;

		movieTitle = "Vingadores";

		postMovieInstance = new HashMap<>();
		postMovieInstance.put("title", "Test Movie");
		postMovieInstance.put("score", 0.0);
		postMovieInstance.put("count", 0);
		postMovieInstance.put("image", "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");;
	}

	@Test
	public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {

		given()
			.get("/movies")
		.then()
			.statusCode(200)
			.body("content[0].id", is(1))
			.body("content[0].title", equalTo("The Witcher"))
			.body("content[0].image", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg"))
			.body("content[0].score", is(4.5F));
	}

	@Test
	public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {		

		given()
			.get("/movies?title={title}", movieTitle)
		.then()
			.statusCode(200)
			.body("content[0].id", is(13))
			.body("content[0].title", equalTo("Vingadores: Ultimato"))
			.body("content[0].image", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/7RyHsO4yDXtBv1zUU3mTpHeQ0d5.jpg"))
			.body("content[0].score", is(0F));
	}

	@Test
	public void findByIdShouldReturnMovieWhenIdExists() {		

		given()
			.get("/movies/{id}", existingMovieId)
		.then()
		.statusCode(200)
			.body("id", is(1))
			.body("title", equalTo("The Witcher"))
			.body("image", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg"))
			.body("score", is(4.5F));
	}

	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {	

		given()
			.get("/movies/{id}", nonExistingMovieId)
		.then()
			.statusCode(404);
	}

	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws JSONException {		

		postMovieInstance.put("title", "");
		JSONObject newProduct = new JSONObject(postMovieInstance);

		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + adminToken)
			.body(newProduct.toString())
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post("/movies")
		.then()
			.statusCode(422);
	}

	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {

		JSONObject newProduct = new JSONObject(postMovieInstance);

		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + clientToken)
			.body(newProduct.toString())
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post("/movies")
		.then()
			.statusCode(403);
	}

	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {

		JSONObject newProduct = new JSONObject(postMovieInstance);

		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + invalidToken)
			.body(newProduct.toString())
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post("/movies")
		.then()
			.statusCode(401);
	}
}