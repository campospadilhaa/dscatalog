package com.devsuperior.dscommerce.tests;

import java.util.ArrayList;
import java.util.List;

import com.devsuperior.dscommerce.projections.UserDetailsProjection;

public class UserDetailProjectionFactory {

	public static List<UserDetailsProjection> createUserDetailProjectionClient(String username) {

		List<UserDetailsProjection> listaUserDetailsProjection = new ArrayList<>();
			listaUserDetailsProjection.add(new UserDetailImpl(username, "123456", 1L, "ROLE_CLIENT"));

		return listaUserDetailsProjection;
	}

	public static List<UserDetailsProjection> createUserDetailProjectionAdmin(String username) {

		List<UserDetailsProjection> listaUserDetailsProjection = new ArrayList<>();
			listaUserDetailsProjection.add(new UserDetailImpl(username, "123456", 2L, "ROLE_ADMIN"));

		return listaUserDetailsProjection;
	}

	public static List<UserDetailsProjection> createUserDetailProjectionClientAdmin(String username) {

		List<UserDetailsProjection> listaUserDetailsProjection = new ArrayList<>();
			listaUserDetailsProjection.add(new UserDetailImpl(username, "123456", 1L, "ROLE_CLIENT"));
			listaUserDetailsProjection.add(new UserDetailImpl(username, "123456", 2L, "ROLE_ADMIN"));

		return listaUserDetailsProjection;
	}
}

class UserDetailImpl implements UserDetailsProjection {

	private String username;
	private String password;
	private Long roleId;
	private String authority;	
	
	public UserDetailImpl() {

	}

	public UserDetailImpl(String username, String password, Long roleId, String authority) {

		this.username = username;
		this.password = password;
		this.roleId = roleId;
		this.authority = authority;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public Long getRoleId() {
		return roleId;
	}

	@Override
	public String getAuthority() {
		return authority;
	}
}