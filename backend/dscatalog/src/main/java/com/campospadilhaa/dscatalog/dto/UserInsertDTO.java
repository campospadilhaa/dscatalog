package com.campospadilhaa.dscatalog.dto;

import com.campospadilhaa.dscatalog.services.validation.UserInsertValid;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@UserInsertValid // utilizando a anotation criada para validar o e-mail
public class UserInsertDTO extends UserDTO {

	private static final long serialVersionUID = 1L;

	@NotBlank(message = "Campo obrigatório")
	@Size(min = 8, message = "Deve ter no mínimo 8 caracteres")
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