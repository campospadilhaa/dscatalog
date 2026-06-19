package com.devsuperior.demo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.devsuperior.demo.entities.Role;
import com.devsuperior.demo.entities.User;
import com.devsuperior.demo.projections.UserDetailsProjection;
import com.devsuperior.demo.repositories.UserRepository;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		List<UserDetailsProjection> listaUserDetailsProjection = userRepository.searchUserAndRolesByEmail(username);

		if(listaUserDetailsProjection == null || listaUserDetailsProjection.isEmpty()) {
			throw new UsernameNotFoundException("Usuário não encontrado");
		}

		User user = new User();
		user.setEmail(username);
		user.setPassword(listaUserDetailsProjection.get(0).getPassword());

		for (UserDetailsProjection userDetailsProjection : listaUserDetailsProjection) {
			user.addRole(new Role(userDetailsProjection.getRoleId(), userDetailsProjection.getAuthority()));
		}

		return user;
	}
}