package com.campospadilhaa.dscatalog.dto;

import com.campospadilhaa.dscatalog.services.validation.UserInsertValid;

@UserInsertValid // utilizando a anotation criada para validar o e-mail
public class UserInsertDTO extends UserDTO {

	private static final long serialVersionUID = 1L;

	private String password;

	private UserInsertDTO() {
		super();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}