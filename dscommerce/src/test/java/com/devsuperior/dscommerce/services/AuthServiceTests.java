package com.devsuperior.dscommerce.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.services.exceptions.ForbiddenException;
import com.devsuperior.dscommerce.tests.UserFactory;

@ExtendWith(SpringExtension.class)
public class AuthServiceTests {

	@InjectMocks
	private AuthService authService;

	@Mock
	private UserService userService;

	private User userAdmin;
	private User userClientSelf;
	private User userClientOther;

	@BeforeEach
	void setUp() throws Exception {

		userAdmin = UserFactory.createUserAdmin();
		userClientSelf = UserFactory.createUserClient(1L, "Bob");
		userClientOther = UserFactory.createUserClient(2L, "Ana");
	}

	@Test
	public void validateSelfOrAdminRetornaNadaQuandoLogadoComAdmin() {

		Mockito.when(userService.authenticated()).thenReturn(userAdmin);

		Long userid = userAdmin.getId();

		Assertions.assertDoesNotThrow(() -> {
			authService.validateSelfOrAdmin(userid);
		});
	}

	@Test
	public void validateSelfOrAdminRetornaNadaQuandoLogadoComSelf() {

		Mockito.when(userService.authenticated()).thenReturn(userClientSelf);

		Long userid = userClientSelf.getId();

		Assertions.assertDoesNotThrow(() -> {
			authService.validateSelfOrAdmin(userid);
		});
	}

	@Test
	public void validateSelfOrAdminRetornaForbiddenExceptionQuandoLogadoOutroClient() {

		Mockito.when(userService.authenticated()).thenReturn(userClientSelf);

		// observe que se trata de outro Client: userClientSelf != userClientOther
		Long userid = userClientOther.getId();

		Assertions.assertThrows(ForbiddenException.class, () -> {
			authService.validateSelfOrAdmin(userid);
		});
	}
}