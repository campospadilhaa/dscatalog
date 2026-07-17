package com.devsuperior.dsmovie.services;

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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;

import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.projections.UserDetailsProjection;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.tests.UserDetailsFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import com.devsuperior.dsmovie.utils.CustomUserUtil;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration
public class UserServiceTests {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private CustomUserUtil customUserUtil;

	private String existingUsername;
	private String nonExistingUsername;

	private UserEntity userEntity;

	@BeforeEach
	void setUp() throws Exception {

		existingUsername = "maria@gmail.com";
		nonExistingUsername = "emailnaoexistente@gmail.com";

		userEntity = UserFactory.createUserEntity();
	}

	@Test
	public void authenticatedShouldReturnUserEntityWhenUserExists() {

		Mockito.when(userRepository.findByUsername(existingUsername)).thenReturn(Optional.of(userEntity));

		Mockito.when(customUserUtil.getLoggedUsername()).thenReturn(existingUsername);

		UserEntity userResult = userService.authenticated();

		Assertions.assertNotNull(userResult);
		Assertions.assertEquals(userResult.getUsername(), existingUsername);
	}

	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {

		Mockito.doThrow(ClassCastException.class).when(customUserUtil).getLoggedUsername();

		Assertions.assertThrows(UsernameNotFoundException.class, () ->
			{
				userService.authenticated();
			}
		);
	}

	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {

		List<UserDetailsProjection> listaUserDetailsProjection = UserDetailsFactory.createCustomClientUser(existingUsername);

		Mockito.when(userRepository.searchUserAndRolesByUsername(existingUsername)).thenReturn(listaUserDetailsProjection);

		UserDetails UserDetailsResult = userService.loadUserByUsername(existingUsername);

		Assertions.assertNotNull(UserDetailsResult);
		Assertions.assertEquals(UserDetailsResult.getUsername(), existingUsername);
	}

	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {

		Mockito.when(userRepository.searchUserAndRolesByUsername(nonExistingUsername)).thenReturn(new ArrayList<>());

		Assertions.assertThrows(UsernameNotFoundException.class, () ->
			{
				userService.loadUserByUsername(nonExistingUsername);
			}
		);
	}
}