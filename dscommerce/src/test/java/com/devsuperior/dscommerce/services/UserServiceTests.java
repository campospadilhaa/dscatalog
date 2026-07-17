package com.devsuperior.dscommerce.services;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscommerce.dto.UserDTO;
import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.projections.UserDetailsProjection;
import com.devsuperior.dscommerce.repositories.UserRepository;
import com.devsuperior.dscommerce.tests.UserDetailProjectionFactory;
import com.devsuperior.dscommerce.tests.UserFactory;
import com.devsuperior.dscommerce.util.CustomUserUtil;

@ExtendWith(SpringExtension.class)
public class UserServiceTests {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private CustomUserUtil customUserUtil;

	private String existingUsername;
	private String nonExistingUsername;

	private User user;

	private List<UserDetailsProjection> listaUserDetailsProjection;


	@BeforeEach
	void setUp() throws Exception {

		existingUsername = "maria@gmail.com";
		nonExistingUsername = "emailnaoexistente@gmail.com";

		user = UserFactory.createUserClient(1L, existingUsername);

		listaUserDetailsProjection = UserDetailProjectionFactory.createUserDetailProjectionClient(existingUsername);

		Mockito.when(userRepository.searchUserAndRolesByEmail(existingUsername)).thenReturn(listaUserDetailsProjection);
		Mockito.when(userRepository.searchUserAndRolesByEmail(nonExistingUsername)).thenReturn(new ArrayList<>());

		Mockito.when(userRepository.findByEmail(existingUsername)).thenReturn(Optional.of(user));
		Mockito.when(userRepository.findByEmail(nonExistingUsername)).thenReturn(Optional.empty());
	}

	@Test
	public void loadUserByUsernameDeveRetornarListaUserDetailsProjectionQuandoExistingUsername() {

		UserDetails UserDetailsResult = userService.loadUserByUsername(existingUsername);

		Assertions.assertNotNull(UserDetailsResult);
		Assertions.assertEquals(UserDetailsResult.getUsername(), existingUsername);
	}

	@Test
	public void loadUserByUsernameDeveRetornarUsernameNotFoundExceptionQuandoNonExistingUsername() {

		Assertions.assertThrows(UsernameNotFoundException.class, () -> {

			userService.loadUserByUsername(nonExistingUsername);
		});
	}

	@Test
	public void authenticatedDeveRetornarUserQuandoUserExists() {

		Mockito.when(customUserUtil.getLoggedUsername()).thenReturn(existingUsername);

		User userResult = userService.authenticated();

		Assertions.assertNotNull(userResult);
		Assertions.assertEquals(userResult.getUsername(), existingUsername);
	}

	@Test
	public void authenticatedDeveRetornarUsernameNotFoundExceptionQuandoNonExistingUsername() {

		Mockito.doThrow(ClassCastException.class).when(customUserUtil).getLoggedUsername();

		Assertions.assertThrows(UsernameNotFoundException.class, () ->
			{
				userService.authenticated();
			});
	}

	@Test
	public void meDeveRetornarUserDTOQuandoUserAuthenticated() {

		//Mockito.doReturn(user).when(userService).authenticated();

		UserService userServiceSpy = Mockito.spy(userService);
		Mockito.doReturn(user).when(userServiceSpy).authenticated();

		UserDTO userDTOresult = userServiceSpy.getMe();

		Assertions.assertNotNull(userDTOresult);
		Assertions.assertEquals(userDTOresult.getEmail(), existingUsername);
	}

	@Test
	public void meDeveRetornarUsernameNotFoundExceptionQuandoUserNotAuthenticated() {

		UserService userServiceSpy = Mockito.spy(userService);
		Mockito.doThrow(UsernameNotFoundException.class).when(userServiceSpy).authenticated();

		Assertions.assertThrows(UsernameNotFoundException.class, () ->
			{
				@SuppressWarnings("unused")
				UserDTO userDTOresult = userServiceSpy.getMe();
			});
	}
}