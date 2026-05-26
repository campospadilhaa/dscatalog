package com.campospadilhaa.dscatalog.dto;

// necessário criar a classe UserUpdateDTO porque UserInsertDTO herda de UserDTO
// então, a herança herdaria indevidamente também a annotation @@UserUpdateValid 

import com.campospadilhaa.dscatalog.services.validation.UserUpdateValid;

@UserUpdateValid // utilizando a anotation criada para validar o e-mail
public class UserUpdateDTO extends UserDTO {

	private static final long serialVersionUID = 1L;

}